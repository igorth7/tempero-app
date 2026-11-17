package com.igor.tempero.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igor.tempero.data.local.Receita
import com.igor.tempero.data.local.receitasExemplo
import com.igor.tempero.data.repository.ReceitasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class AdminUiState(
    val receitas: List<Receita> = emptyList(),
    val totalReceitas: Int = 0,
    val totalFavoritas: Int = 0,
    val tempoMedio: Int = 0,
    val porCategoria: List<Pair<String, Int>> = emptyList(),
    val erro: String? = null,
    val mensagem: String? = null
)

/** ViewModel do Painel do Administrador: relatórios e gerenciamento da base. */
class AdminViewModel(
    private val repository: ReceitasRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observarReceitas().collect { resultado ->
                resultado
                    .onSuccess { lista -> _uiState.update { calcularRelatorio(it, lista) } }
                    .onFailure { e -> _uiState.update { it.copy(erro = e.message) } }
            }
        }
    }

    /** Estatísticas com map, count, average, groupBy e sortedByDescending. */
    private fun calcularRelatorio(estado: AdminUiState, lista: List<Receita>): AdminUiState {
        val tempoMedio = if (lista.isEmpty()) 0 else lista.map { it.tempoMinutos }.average().roundToInt()
        val porCategoria = lista
            .groupBy { it.categoria }
            .map { (categoria, receitas) -> categoria to receitas.size }
            .sortedByDescending { it.second }

        return estado.copy(
            receitas = lista.sortedByDescending { it.id },
            totalReceitas = lista.size,
            totalFavoritas = lista.count { it.favorita },
            tempoMedio = tempoMedio,
            porCategoria = porCategoria,
            erro = null
        )
    }

    fun excluir(receita: Receita) {
        viewModelScope.launch {
            repository.deletar(receita)
                .onSuccess { _uiState.update { it.copy(mensagem = "\"${receita.nome}\" removida da base.") } }
                .onFailure { e -> _uiState.update { it.copy(erro = e.message) } }
        }
    }

    fun carregarExemplos() {
        viewModelScope.launch {
            val nomesExistentes = _uiState.value.receitas.map { it.nome }
            val novas = receitasExemplo.filter { it.nome !in nomesExistentes }
            novas.forEach { repository.inserir(it) }
            _uiState.update { it.copy(mensagem = "${novas.size} receita(s) de exemplo carregadas.") }
        }
    }

    fun mensagemExibida() {
        _uiState.update { it.copy(mensagem = null) }
    }
}
