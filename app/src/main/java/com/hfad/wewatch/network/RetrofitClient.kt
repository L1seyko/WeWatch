package com.hfad.wewatch.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    const val API_KEY = "4c09d831"
    const val TMDB_BASE_URL = "https://www.omdbapi.com/"
    const val TMDB_IMAGEURL = ""

    val retrofit: RetrofitInterface by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(TMDB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(RetrofitInterface::class.java)
    }
}