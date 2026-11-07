package com.nhom.movieapp.data

/** Ba trạng thái UI theo đề: Loading, Success, Error. */
sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
}
