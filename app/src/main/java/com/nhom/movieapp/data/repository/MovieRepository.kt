package com.nhom.movieapp.data.repository

import com.nhom.movieapp.data.Resource
import com.nhom.movieapp.data.local.MovieDao
import com.nhom.movieapp.data.local.MovieEntity
import com.nhom.movieapp.data.remote.MovieDto
import com.nhom.movieapp.data.remote.TmdbApi
import kotlinx.coroutines.flow.Flow

/**
 * Room là nguồn dữ liệu chính (single source of truth).
 * API chỉ dùng để làm mới cache. Mất mạng thì UI vẫn đọc được dữ liệu đã lưu.
 */
class MovieRepository(
    private val api: TmdbApi,
    private val dao: MovieDao
) {

    fun observeMovies(category: String): Flow<List<MovieEntity>> =
        dao.observeByCategory(category)

    fun observeMovie(id: Int): Flow<MovieEntity?> = dao.observeById(id)

    /** Gọi API rồi ghi đè cache. Trả về Success hoặc Error, không ném exception ra ngoài. */
    suspend fun refresh(category: String): Resource<Unit> =
        try {
            val response = when (category) {
                CATEGORY_TOP_RATED -> api.getTopRated()
                else -> api.getPopular()
            }
            dao.replaceCategory(category, response.results.mapIndexed { i, dto -> dto.toEntity(category, i) })
            Resource.Success(Unit)
        } catch (e: java.io.IOException) {
            Resource.Error("Không có kết nối mạng. Đang hiển thị dữ liệu đã lưu.")
        } catch (e: retrofit2.HttpException) {
            Resource.Error("Lỗi máy chủ (${e.code()}). Vui lòng thử lại.")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Đã xảy ra lỗi không xác định.")
        }

    private fun MovieDto.toEntity(category: String, position: Int) = MovieEntity(
        id = id,
        category = category,
        title = title.orEmpty(),
        overview = overview?.takeIf { it.isNotBlank() } ?: "Chưa có mô tả.",
        posterPath = posterPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage ?: 0.0,
        position = position
    )

    companion object {
        const val CATEGORY_POPULAR = "popular"
        const val CATEGORY_TOP_RATED = "top_rated"
    }
}
