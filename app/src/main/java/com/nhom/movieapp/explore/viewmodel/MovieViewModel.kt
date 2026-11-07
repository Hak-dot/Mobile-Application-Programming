package com.nhom.movieapp.explore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.nhom.movieapp.data.Resource
import com.nhom.movieapp.data.local.MovieEntity
import com.nhom.movieapp.data.repository.MovieRepository
import kotlinx.coroutines.launch

/**
 * Ví dụ ViewModel cho bạn B (Khám phá phim).
 * - movies: LiveData lấy từ Room, tự cập nhật UI khi cache thay đổi.
 * - refreshState: Loading / Success / Error của lần gọi API.
 */
class MovieViewModel(
    private val repository: MovieRepository,
    private val category: String = MovieRepository.CATEGORY_POPULAR
) : ViewModel() {

    val movies: LiveData<List<MovieEntity>> =
        repository.observeMovies(category).asLiveData()

    private val _refreshState = MutableLiveData<Resource<Unit>>()
    val refreshState: LiveData<Resource<Unit>> = _refreshState

    init {
        refresh()
    }

    fun refresh() {
        _refreshState.value = Resource.Loading
        viewModelScope.launch {
            _refreshState.value = repository.refresh(category)
        }
    }

    class Factory(
        private val repository: MovieRepository,
        private val category: String = MovieRepository.CATEGORY_POPULAR
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MovieViewModel(repository, category) as T
    }
}
