package com.igor.tempero.data.remote

/** Receita vinda da internet, já "limpa" para o app usar. */
data class ReceitaRemota(
    val idExterno: String,
    val nome: String,
    val categoria: String,
    val origem: String,
    val ingredientes: List<String>,
    val instrucoes: String
)
