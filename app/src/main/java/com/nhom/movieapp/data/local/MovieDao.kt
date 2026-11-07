package com.nhom.movieapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Query("SELECT * FROM movies WHERE category = :category ORDER BY position ASC")
    fun observeByCategory(category: String): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE id = :id LIMIT 1")
    fun observeById(id: Int): Flow<MovieEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<MovieEntity>)

    @Query("DELETE FROM movies WHERE category = :category")
    suspend fun clearCategory(category: String)

    /** Thay toàn bộ cache của một danh mục trong một transaction. */
    @Transaction
    suspend fun replaceCategory(category: String, movies: List<MovieEntity>) {
        clearCategory(category)
        insertAll(movies)
    }
}
