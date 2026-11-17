package com.igor.tempero.ui.explorar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igor.tempero.data.local.Receita
import com.igor.tempero.data.remote.ReceitaRemota
import com.igor.tempero.data.repository.ReceitasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExplorarUiState(
    val termo: String = "",
    val resultados: List<ReceitaRemota> = emptyList(),
    val carregando: Boolean = false,
    val jaBuscou: Boolean = false,
    val erro: String? = null,
    val mensagem: String? = null
)

/** ViewModel da busca na internet (Retrofit / TheMealDB). */
class ExplorarViewModel(
    private val repository: ReceitasRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExplorarUiState())
    val uiState: StateFlow<ExplorarUiState> = _uiState.asStateFlow()

    fun onTermoChange(novoTermo: String) {
        _uiState.update { it.copy(termo = novoTermo, erro = null) }
    }

    fun buscar() {
        val termo = _uiState.value.termo.trim()
        if (termo.isEmpty()) {
            _uiState.update { it.copy(erro = "Digite algo para buscar.") }
            return
        }
        _uiState.update { it.copy(carregando = true, erro = null) }

        viewModelScope.launch {
            repository.buscarNaInternet(termo)
                .onSuccess { lista ->
                    _uiState.update {
                        it.copy(
                            resultados = lista.sortedBy { receita -> receita.nome },
                            carregando = false,
                            jaBuscou = true
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(resultados = emptyList(), carregando = false, jaBuscou = true, erro = e.message)
                    }
                }
        }
    }

    /** Importa uma receita da internet para o Room (Create). */
    fun salvarNoCaderno(remota: ReceitaRemota) {
        val receita = Receita(
            nome = remota.nome,
            categoria = traduzirCategoria(remota.categoria),
            tempoMinutos = 30,
            porcoes = 4,
            ingredientes = remota.ingredientes.joinToString("\n"),
            modoPreparo = remota.instrucoes,
            origem = "TheMealDB (${remota.origem})"
        )
        viewModelScope.launch {
            repository.inserir(receita)
                .onSuccess { _uiState.update { it.copy(mensagem = "\"${remota.nome}\" salva no seu caderno!") } }
                .onFailure { e -> _uiState.update { it.copy(erro = e.message) } }
        }
    }

    fun mensagemExibida() {
        _uiState.update { it.copy(mensagem = null) }
    }

    private fun traduzirCategoria(categoria: String): String =
        traducoes[categoria] ?: categoria

    companion object {
        private val traducoes = mapOf(
            "Dessert" to "Doces",
            "Chicken" to "Frango",
            "Beef" to "Carnes",
            "Pork" to "Carnes",
            "Lamb" to "Carnes",
            "Pasta" to "Massas",
            "Seafood" to "Frutos do mar",
            "Vegetarian" to "Vegetariana",
            "Vegan" to "Vegetariana",
            "Breakfast" to "Café da manhã",
            "Side" to "Acompanhamentos",
            "Starter" to "Entradas"
        )
    }
}
