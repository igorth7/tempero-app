package com.igor.tempero.ui.compras

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igor.tempero.data.local.ItemCompra
import com.igor.tempero.data.repository.ListaComprasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ListaComprasUiState(
    val itens: List<ItemCompra> = emptyList(),
    val novoItem: String = "",
    val erro: String? = null,
    val mensagem: String? = null
) {
    val totalComprados: Int get() = itens.count { it.comprado }
    val progresso: Float get() = if (itens.isEmpty()) 0f else totalComprados.toFloat() / itens.size
}

/** ViewModel da lista de compras (Entidade 2 — CRUD completo). */
class ListaComprasViewModel(
    private val repository: ListaComprasRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListaComprasUiState())
    val uiState: StateFlow<ListaComprasUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observarItens().collect { resultado ->
                resultado
                    .onSuccess { lista ->
                        // Pendentes primeiro, depois em ordem alfabética
                        val ordenada = lista.sortedWith(compareBy<ItemCompra>({ it.comprado }, { it.nome.lowercase() }))
                        _uiState.update { it.copy(itens = ordenada, erro = null) }
                    }
                    .onFailure { e -> _uiState.update { it.copy(erro = e.message) } }
            }
        }
    }

    fun onNovoItemChange(texto: String) {
        _uiState.update { it.copy(novoItem = texto, erro = null) }
    }

    fun adicionar() {
        val nome = _uiState.value.novoItem.trim()
        if (nome.isEmpty()) {
            _uiState.update { it.copy(erro = "Digite o nome do item.") }
            return
        }
        viewModelScope.launch {
            repository.inserir(ItemCompra(nome = nome))
                .onSuccess { _uiState.update { it.copy(novoItem = "") } }
                .onFailure { e -> _uiState.update { it.copy(erro = e.message) } }
        }
    }

    /** Usado pela tela de Detalhe: manda os ingredientes de uma receita para a lista. */
    fun adicionarIngredientes(ingredientes: List<String>) {
        val jaNaLista = _uiState.value.itens.map { it.nome.lowercase() }
        val novos = ingredientes.filter { it.lowercase() !in jaNaLista }
        viewModelScope.launch {
            novos.forEach { nome -> repository.inserir(ItemCompra(nome = nome)) }
            _uiState.update { it.copy(mensagem = "${novos.size} ingrediente(s) adicionados à lista de compras.") }
        }
    }

    fun alternarComprado(item: ItemCompra) {
        viewModelScope.launch {
            repository.atualizar(item.copy(comprado = !item.comprado))
                .onFailure { e -> _uiState.update { it.copy(erro = e.message) } }
        }
    }

    fun remover(item: ItemCompra) {
        viewModelScope.launch {
            repository.deletar(item)
                .onFailure { e -> _uiState.update { it.copy(erro = e.message) } }
        }
    }

    fun limparComprados() {
        viewModelScope.launch {
            repository.limparComprados()
                .onSuccess { _uiState.update { it.copy(mensagem = "Itens comprados removidos.") } }
                .onFailure { e -> _uiState.update { it.copy(erro = e.message) } }
        }
    }

    fun mensagemExibida() {
        _uiState.update { it.copy(mensagem = null) }
    }
}
