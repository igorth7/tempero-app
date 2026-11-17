package com.igor.tempero.ui.receitas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.igor.tempero.data.local.Receita
import com.igor.tempero.data.repository.ReceitasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** ViewModel do caderno de receitas do usuário (lista, detalhe e formulário). */
class ReceitasViewModel(
    private val repository: ReceitasRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReceitasUiState())
    val uiState: StateFlow<ReceitasUiState> = _uiState.asStateFlow()

    init {
        // Inscreve UMA vez no Flow do Room: a lista se atualiza sozinha
        viewModelScope.launch {
            repository.observarReceitas().collect { resultado ->
                resultado
                    .onSuccess { lista ->
                        _uiState.update { it.copy(todas = lista, carregando = false, erro = null) }
                        aplicarFiltros()
                    }
                    .onFailure { e ->
                        _uiState.update { it.copy(carregando = false, erro = e.message) }
                    }
            }
        }
    }

    // ---------- Filtros e ordenação ----------

    fun onBuscaChange(texto: String) {
        _uiState.update { it.copy(busca = texto) }
        aplicarFiltros()
    }

    fun onCategoriaChange(categoria: String) {
        _uiState.update { it.copy(categoriaSelecionada = categoria) }
        aplicarFiltros()
    }

    fun onOrdenacaoChange(ordenacao: Ordenacao) {
        _uiState.update { it.copy(ordenacao = ordenacao) }
        aplicarFiltros()
    }

    /** Lógica de negócio: filter + sortedBy com lambdas sobre a lista do Flow. */
    private fun aplicarFiltros() {
        _uiState.update { estado ->
            val termo = estado.busca.trim().lowercase()

            val filtradas = estado.todas
                .filter { receita ->
                    estado.categoriaSelecionada == ReceitasUiState.TODAS ||
                        receita.categoria == estado.categoriaSelecionada
                }
                .filter { receita ->
                    termo.isEmpty() ||
                        receita.nome.lowercase().contains(termo) ||
                        receita.ingredientes.lowercase().contains(termo)
                }

            val ordenadas = when (estado.ordenacao) {
                Ordenacao.NOME -> filtradas.sortedBy { it.nome.lowercase() }
                Ordenacao.TEMPO -> filtradas.sortedBy { it.tempoMinutos }
                Ordenacao.FAVORITAS -> filtradas.sortedByDescending { it.favorita }
            }

            estado.copy(visiveis = ordenadas)
        }
    }

    // ---------- Ações sobre uma receita ----------

    fun alternarFavorita(receita: Receita) {
        viewModelScope.launch {
            repository.atualizar(receita.copy(favorita = !receita.favorita))
                .onFailure { e -> _uiState.update { it.copy(erro = e.message) } }
        }
    }

    fun excluir(receita: Receita) {
        viewModelScope.launch {
            repository.deletar(receita)
                .onSuccess { _uiState.update { it.copy(mensagem = "\"${receita.nome}\" excluída.") } }
                .onFailure { e -> _uiState.update { it.copy(erro = e.message) } }
        }
    }

    // ---------- Formulário (Create / Update) ----------

    /** Prepara o formulário: vazio (nova receita) ou preenchido (edição). */
    fun abrirFormulario(receita: Receita?) {
        val form = if (receita == null) {
            FormReceita()
        } else {
            FormReceita(
                emEdicao = receita,
                nome = receita.nome,
                categoria = receita.categoria,
                tempo = receita.tempoMinutos.toString(),
                porcoes = receita.porcoes.toString(),
                ingredientes = receita.ingredientes,
                modoPreparo = receita.modoPreparo
            )
        }
        _uiState.update { it.copy(form = form) }
    }

    /** A tela manda o formulário inteiro já alterado (ex: form.copy(nome = novoTexto)). */
    fun onFormChange(novoForm: FormReceita) {
        _uiState.update { it.copy(form = novoForm.copy(erro = null)) }
    }

    fun salvar(aoSalvar: () -> Unit) {
        val form = _uiState.value.form
        val tempo = form.tempo.toIntOrNull()
        val porcoes = form.porcoes.toIntOrNull()

        // Validação (regra de negócio no ViewModel, não na tela)
        val erro = when {
            form.nome.isBlank() -> "Informe o nome da receita."
            form.categoria.isBlank() -> "Escolha uma categoria."
            tempo == null || tempo <= 0 -> "Informe o tempo em minutos."
            porcoes == null || porcoes <= 0 -> "Informe o número de porções."
            form.ingredientes.isBlank() -> "Adicione pelo menos um ingrediente."
            else -> null
        }
        if (erro != null) {
            _uiState.update { it.copy(form = form.copy(erro = erro)) }
            return
        }

        val receita = form.emEdicao?.copy(
            nome = form.nome.trim(),
            categoria = form.categoria.trim(),
            tempoMinutos = tempo!!,
            porcoes = porcoes!!,
            ingredientes = form.ingredientes.trim(),
            modoPreparo = form.modoPreparo.trim()
        ) ?: Receita(
            nome = form.nome.trim(),
            categoria = form.categoria.trim(),
            tempoMinutos = tempo!!,
            porcoes = porcoes!!,
            ingredientes = form.ingredientes.trim(),
            modoPreparo = form.modoPreparo.trim()
        )

        viewModelScope.launch {
            val resultado = if (form.emEdicao == null) {
                repository.inserir(receita)
            } else {
                repository.atualizar(receita)
            }
            resultado
                .onSuccess {
                    val texto = if (form.emEdicao == null) "Receita salva!" else "Receita atualizada!"
                    _uiState.update { it.copy(form = FormReceita(), mensagem = texto) }
                    aoSalvar()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(form = form.copy(erro = e.message)) }
                }
        }
    }

    fun mensagemExibida() {
        _uiState.update { it.copy(mensagem = null) }
    }
}
