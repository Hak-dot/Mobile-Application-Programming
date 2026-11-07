package com.nhom.movieapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Bảng cache phim để xem offline. category: "popular" hoặc "top_rated". */
@Entity(tableName = "movies", primaryKeys = ["id", "category"])
data class MovieEntity(
    val id: Int,
    val category: String,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val releaseDate: String?,
    val voteAverage: Double,
    val position: Int = 0 // giữ thứ tự như API trả về
)
