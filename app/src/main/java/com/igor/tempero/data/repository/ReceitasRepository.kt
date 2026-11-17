package com.igor.tempero.data.repository

import android.util.Log
import com.igor.tempero.data.local.Receita
import com.igor.tempero.data.local.ReceitaDao
import com.igor.tempero.data.remote.MealApi
import com.igor.tempero.data.remote.ReceitaRemota
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException

/**
 * Repository de receitas: a ÚNICA porta de entrada para os dados de receitas.
 * Fala com duas fontes: o Room (ReceitaDao) e a internet (MealApi / Retrofit).
 *
 * Toda falha é tratada aqui com try-catch e devolvida como Result.failure,
 * com uma mensagem amigável que o ViewModel coloca no UiState.
 */
class ReceitasRepository(
    private val receitaDao: ReceitaDao,
    private val api: MealApi
) {

    // ---------- Room ----------

    /** Lista reativa: emite de novo toda vez que a tabela muda. */
    fun observarReceitas(): Flow<Result<List<Receita>>> =
        receitaDao.listarTodas()
            .map { lista -> Result.success(lista) }
            .catch { erro ->
                Log.e(TAG, "Erro ao ler receitas", erro)
                emit(Result.failure(Exception("Não foi possível carregar as receitas.")))
            }

    suspend fun inserir(receita: Receita): Result<Unit> =
        executar("Não foi possível salvar a receita.") { receitaDao.inserir(receita) }

    suspend fun atualizar(receita: Receita): Result<Unit> =
        executar("Não foi possível atualizar a receita.") { receitaDao.atualizar(receita) }

    suspend fun deletar(receita: Receita): Result<Unit> =
        executar("Não foi possível excluir a receita.") { receitaDao.deletar(receita) }

    // ---------- Retrofit (API externa) ----------

    suspend fun buscarNaInternet(termo: String): Result<List<ReceitaRemota>> {
        return try {
            val resposta = api.buscarReceitas(termo)
            val receitas = resposta.meals.orEmpty().map { meal -> converter(meal) }
            Result.success(receitas)
        } catch (e: IOException) {
            Log.e(TAG, "Sem internet", e)
            Result.failure(Exception("Sem conexão com a internet. Verifique e tente de novo."))
        } catch (e: HttpException) {
            Log.e(TAG, "Erro HTTP", e)
            Result.failure(Exception("O servidor de receitas respondeu com erro ${e.code()}."))
        } catch (e: Exception) {
            Log.e(TAG, "Erro inesperado na API", e)
            Result.failure(Exception("Erro inesperado ao buscar receitas."))
        }
    }

    /** Converte o JSON da API (mapa de chaves) para o nosso modelo ReceitaRemota. */
    private fun converter(meal: Map<String, String?>): ReceitaRemota {
        val ingredientes = (1..20).mapNotNull { i ->
            val nome = meal["strIngredient$i"]?.trim()
            val medida = meal["strMeasure$i"]?.trim().orEmpty()
            if (nome.isNullOrBlank()) null else "$medida $nome".trim()
        }
        return ReceitaRemota(
            idExterno = meal["idMeal"].orEmpty(),
            nome = meal["strMeal"].orEmpty(),
            categoria = meal["strCategory"] ?: "Outros",
            origem = meal["strArea"] ?: "Internacional",
            ingredientes = ingredientes,
            instrucoes = meal["strInstructions"].orEmpty()
        )
    }

    /** Executa uma operação no banco dentro de um try-catch e devolve um Result. */
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
        private const val TAG = "ReceitasRepository"
    }
}
