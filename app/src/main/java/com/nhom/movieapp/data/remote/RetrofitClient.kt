package com.nhom.movieapp.data.remote

import com.nhom.movieapp.BuildConfig
import okhttp3.Dns
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.InetAddress
import java.net.Proxy
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://api.themoviedb.org/3/"

    const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"

    val apiService: TmdbApi by lazy {
        val client = OkHttpClient.Builder()
            .proxy(Proxy.NO_PROXY)
            .dns(SafeDns())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val original = chain.request()
                val url = original.url.newBuilder()
                    .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                    .build()
                chain.proceed(original.newBuilder().url(url).build())
            }.apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(
                        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
                    )
                }
            }
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbApi::class.java)
    }

    /**
     * Custom DNS resolver giúp xử lý việc các nhà mạng/môi trường mạng ở VN chặn hoặc bẻ lái
     * tên miền api.themoviedb.org về 127.0.0.1 (Loopback).
     */
    private class SafeDns : Dns {
        override fun lookup(hostname: String): List<InetAddress> {
            val systemAddresses = try {
                Dns.SYSTEM.lookup(hostname)
            } catch (e: Exception) {
                emptyList()
            }

            // Loại bỏ các IP bị bẻ lái về 127.0.0.1 hoặc 0.0.0.0
            val validSystemAddresses = systemAddresses.filter {
                !it.isLoopbackAddress && it.hostAddress != "127.0.0.1" && it.hostAddress != "0.0.0.0"
            }

            if (validSystemAddresses.isNotEmpty()) {
                return validSystemAddresses
            }

            // Nếu DNS hệ thống trả về 127.0.0.1 hoặc bị lỗi, phân giải qua Google DNS over HTTP
            val dohAddresses = resolveViaGoogleDns(hostname)
            if (dohAddresses.isNotEmpty()) {
                return dohAddresses
            }

            // Fallback IP Cloudflare của TMDB nếu cả DNS hệ thống và Google DNS đều không khả dụng
            if (hostname.contains("themoviedb.org") || hostname.contains("tmdb.org")) {
                return listOf(
                    InetAddress.getByAddress(hostname, byteArrayOf(104.toByte(), 16.toByte(), 148.toByte(), 156.toByte())),
                    InetAddress.getByAddress(hostname, byteArrayOf(172.toByte(), 67.toByte(), 74.toByte(), 156.toByte()))
                )
            }

            return systemAddresses.ifEmpty { throw UnknownHostException("Unable to resolve host $hostname") }
        }

        private fun resolveViaGoogleDns(hostname: String): List<InetAddress> {
            return try {
                val url = java.net.URL("https://dns.google/resolve?name=$hostname&type=A")
                val connection = url.openConnection() as java.net.HttpURLConnection
                connection.connectTimeout = 4000
                connection.readTimeout = 4000
                connection.requestMethod = "GET"
                if (connection.responseCode == 200) {
                    val json = connection.inputStream.bufferedReader().use { it.readText() }
                    val matches = Regex("\"data\":\"([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)\"").findAll(json)
                    val ips = matches.map { it.groupValues[1] }
                        .filter { it != "127.0.0.1" && it != "0.0.0.0" }
                        .toList()

                    ips.map { ipStr ->
                        val bytes = ipStr.split(".").map { it.toInt().toByte() }.toByteArray()
                        InetAddress.getByAddress(hostname, bytes)
                    }
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}
