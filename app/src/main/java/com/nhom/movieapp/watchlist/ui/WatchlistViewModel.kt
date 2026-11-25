package com.nhom.movieapp.watchlist.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nhom.movieapp.data.local.AppDatabase
import com.nhom.movieapp.data.local.WatchlistEntity
import com.nhom.movieapp.data.repository.WatchlistRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WatchlistViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = AppDatabase.getInstance(app).watchlistDao()
    private val repository = WatchlistRepository(dao)

    val watchlist = repository.observeAll().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addMovie(movie: WatchlistEntity) {
        viewModelScope.launch {
            repository.addMovie(movie)
        }
    }

    fun removeMovie(id: Int) {
        viewModelScope.launch {
            repository.removeMovie(id)
        }
    }

    fun updateWatched(id: Int, watched: Boolean) {
        viewModelScope.launch {
            repository.updateWatched(id, watched)
        }
    }

    fun isInWatchlist(id: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            onResult(repository.getById(id) != null)
        }
    }
}
