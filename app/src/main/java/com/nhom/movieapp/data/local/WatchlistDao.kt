
package com.nhom.movieapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {

    @Query("SELECT * FROM watchlist ORDER BY title ASC")
    fun observeAll(): Flow<List<WatchlistEntity>>

    @Query("SELECT * FROM watchlist WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): WatchlistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: WatchlistEntity)

    @Query("DELETE FROM watchlist WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("UPDATE watchlist SET isWatched = :watched WHERE id = :id")
    suspend fun updateWatched(id: Int, watched: Boolean)
}
