package com.nhom.movieapp.detail.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nhom.movieapp.data.Resource
import com.nhom.movieapp.data.local.MovieEntity
import com.nhom.movieapp.data.repository.MovieRepository
import kotlinx.coroutines.launch

class DetailViewModel(
    private val movieRepository: MovieRepository,
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

    class Factory(
        private val repository: MovieRepository,
        private val movieId: Int
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
                return DetailViewModel(repository, movieId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
