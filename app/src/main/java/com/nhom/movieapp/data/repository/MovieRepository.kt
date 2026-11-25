package com.nhom.movieapp.data.repository

import com.nhom.movieapp.data.Resource
import com.nhom.movieapp.data.local.MovieDao
import com.nhom.movieapp.data.local.MovieEntity
import com.nhom.movieapp.data.remote.MovieDto
import com.nhom.movieapp.data.remote.TmdbApi
import kotlinx.coroutines.flow.Flow

class MovieRepository(
    private val api: TmdbApi,
    private val dao: MovieDao
) {

    fun observeMovies(category: String): Flow<List<MovieEntity>> =
        dao.observeByCategory(category)

    fun observeMovie(id: Int): Flow<MovieEntity?> =
        dao.observeById(id)

    suspend fun refresh(category: String): Resource<Unit> {
        return try {
            val response = when (category) {
                CATEGORY_TOP_RATED -> api.getTopRated()
                else -> api.getPopular()
            }
            dao.replaceCategory(category, response.results.mapIndexed { i, dto -> dto.toEntity(category, i) })
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Lỗi kết nối mạng: ${e.localizedMessage ?: e.message}")
        }
    }

    suspend fun getMovieById(movieId: Int): MovieEntity? {
        return dao.getMovieById(movieId)
    }

    /** Lấy chi tiết phim từ API và cập nhật vào Room cache. */
    suspend fun fetchAndSaveMovieDetail(movieId: Int): Resource<MovieEntity> {
        val cached = dao.getMovieById(movieId)
        return try {
            val dto = api.getMovieDetail(movieId)
            val category = cached?.category ?: CATEGORY_POPULAR
            val position = cached?.position ?: 0
            val entity = dto.toEntity(category, position)
            dao.insertAll(listOf(entity))
            Resource.Success(entity)
        } catch (e: Exception) {
            if (cached != null) {
                Resource.Success(cached)
            } else {
                Resource.Error("Không thể tải chi tiết phim: ${e.localizedMessage ?: e.message}")
            }
        }
    }

    // TÌM KIẾM PHIM
    suspend fun searchMovies(query: String): Resource<List<MovieEntity>> {
        return try {
            val response = api.searchMovies(query = query)
            val movies = response.results.mapIndexed { index, dto ->
                dto.toEntity(CATEGORY_SEARCH, index)
            }
            Resource.Success(movies)
        } catch (e: java.io.IOException) {
            Resource.Error("Không có kết nối mạng.")
        } catch (e: retrofit2.HttpException) {
            Resource.Error("Lỗi máy chủ: ${e.code()}")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Đã xảy ra lỗi.")
        }
    }

    private fun MovieDto.toEntity(category: String, position: Int) = MovieEntity(
        id = id,
        category = category,
        title = title.orEmpty(),
        overview = overview?.takeIf { it.isNotBlank() } ?: "Chưa có mô tả.",
        posterPath = posterPath ?: backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage ?: 0.0,
        position = position
    )

    companion object {
        const val CATEGORY_POPULAR = "popular"
        const val CATEGORY_TOP_RATED = "top_rated"
        const val CATEGORY_SEARCH = "search"
    }
}
