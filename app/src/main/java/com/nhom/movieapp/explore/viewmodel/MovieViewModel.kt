package com.nhom.movieapp.explore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.nhom.movieapp.data.Resource
import com.nhom.movieapp.data.local.MovieEntity
import com.nhom.movieapp.data.repository.MovieRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MovieViewModel(
    private val repository: MovieRepository,
    initialCategory: String = MovieRepository.CATEGORY_POPULAR
) : ViewModel() {

    private val _currentCategory = MutableLiveData(initialCategory)
    val currentCategory: LiveData<String> = _currentCategory

    // Tự động đổi luồng dữ liệu Room khi category thay đổi
    val movies: LiveData<List<MovieEntity>> = _currentCategory.switchMap { cat ->
        repository.observeMovies(cat).asLiveData()
    }

    private val _refreshState = MutableLiveData<Resource<Unit>>()
    val refreshState: LiveData<Resource<Unit>> = _refreshState

    private var refreshJob: Job? = null

    init {
        refresh()
    }

    fun switchCategory(newCategory: String) {
        if (newCategory != MovieRepository.CATEGORY_POPULAR &&
            newCategory != MovieRepository.CATEGORY_TOP_RATED
        ) return

        if (_currentCategory.value != newCategory) {
            _currentCategory.value = newCategory
            refresh()
        }
    }

    fun refresh() {
        val cat = _currentCategory.value ?: MovieRepository.CATEGORY_POPULAR
        // Hủy job cũ nếu người dùng bấm chuyển tab liên tục để tránh ghi đè trạng thái sai
        refreshJob?.cancel()
        _refreshState.value = Resource.Loading
        refreshJob = viewModelScope.launch {
            _refreshState.value = repository.refresh(cat)
        }
    }

    class Factory(
        private val repository: MovieRepository,
        private val category: String = MovieRepository.CATEGORY_POPULAR
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {
                return MovieViewModel(repository, category) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}