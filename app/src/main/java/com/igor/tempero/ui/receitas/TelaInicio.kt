package com.igor.tempero.ui.receitas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.igor.tempero.ui.componentes.CardReceita
import com.igor.tempero.ui.componentes.EstadoVazio
import com.igor.tempero.ui.componentes.MensagemErro
import com.igor.tempero.ui.componentes.ToastDeMensagem
import com.igor.tempero.ui.theme.CinzaTexto
import com.igor.tempero.ui.theme.Marrom

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaInicio(
    viewModel: ReceitasViewModel,
    nomeUsuario: String,
    aoAbrirReceita: (Int) -> Unit,
    aoNovaReceita: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ToastDeMensagem(uiState.mensagem) { viewModel.mensagemExibida() }

    // Box: a lista fica por baixo e o botão "+ Nova receita" flutua por cima
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column {
                    Text("Olá, $nomeUsuario! 👋", fontSize = 15.sp, color = CinzaTexto)
                    Text("O que vamos cozinhar hoje?", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Marrom)
                }
            }
            item {
                OutlinedTextField(
                    value = uiState.busca,
                    onValueChange = { viewModel.onBuscaChange(it) },
                    placeholder = { Text("🔍 Buscar por nome ou ingrediente") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                // Categorias (LazyRow com scroll horizontal)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.categorias) { categoria ->
                        FilterChip(
                            selected = categoria == uiState.categoriaSelecionada,
                            onClick = { viewModel.onCategoriaChange(categoria) },
                            label = { Text(categoria) }
                        )
                    }
                }
            }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item { Text("Ordenar:", color = CinzaTexto, fontSize = 13.sp) }
                    items(Ordenacao.entries) { ordenacao ->
                        FilterChip(
                            selected = ordenacao == uiState.ordenacao,
                            onClick = { viewModel.onOrdenacaoChange(ordenacao) },
                            label = { Text(ordenacao.rotulo) }
                        )
                    }
                }
            }
            item { MensagemErro(uiState.erro) }

            when {
                uiState.carregando -> item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.todas.isEmpty() -> item {
                    EstadoVazio("📖", "Seu caderno está vazio", "Toque em + Nova receita ou importe receitas na aba Explorar.")
                }
                uiState.visiveis.isEmpty() -> item {
                    EstadoVazio("🔎", "Nada encontrado", "Tente outra busca ou categoria.")
                }
                else -> {
                    item {
                        Text("${uiState.visiveis.size} receita(s)", fontSize = 13.sp, color = CinzaTexto)
                    }
                    items(uiState.visiveis, key = { it.id }) { receita ->
                        CardReceita(
                            receita = receita,
                            aoClicar = { aoAbrirReceita(receita.id) },
                            aoFavoritar = { viewModel.alternarFavorita(receita) }
                        )
                    }
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = aoNovaReceita,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Text("+ Nova receita", fontWeight = FontWeight.SemiBold)
        }
    }
}
