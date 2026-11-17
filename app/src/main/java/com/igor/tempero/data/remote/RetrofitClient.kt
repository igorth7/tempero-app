package com.igor.tempero.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Cria (uma única vez) o cliente Retrofit apontando para a TheMealDB. */
object RetrofitClient {

    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

    val api: MealApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // JSON -> objetos Kotlin
            .build()
            .create(MealApi::class.java)
    }
}
