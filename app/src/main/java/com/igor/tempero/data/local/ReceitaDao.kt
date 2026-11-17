package com.igor.tempero.data.local

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

/** DAO das receitas — CRUD completo. */
@Dao
interface ReceitaDao {

    // C — Create
    @Insert
    suspend fun inserir(receita: Receita)

    // R — Read (via Flow: o Room reemite a lista sempre que a tabela muda)
    @Query("SELECT * FROM receitas ORDER BY nome ASC")
    fun listarTodas(): Flow<List<Receita>>

    // U — Update
    @Update
    suspend fun atualizar(receita: Receita)

    // D — Delete
    @Delete
    suspend fun deletar(receita: Receita)
}
