package com.nhom.movieapp.detail.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.R
import com.nhom.movieapp.databinding.FragmentDetailBinding

//import coil.load
// Nếu dự án đang dùng Coil tải ảnh
// Hoặc Glide tùy ý nhóm

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Nhận movieId từ Bundle do ExploreFragment truyền sang
        val movieId = arguments?.getInt("movieId") ?: -1

        if (movieId != -1) {
            // 2. TODO: Gọi ViewModel lấy chi tiết phim dựa vào movieId
            // Tạm thời cấu trúc hiển thị mẫu, sau khi kết nối ViewModel bạn chỉ cần đổ data vào đây:
            loadMovieDetailsSample()
        }
    }


    private fun loadMovieDetailsSample() {
        // Ví dụ xử lý an toàn Null-safety cho overview và backdrop (Yêu cầu Tuần 2)
        val backdropPath: String? = null // Giả lập dữ liệu trả về từ API/Room
        val overview: String? = null     // Giả lập trường hợp overview bị rỗng/null
        val title = "Tên phim chi tiết"
        val releaseDate = "2026-03-30"
        val rating = 8.5


        // Bind dữ liệu an toàn, không sợ crash app
        binding.tvTitle.text = title ?: "Đang cập nhật..."
        binding.tvReleaseDate.text = "Ngày chiếu: ${releaseDate ?: "N/A"}"
        binding.tvRating.text = "⭐ $rating/10"
        // Xử lý sự kiện bấm nút Back để quay lại màn hình trước
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        // Xử lý an toàn overview rỗng
        binding.tvOverview.text = if (!overview.isNullOrEmpty()) {
            overview
        } else {
            "Chưa có mô tả chi tiết cho bộ phim này."
        }

        // Xử lý tải ảnh backdrop an toàn với Glide (tránh null pointer)
        if (!backdropPath.isNullOrEmpty()) {
            val backdropUrl = "https://image.tmdb.org/t/p/w500$backdropPath"
            Glide.with(this)
                .load(backdropUrl)
                .placeholder(android.R.color.darker_gray)
                .error(android.R.color.darker_gray)
                .into(binding.imgBackdrop)
        } else {
            binding.imgBackdrop.setImageResource(android.R.color.darker_gray)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}