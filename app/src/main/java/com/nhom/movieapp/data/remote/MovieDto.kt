package com.nhom.movieapp.data.remote

import com.google.gson.annotations.SerializedName

data class MovieListResponse(
    val page: Int,
    val results: List<MovieDto>,
    @SerializedName("total_pages") val totalPages: Int
)

/** Các trường nullable vì TMDB có thể trả null (poster_path, release_date...). */
data class MovieDto(
    val id: Int,
    val title: String?,
    val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("genre_ids") val genreIds: List<Int>?
)
