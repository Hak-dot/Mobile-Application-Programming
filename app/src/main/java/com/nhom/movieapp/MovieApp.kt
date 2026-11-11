package com.nhom.movieapp

import android.app.Application
import com.nhom.movieapp.data.local.AppDatabase
import com.nhom.movieapp.data.remote.RetrofitClient
import com.nhom.movieapp.data.repository.MovieRepository

/** Khai báo trong AndroidManifest: android:name=".MovieApp" */
class MovieApp : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }

    val movieRepository: MovieRepository by lazy {
        MovieRepository(RetrofitClient.apiService, database.movieDao())
    }
}
