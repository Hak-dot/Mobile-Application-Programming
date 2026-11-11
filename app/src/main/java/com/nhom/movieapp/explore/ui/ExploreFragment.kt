package com.nhom.movieapp.explore.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nhom.movieapp.MovieApp // Import Application của nhóm
import com.nhom.movieapp.data.Resource
import com.nhom.movieapp.data.repository.MovieRepository
import com.nhom.movieapp.databinding.FragmentExploreBinding
import com.nhom.movieapp.explore.viewmodel.MovieViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nhom.movieapp.R
class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    // Lấy repository đã được khởi tạo sẵn từ Application class của nhóm
    private val viewModel: MovieViewModel by viewModels {
        val app = requireActivity().application as MovieApp
        MovieViewModel.Factory(app.movieRepository, MovieRepository.CATEGORY_POPULAR)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Khởi tạo Adapter cho danh sách phim (nếu bạn đã tạo MovieAdapter)
        val adapter = MovieAdapter { movie ->
            val bundle = Bundle().apply {
                putInt("movieId", movie.id)
            }
            findNavController().navigate(R.id.action_explore_to_detail, bundle)
        }
        binding.recyclerViewMovies.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewMovies.adapter = adapter

        // 2. Lắng nghe dữ liệu phim từ Room Database
        viewModel.movies.observe(viewLifecycleOwner) { movieList ->
            movieList?.let {
                 adapter.submitList(it)
            }
        }

        // 3. Lắng nghe trạng thái khi gọi API làm mới cache
        viewModel.refreshState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Đang tải...
                }
                is Resource.Success -> {
                    // Tải thành công
                }
                is Resource.Error -> {
                    // Hiển thị thông báo khi mất mạng hoặc lỗi server
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}