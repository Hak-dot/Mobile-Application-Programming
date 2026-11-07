package com.nhom.movieapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    @GET("movie/popular")
    suspend fun getPopular(
        @Query("page") page: Int = 1,
        @Query("language") language: String = "vi-VN"
    ): MovieListResponse

    @GET("movie/top_rated")
    suspend fun getTopRated(
        @Query("page") page: Int = 1,
        @Query("language") language: String = "vi-VN"
    ): MovieListResponse

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "vi-VN"
    ): MovieListResponse

    @GET("movie/{id}")
    suspend fun getMovieDetail(
        @Path("id") id: Int,
        @Query("language") language: String = "vi-VN"
    ): MovieDto
}
