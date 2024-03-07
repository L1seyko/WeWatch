package com.hfad.wewatch.model

import android.database.Observable
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Query("SELECT * FROM movie_table")
    fun getAll(): Flow<List<Movie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: Movie)

    @Query("DELETE FROM movie_table WHERE id = :id")
    suspend fun delete(id: Int?)

    @Query("DELETE FROM movie_table")
    suspend fun deleteAll()

    @Update
    suspend fun update(movie: Movie)

}