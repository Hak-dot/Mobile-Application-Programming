package com.nhom.movieapp.detail.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.nhom.movieapp.MovieApp
import com.nhom.movieapp.R
import com.nhom.movieapp.databinding.FragmentDetailBinding
import com.nhom.movieapp.detail.viewmodel.DetailViewModel

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    // Khởi tạo DetailViewModel qua Factory, nhận movieId từ Bundle do ExploreFragment truyền sang
    private val viewModel: DetailViewModel by viewModels {
        val app = requireActivity().application as MovieApp
        val movieId = arguments?.getInt("movieId") ?: -1
        DetailViewModel.Factory(app.movieRepository, app.watchlistDao, movieId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Đảm bảo nút back không bị che bởi status bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, insets.top, v.paddingRight, v.paddingBottom)
            windowInsets
        }

        // Xử lý sự kiện bấm nút Back để quay lại màn hình trước
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        // Lắng nghe trạng thái yêu thích để đổi icon / màu tim
        viewModel.isFavorite.observe(viewLifecycleOwner) { isFav ->
            if (isFav) {
                binding.btnFavorite.setImageResource(R.drawable.ic_favorite)
                binding.btnFavorite.imageTintList = ColorStateList.valueOf(Color.RED)
            } else {
                binding.btnFavorite.setImageResource(R.drawable.ic_favorite_border)
                binding.btnFavorite.imageTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }

// Bấm nút tim để thêm / xóa khỏi Watchlist
        binding.btnFavorite.setOnClickListener {
            val currentMovie = viewModel.movieDetail.value
            if (currentMovie != null) {
                viewModel.toggleFavorite(currentMovie)
                val wasFav = viewModel.isFavorite.value == true
                val msg = if (wasFav) "Đã xóa khỏi Watchlist" else "Đã thêm vào Watchlist"
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }

        // Lắng nghe dữ liệu chi tiết phim từ DetailViewModel
        viewModel.movieDetail.observe(viewLifecycleOwner) { movie ->
            if (movie != null) {
                binding.tvTitle.text = movie.title.ifEmpty { "Đang cập nhật..." }

                val releaseDate = if (!movie.releaseDate.isNullOrEmpty()) movie.releaseDate else "N/A"
                binding.tvReleaseDate.text = "Ngày chiếu: $releaseDate"

                binding.tvRating.text = String.format("⭐ %.1f/10", movie.voteAverage)

                binding.tvOverview.text = if (!movie.overview.isNullOrEmpty()) {
                    movie.overview
                } else {
                    "Chưa có mô tả chi tiết cho bộ phim này."
                }

                if (!movie.posterPath.isNullOrEmpty()) {
                    val path = if (movie.posterPath.startsWith("/")) movie.posterPath else "/${movie.posterPath}"
                    val posterUrl = "https://image.tmdb.org/t/p/w500$path"

                    Glide.with(this)
                        .load(posterUrl)
                        .placeholder(android.R.color.darker_gray)
                        .error(android.R.color.darker_gray)
                        .into(binding.imgBackdrop)
                } else {
                    binding.imgBackdrop.setImageResource(android.R.color.darker_gray)
                }
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
