package com.igor.tempero.ui.receitas

import com.igor.tempero.data.local.Receita

enum class Ordenacao(val rotulo: String) {
    NOME("A-Z"),
    TEMPO("Mais rápidas"),
    FAVORITAS("Favoritas")
}

/** Estado da tela de receitas: UMA fonte de verdade para Início, Detalhe e Formulário. */
data class ReceitasUiState(
    val todas: List<Receita> = emptyList(),      // o que veio do Room
    val visiveis: List<Receita> = emptyList(),   // depois de filtros e ordenação
    val busca: String = "",
    val categoriaSelecionada: String = TODAS,
    val ordenacao: Ordenacao = Ordenacao.NOME,
    val carregando: Boolean = true,
    val erro: String? = null,
    val mensagem: String? = null,                // usada para o Toast
    val form: FormReceita = FormReceita()
) {
    /** Categorias calculadas a partir das receitas salvas (map + distinct + sorted). */
    val categorias: List<String>
        get() = listOf(TODAS) + todas.map { it.categoria }.distinct().sorted()

    companion object {
        const val TODAS = "Todas"
    }
}

/** Campos do formulário de nova receita / edição. */
data class FormReceita(
    val emEdicao: Receita? = null,
    val nome: String = "",
    val categoria: String = "",
    val tempo: String = "",
    val porcoes: String = "",
    val ingredientes: String = "",
    val modoPreparo: String = "",
    val erro: String? = null
) {
    val titulo: String get() = if (emEdicao == null) "Nova receita" else "Editar receita"
    val textoBotao: String get() = if (emEdicao == null) "Salvar receita" else "Atualizar receita"
}
