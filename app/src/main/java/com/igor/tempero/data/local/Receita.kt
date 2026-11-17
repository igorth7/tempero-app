package com.igor.tempero.data.local

import androidx.room3.Entity
import androidx.room3.PrimaryKey

/**
 * ENTIDADE 1 — Tabela "receitas" no Room.
 * É o dado principal do app: o "caderno de receitas" do usuário.
 */
@Entity(tableName = "receitas")
data class Receita(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val categoria: String,
    val tempoMinutos: Int,
    val porcoes: Int,
    val ingredientes: String,          // um ingrediente por linha
    val modoPreparo: String,
    val favorita: Boolean = false,
    val origem: String = "Minha receita" // "Minha receita" ou "TheMealDB (...)"
) {
    /** Transforma o texto de ingredientes em lista (uma linha = um ingrediente). */
    fun listaIngredientes(): List<String> =
        ingredientes.lines().map { it.trim() }.filter { it.isNotEmpty() }
}
