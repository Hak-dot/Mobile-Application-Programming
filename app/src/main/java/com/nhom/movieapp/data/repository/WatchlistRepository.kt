
package com.nhom.movieapp.data.repository

import com.nhom.movieapp.data.local.WatchlistDao
import com.nhom.movieapp.data.local.WatchlistEntity
import kotlinx.coroutines.flow.Flow

class WatchlistRepository(
    private val watchlistDao: WatchlistDao
) {

    // Lấy danh sách phim trong Watchlist
    fun observeAll(): Flow<List<WatchlistEntity>> {
        return watchlistDao.observeAll()
    }

    // Tìm phim theo ID
    suspend fun getById(id: Int): WatchlistEntity? {
        return watchlistDao.getById(id)
    }

    // Thêm phim vào Watchlist
    suspend fun addMovie(movie: WatchlistEntity) {
        watchlistDao.insert(movie)
    }

    // Xóa phim khỏi Watchlist
    suspend fun removeMovie(id: Int) {
        watchlistDao.delete(id)
    }

    // Cập nhật trạng thái đã xem
    suspend fun updateWatched(id: Int, watched: Boolean) {
        watchlistDao.updateWatched(id, watched)
    }
}
