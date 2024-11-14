package com.example.menulateral.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {  // Establecer la esto la URL pública de la API
    private const val BASE_URL = "https://2flmzw13-7267.use2.devtunnels.ms/apiolap/ "// url Api en app Nativa "https://8981hht6-7267.use2.devtunnels.ms/apiolap/"//"https://6jbkzlc5-7267.usw2.devtunnels.ms/apiolap/" // "https://2flmzw13-7267.use2.devtunnels.ms/apiolap/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
