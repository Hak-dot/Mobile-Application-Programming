
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MovieViewModel(
    private val repository: MovieRepository,
    private val category: String = MovieRepository.CATEGORY_POPULAR
) : ViewModel() {

    val movies: LiveData<List<MovieEntity>> =
        repository.observeMovies(category).asLiveData()

    private val _searchResults = MutableLiveData<List<MovieEntity>>()
    val searchResults: LiveData<List<MovieEntity>> = _searchResults

    private val _searchState =
        MutableLiveData<Resource<List<MovieEntity>>?>()

    val searchState: LiveData<Resource<List<MovieEntity>>?> = _searchState

    private var searchJob: Job? = null

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

    fun searchMovies(query: String) {
        val keyword = query.trim()

        if (keyword.isEmpty()) {
            searchJob?.cancel()
            _searchResults.value = emptyList()
            _searchState.value = null
            return
        }

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            _searchState.value = Resource.Loading

            val result = repository.searchMovies(keyword)

            when (result) {
                is Resource.Success -> {
                    _searchResults.value = result.data
                    _searchState.value = result
                }

                is Resource.Error -> {
                    _searchState.value = result
                }

                is Resource.Loading -> {}
            }
        }
    }

    class Factory(
        private val repository: MovieRepository,
        private val category: String = MovieRepository.CATEGORY_POPULAR
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MovieViewModel(repository, category) as T
        }
    }
}
