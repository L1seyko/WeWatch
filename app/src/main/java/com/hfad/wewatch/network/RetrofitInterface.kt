package com.hfad.wewatch.network


import com.hfad.wewatch.model.TmdbResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.*

interface RetrofitInterface {
    @GET("/")
    suspend fun searchMovie(
        @Query("apikey") apiKey: String,
        @Query("s") query: String
    ): TmdbResponse
}