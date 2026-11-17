package com.igor.tempero.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface do Retrofit para a API pública TheMealDB.
 * Exemplo: https://www.themealdb.com/api/json/v1/1/search.php?s=chicken
 */
interface MealApi {

    @GET("search.php")
    suspend fun buscarReceitas(@Query("s") termo: String): MealsResponse
}

/**
 * Resposta da API: {"meals": [ {...}, {...} ]}
 * Cada receita vem como um mapa (chave -> valor), porque a API manda
 * strIngredient1..strIngredient20 e strMeasure1..strMeasure20 soltos.
 * "meals" vem null quando nada é encontrado.
 */
data class MealsResponse(
    val meals: List<Map<String, String?>>?
)
