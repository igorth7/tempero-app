package com.igor.tempero.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.igor.tempero.ui.AppViewModelFactory
import com.igor.tempero.ui.admin.AdminViewModel
import com.igor.tempero.ui.admin.TelaAdmin
import com.igor.tempero.ui.compras.ListaComprasViewModel
import com.igor.tempero.ui.compras.TelaListaCompras
import com.igor.tempero.ui.componentes.EstadoVazio
import com.igor.tempero.ui.explorar.ExplorarViewModel
import com.igor.tempero.ui.explorar.TelaExplorar
import com.igor.tempero.ui.login.LoginViewModel
import com.igor.tempero.ui.login.TelaLogin
import com.igor.tempero.ui.perfil.TelaPerfil
import com.igor.tempero.ui.receitas.ReceitasViewModel
import com.igor.tempero.ui.receitas.TelaDetalhe
import com.igor.tempero.ui.receitas.TelaFormReceita
import com.igor.tempero.ui.receitas.TelaInicio

/** Todas as rotas (destinos) do NavHost. */
object Rotas {
    const val LOGIN = "login"
    const val INICIO = "inicio"
    const val EXPLORAR = "explorar"
    const val COMPRAS = "compras"
    const val PERFIL = "perfil"
    const val ADMIN = "admin"
    const val DETALHE = "detalhe/{id}"
    const val FORMULARIO = "formulario"

    fun detalhe(id: Int) = "detalhe/$id"
}

/** Um botão da barra inferior. */
data class ItemMenu(val rota: String, val rotulo: String, val icone: String)

private val menuUsuario = listOf(
    ItemMenu(Rotas.INICIO, "Início", "🏠"),
    ItemMenu(Rotas.EXPLORAR, "Explorar", "🔎"),
    ItemMenu(Rotas.COMPRAS, "Compras", "🛒"),
    ItemMenu(Rotas.PERFIL, "Perfil", "👤")
)

@Composable
fun AppNavHost(factory: AppViewModelFactory) {
    val navController = rememberNavController()

    // Os ViewModels são criados uma vez (pela Factory) e compartilhados entre as telas
    val loginViewModel: LoginViewModel = viewModel(factory = factory)
    val receitasViewModel: ReceitasViewModel = viewModel(factory = factory)
    val explorarViewModel: ExplorarViewModel = viewModel(factory = factory)
    val listaComprasViewModel: ListaComprasViewModel = viewModel(factory = factory)
    val adminViewModel: AdminViewModel = viewModel(factory = factory)

    val login by loginViewModel.uiState.collectAsStateWithLifecycle()
    val receitas by receitasViewModel.uiState.collectAsStateWithLifecycle()
    val compras by listaComprasViewModel.uiState.collectAsStateWithLifecycle()

    // Controle de perfil: o Admin ganha uma aba a mais
    val itensMenu = if (login.isAdmin) menuUsuario + ItemMenu(Rotas.ADMIN, "Admin", "🛠️") else menuUsuario

    val entradaAtual by navController.currentBackStackEntryAsState()
    val rotaAtual = entradaAtual?.destination?.route
    val mostrarBarra = itensMenu.any { it.rota == rotaAtual }

    Scaffold(
        bottomBar = {
            if (mostrarBarra) {
                BarraInferior(itensMenu, rotaAtual) { rota -> navegarPelaBarra(navController, rota) }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Rotas.LOGIN,
            modifier = Modifier.padding(padding)
        ) {
            composable(Rotas.LOGIN) {
                TelaLogin(
                    viewModel = loginViewModel,
                    aoEntrar = {
                        navController.navigate(Rotas.INICIO) {
                            popUpTo(Rotas.LOGIN) { inclusive = true }
                        }
                    }
                )
            }

            composable(Rotas.INICIO) {
                TelaInicio(
                    viewModel = receitasViewModel,
                    nomeUsuario = login.nome.trim(),
                    aoAbrirReceita = { id -> navController.navigate(Rotas.detalhe(id)) },
                    aoNovaReceita = {
                        receitasViewModel.abrirFormulario(null)
                        navController.navigate(Rotas.FORMULARIO)
                    }
                )
            }

            composable(
                route = Rotas.DETALHE,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { entrada ->
                val id = entrada.arguments?.getInt("id") ?: -1
                TelaDetalhe(
                    receitaId = id,
                    viewModel = receitasViewModel,
                    listaComprasViewModel = listaComprasViewModel,
                    aoVoltar = { navController.popBackStack() },
                    aoEditar = { receita ->
                        receitasViewModel.abrirFormulario(receita)
                        navController.navigate(Rotas.FORMULARIO)
                    }
                )
            }

            composable(Rotas.FORMULARIO) {
                TelaFormReceita(
                    viewModel = receitasViewModel,
                    aoVoltar = { navController.popBackStack() }
                )
            }

            composable(Rotas.EXPLORAR) {
                TelaExplorar(viewModel = explorarViewModel)
            }

            composable(Rotas.COMPRAS) {
                TelaListaCompras(viewModel = listaComprasViewModel)
            }

            composable(Rotas.PERFIL) {
                TelaPerfil(
                    nome = login.nome.trim(),
                    isAdmin = login.isAdmin,
                    receitas = receitas.todas,
                    totalItensCompra = compras.itens.size,
                    aoSair = {
                        loginViewModel.sair()
                        navController.navigate(Rotas.LOGIN) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }

            composable(Rotas.ADMIN) {
                // Proteção extra: só o Admin enxerga o painel
                if (login.isAdmin) {
                    TelaAdmin(viewModel = adminViewModel)
                } else {
                    Column(Modifier.fillMaxSize()) {
                        EstadoVazio("🔒", "Acesso restrito", "Somente administradores podem ver esta tela.")
                    }
                }
            }
        }
    }
}

@Composable
private fun BarraInferior(itens: List<ItemMenu>, rotaAtual: String?, aoNavegar: (String) -> Unit) {
    NavigationBar {
        itens.forEach { item ->
            NavigationBarItem(
                selected = rotaAtual == item.rota,
                onClick = { aoNavegar(item.rota) },
                icon = { Text(item.icone, fontSize = 20.sp) },
                label = { Text(item.rotulo) }
            )
        }
    }
}

/** Navegação da barra: não empilha várias cópias da mesma aba. */
private fun navegarPelaBarra(navController: NavHostController, rota: String) {
    navController.navigate(rota) {
        popUpTo(Rotas.INICIO) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
