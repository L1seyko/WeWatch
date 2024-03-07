package com.hfad.wewatch.model

import android.util.Log
import com.hfad.wewatch.network.RetrofitClient
import com.hfad.wewatch.network.RetrofitClient.retrofit
import com.hfad.wewatch.network.RetrofitInterface
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RemoteDataSource {
    private val TAG = "RemoteDataSource"

    suspend fun searchResultsObservable(query: String): TmdbResponse {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "search/movie")
                retrofit.searchMovie(RetrofitClient.API_KEY, query)
            } catch (e: Exception) {
                Log.e(TAG, "Error: $e")
                throw e
            }
        }
    }
}
