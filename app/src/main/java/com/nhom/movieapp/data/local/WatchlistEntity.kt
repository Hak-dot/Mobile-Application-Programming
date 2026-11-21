package com.nhom.movieapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchlistEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: String?,
    val voteAverage: Double
)