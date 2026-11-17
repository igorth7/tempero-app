package com.igor.tempero.data.local

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

/** DAO da lista de compras — CRUD completo. */
@Dao
interface ItemCompraDao {

    @Insert
    suspend fun inserir(item: ItemCompra)

    @Query("SELECT * FROM itens_compra")
    fun listarTodos(): Flow<List<ItemCompra>>

    @Update
    suspend fun atualizar(item: ItemCompra)

    @Delete
    suspend fun deletar(item: ItemCompra)

    @Query("DELETE FROM itens_compra WHERE comprado = 1")
    suspend fun limparComprados()
}
