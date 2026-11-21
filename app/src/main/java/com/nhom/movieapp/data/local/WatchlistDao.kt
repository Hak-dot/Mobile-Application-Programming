package com.nhom.movieapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WatchlistDao {
    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE id = :movieId)")
    suspend fun isFavorite(movieId: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: WatchlistEntity)

    @Query("DELETE FROM watchlist WHERE id = :movieId")
    suspend fun deleteById(movieId: Int)
}