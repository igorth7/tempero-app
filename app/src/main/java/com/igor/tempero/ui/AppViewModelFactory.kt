package com.igor.tempero.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.igor.tempero.data.repository.ListaComprasRepository
import com.igor.tempero.data.repository.ReceitasRepository
import com.igor.tempero.ui.admin.AdminViewModel
import com.igor.tempero.ui.compras.ListaComprasViewModel
import com.igor.tempero.ui.explorar.ExplorarViewModel
import com.igor.tempero.ui.login.LoginViewModel
import com.igor.tempero.ui.receitas.ReceitasViewModel

/**
 * Factory: o "manual de instruções" que ensina o Android a criar
 * os ViewModels que recebem um Repository no construtor (injeção de dependência).
 */
class AppViewModelFactory(
    private val receitasRepository: ReceitasRepository,
    private val listaComprasRepository: ListaComprasRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                LoginViewModel()
            modelClass.isAssignableFrom(ReceitasViewModel::class.java) ->
                ReceitasViewModel(receitasRepository)
            modelClass.isAssignableFrom(ExplorarViewModel::class.java) ->
                ExplorarViewModel(receitasRepository)
            modelClass.isAssignableFrom(ListaComprasViewModel::class.java) ->
                ListaComprasViewModel(listaComprasRepository)
            modelClass.isAssignableFrom(AdminViewModel::class.java) ->
                AdminViewModel(receitasRepository)
            else -> throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
        } as T
    }
}
