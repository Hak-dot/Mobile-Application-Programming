package com.nhom.movieapp.explore.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
                    binding.btnRetry.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.btnRetry.visibility = View.GONE
                }
                is Resource.Error -> {
                    // Hiển thị thông báo khi mất mạng hoặc lỗi server
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    binding.btnRetry.visibility = View.VISIBLE
                }
            }
        }
        //Tự động đo chiều cao status bar và thêm padding cho layout để không bị che khuất camera
        val rootView = binding.root // Hoặc tên biến root layout của bạn
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Set padding top bằng đúng chiều cao của thanh trạng thái (status bar)
            view.setPadding(view.paddingLeft, insets.top, view.paddingRight, view.paddingBottom)
            windowInsets
        }
        // Xử lý sự kiện bấm nút Thử lại
        // Xử lý sự kiện bấm nút Thử lại
        binding.btnRetry.setOnClickListener {
            Toast.makeText(requireContext(), "Đang thử kết nối lại...", Toast.LENGTH_SHORT).show()
            viewModel.refresh() // <--- GỌI HÀM REFRESH CỦA VIEWMODEL Ở ĐÂY
        }

        // Xử lý chuyển đổi qua lại Popular ⇄ Top Rated
        binding.btnPopular.setOnClickListener {
            Toast.makeText(requireContext(), "Đang xem Popular", Toast.LENGTH_SHORT).show()
            viewModel.switchCategory(MovieRepository.CATEGORY_POPULAR) // <--- GỌI ĐỔI CATEGORY
        }

        binding.btnTopRated.setOnClickListener {
            Toast.makeText(requireContext(), "Đang xem Top Rated", Toast.LENGTH_SHORT).show()
            viewModel.switchCategory(MovieRepository.CATEGORY_TOP_RATED) // <--- GỌI ĐỔI CATEGORY
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}