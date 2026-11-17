package com.igor.tempero.data.local

import androidx.room3.Entity
import androidx.room3.PrimaryKey

/**
 * ENTIDADE 2 — Tabela "itens_compra" no Room.
 * Guarda a lista de compras do usuário (com o check de "já comprei").
 */
@Entity(tableName = "itens_compra")
data class ItemCompra(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val comprado: Boolean = false
)
