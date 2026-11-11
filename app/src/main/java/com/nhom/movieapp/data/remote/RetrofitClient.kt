package com.nhom.movieapp.data.remote

import com.nhom.movieapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    val apiService: TmdbApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbApi::class.java)
    }
    private val client: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            // Tự gắn api_key vào mọi request, key lấy từ BuildConfig (không hard-code).
            .addInterceptor { chain ->
                val original = chain.request()
                val url = original.url.newBuilder()
                    .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                    .build()
                chain.proceed(original.newBuilder().url(url).build())
            }
        if (BuildConfig.DEBUG) {
            // BASIC để log không in URL đầy đủ kèm key ra Logcat.
            builder.addInterceptor(
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
            )
        }
        builder.build()
    }
    object RetrofitClient {
        private const val BASE_URL = "https://api.themoviedb.org/3/"

        val client: OkHttpClient by lazy {
            OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val original = chain.request()
                    val url = original.url.newBuilder()
                        .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                        .build()
                    val request = original.newBuilder().url(url).build()
                    chain.proceed(request)
                }
                .build()
        }

    val api: TmdbApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbApi::class.java)
    }
    }
}
