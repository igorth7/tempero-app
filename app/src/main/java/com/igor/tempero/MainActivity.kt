package com.igor.tempero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.igor.tempero.data.local.AppDatabase
import com.igor.tempero.data.remote.RetrofitClient
import com.igor.tempero.data.repository.ListaComprasRepository
import com.igor.tempero.data.repository.ReceitasRepository
import com.igor.tempero.ui.AppViewModelFactory
import com.igor.tempero.ui.navigation.AppNavHost
import com.igor.tempero.ui.theme.TemperoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Monta as dependências uma vez: Banco -> DAOs -> Repositories -> Factory
        val banco = AppDatabase.getDatabase(applicationContext)
        val receitasRepository = ReceitasRepository(banco.receitaDao(), RetrofitClient.api)
        val listaComprasRepository = ListaComprasRepository(banco.itemCompraDao())
        val factory = AppViewModelFactory(receitasRepository, listaComprasRepository)

        // Nenhum banco, nenhuma regra de negócio aqui: só chama a navegação
        setContent {
            TemperoTheme {
                AppNavHost(factory = factory)
            }
        }
    }
}
