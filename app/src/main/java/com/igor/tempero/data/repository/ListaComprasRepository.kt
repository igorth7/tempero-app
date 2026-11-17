package com.igor.tempero.data.repository

import android.util.Log
import com.igor.tempero.data.local.ItemCompra
import com.igor.tempero.data.local.ItemCompraDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/** Repository da lista de compras (Entidade 2), com tratamento de erros. */
class ListaComprasRepository(private val itemCompraDao: ItemCompraDao) {

    fun observarItens(): Flow<Result<List<ItemCompra>>> =
        itemCompraDao.listarTodos()
            .map { lista -> Result.success(lista) }
            .catch { erro ->
                Log.e(TAG, "Erro ao ler lista de compras", erro)
                emit(Result.failure(Exception("Não foi possível carregar a lista de compras.")))
            }

    suspend fun inserir(item: ItemCompra): Result<Unit> =
        executar("Não foi possível adicionar o item.") { itemCompraDao.inserir(item) }

    suspend fun atualizar(item: ItemCompra): Result<Unit> =
        executar("Não foi possível atualizar o item.") { itemCompraDao.atualizar(item) }

    suspend fun deletar(item: ItemCompra): Result<Unit> =
        executar("Não foi possível remover o item.") { itemCompraDao.deletar(item) }

    suspend fun limparComprados(): Result<Unit> =
        executar("Não foi possível limpar os itens comprados.") { itemCompraDao.limparComprados() }

    private suspend fun executar(mensagemErro: String, operacao: suspend () -> Unit): Result<Unit> {
        return try {
            operacao()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, mensagemErro, e)
            Result.failure(Exception(mensagemErro))
        }
    }

    companion object {
        private const val TAG = "ListaComprasRepository"
    }
}
