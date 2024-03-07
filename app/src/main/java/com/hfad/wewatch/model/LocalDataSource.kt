package com.hfad.wewatch.model

import android.app.Application
import android.database.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread


class LocalDataSource(application: Application) {

    private val movieDao: MovieDao
    val allMovies: Flow<List<Movie>>

    init {
        val db = LocalDatabase.getInstance(application)
        movieDao = db.movieDao()
        allMovies = movieDao.getAll()
    }

    suspend fun insert(movie: Movie) {
        withContext(Dispatchers.IO) {
            movieDao.insert(movie)
        }
    }

    suspend fun delete(movie: Movie) {
        withContext(Dispatchers.IO) {
            movieDao.delete(movie.id)
        }
    }

    suspend fun update(movie: Movie) {
        withContext(Dispatchers.IO) {
            movieDao.update(movie)
        }
    }
}