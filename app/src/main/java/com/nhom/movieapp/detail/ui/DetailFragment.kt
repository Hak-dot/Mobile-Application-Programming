
package com.nhom.movieapp.detail.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.nhom.movieapp.data.local.WatchlistEntity
import com.nhom.movieapp.data.remote.RetrofitClient
import com.nhom.movieapp.databinding.FragmentDetailBinding
import com.nhom.movieapp.watchlist.ui.WatchlistViewModel
import kotlinx.coroutines.launch

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val watchlistViewModel: WatchlistViewModel by viewModels()

    private var currentMovie: WatchlistEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        val movieId = arguments?.getInt("movieId") ?: -1

        binding.btnAddWatchlist.isEnabled = false

        if (movieId == -1) {
            Toast.makeText(
                requireContext(),
                "Không tìm thấy phim",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val movie = RetrofitClient.apiService.getMovieDetail(movieId)

                currentMovie = WatchlistEntity(
                    id = movie.id,
                    title = movie.title.orEmpty(),
                    overview = movie.overview.orEmpty(),
                    posterPath = movie.posterPath,
                    releaseDate = movie.releaseDate,
                    voteAverage = movie.voteAverage ?: 0.0,
                    isWatched = false
                )

                // Hiển thị tên phim
                binding.tvMovieTitle.text =
                    movie.title ?: "Không có tên phim"

                // Hiển thị điểm đánh giá
                binding.tvMovieRating.text =
                    "⭐ Đánh giá: ${String.format("%.1f", movie.voteAverage ?: 0.0)}/10"

                // Hiển thị ngày phát hành
                binding.tvReleaseDate.text =
                    "Ngày phát hành: ${movie.releaseDate ?: "Chưa cập nhật"}"

                // Hiển thị mô tả
                binding.tvMovieOverview.text =
                    movie.overview?.takeIf { it.isNotBlank() }
                        ?: "Chưa có mô tả cho phim này."

                // Tải poster bằng Glide
                val posterUrl = movie.posterPath?.let {
                    "${RetrofitClient.IMAGE_BASE_URL}$it"
                }

                Glide.with(this@DetailFragment)
                    .load(posterUrl)
                    .centerInside()
                    .into(binding.imageMoviePoster)

                binding.btnAddWatchlist.isEnabled = true

            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Không tải được thông tin phim: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.btnAddWatchlist.setOnClickListener {
            val movie = currentMovie ?: return@setOnClickListener

            watchlistViewModel.addMovie(movie)

            Toast.makeText(
                requireContext(),
                "Đã thêm ${movie.title} vào Watchlist",
                Toast.LENGTH_SHORT
            ).show()

            binding.btnAddWatchlist.text = "Đã thêm vào Watchlist"
            binding.btnAddWatchlist.isEnabled = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
