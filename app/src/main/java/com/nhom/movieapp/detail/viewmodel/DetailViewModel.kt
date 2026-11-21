package com.nhom.movieapp.detail.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nhom.movieapp.data.Resource
import com.nhom.movieapp.data.local.MovieEntity
import com.nhom.movieapp.data.local.WatchlistDao
import com.nhom.movieapp.data.local.WatchlistEntity
import com.nhom.movieapp.data.repository.MovieRepository
import kotlinx.coroutines.launch

class DetailViewModel(
    private val movieRepository: MovieRepository,
    private val watchlistDao: WatchlistDao,
    private val movieId: Int
) : ViewModel() {

    private val _movieDetail = MutableLiveData<MovieEntity?>()
    val movieDetail: LiveData<MovieEntity?> get() = _movieDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    init {
        if (movieId != -1) {
            fetchMovieDetail(movieId)
        }
    }

    private fun fetchMovieDetail(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            // 1. Đọc từ local DB hiển thị trước nếu đã có
            val localMovie = movieRepository.getMovieById(id)
            if (localMovie != null) {
                _movieDetail.value = localMovie
            }

            // 2. Gọi API để tải thông tin mới nhất và lưu vào Room
            when (val result = movieRepository.fetchAndSaveMovieDetail(id)) {
                is Resource.Success -> {
                    _movieDetail.value = result.data
                }
                is Resource.Error -> {
                    if (localMovie == null) {
                        _errorMessage.value = result.message
                    }
                }
                else -> {}
            }
            _isLoading.value = false
        }
    }
    // Trong DetailViewModel.kt
    private val _isFavorite = MutableLiveData(false)
    val isFavorite: LiveData<Boolean> get() = _isFavorite

    init {
        if (movieId != -1) {
            fetchMovieDetail(movieId)
            checkFavoriteStatus(movieId)
        }
    }

    private fun checkFavoriteStatus(id: Int) {
        viewModelScope.launch {
            // TODO: Đổi tên hàm cho khớp WatchlistDao của bạn C (ví dụ: watchlistDao.isFavorite(id) hoặc getById(id))
            _isFavorite.value = watchlistDao.isFavorite(id)
        }
    }

    fun toggleFavorite(movie: MovieEntity) {
        viewModelScope.launch {
            val current = _isFavorite.value == true
            if (current) {
                // TODO: Đổi sang hàm xóa khỏi watchlist của bạn C (ví dụ: watchlistDao.deleteById(movie.id))
                watchlistDao.deleteById(movie.id)
                _isFavorite.value = false
            } else {
                // TODO: Map MovieEntity sang WatchlistEntity của bạn C rồi insert
                val item = WatchlistEntity(
                    id = movie.id,
                    title = movie.title,
                    posterPath = movie.posterPath,
                    releaseDate = movie.releaseDate,
                    voteAverage = movie.voteAverage
                )
                watchlistDao.insert(item)
                _isFavorite.value = true
            }
        }
    }

    class Factory(
        private val repository: MovieRepository,
        private val watchlistDao: WatchlistDao,
        private val movieId: Int
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
                return DetailViewModel(repository, watchlistDao, movieId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
