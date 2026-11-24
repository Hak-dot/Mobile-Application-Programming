package com.nhom.movieapp.explore.ui

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.nhom.movieapp.MovieApp
import com.nhom.movieapp.R
import com.nhom.movieapp.data.Resource
import com.nhom.movieapp.data.repository.MovieRepository
import com.nhom.movieapp.databinding.FragmentExploreBinding
import com.nhom.movieapp.explore.viewmodel.MovieViewModel

class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

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

        val adapter = MovieAdapter { movie ->
            val bundle = Bundle().apply {
                putInt("movieId", movie.id)
            }
            findNavController().navigate(R.id.action_explore_to_detail, bundle)
        }
        binding.recyclerViewMovies.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewMovies.adapter = adapter

        // 1. Lắng nghe dữ liệu phim từ Room Database
        viewModel.movies.observe(viewLifecycleOwner) { movieList ->
            movieList?.let {
                adapter.submitList(it) {
                    binding.recyclerViewMovies.scrollToPosition(0)
                }
            }
        }

        // 2. Cập nhật trạng thái nổi bật cho 2 nút Popular / Top Rated
        viewModel.currentCategory.observe(viewLifecycleOwner) { category ->
            val isPopular = category == MovieRepository.CATEGORY_POPULAR
            updateCategoryButtonState(isPopularSelected = isPopular)
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
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    binding.btnRetry.visibility = View.VISIBLE
                }
            }
        }

        // Tự động đo chiều cao status bar và thêm padding cho layout
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { root, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            root.setPadding(root.paddingLeft, insets.top, root.paddingRight, root.paddingBottom)
            windowInsets
        }

        binding.btnRetry.setOnClickListener {
            Toast.makeText(requireContext(), "Đang thử kết nối lại...", Toast.LENGTH_SHORT).show()
            viewModel.refresh()
        }

        binding.btnPopular.setOnClickListener {
            viewModel.switchCategory(MovieRepository.CATEGORY_POPULAR)
        }

        binding.btnTopRated.setOnClickListener {
            viewModel.switchCategory(MovieRepository.CATEGORY_TOP_RATED)
        }
    }

    private fun updateCategoryButtonState(isPopularSelected: Boolean) {
        val activeBg = ColorStateList.valueOf(Color.parseColor("#6750A4"))
        val inactiveBg = ColorStateList.valueOf(Color.parseColor("#E7E0EC"))
        val activeText = Color.WHITE
        val inactiveText = Color.parseColor("#1D1B20")

        binding.btnPopular.isSelected = isPopularSelected
        binding.btnTopRated.isSelected = !isPopularSelected

        binding.btnPopular.backgroundTintList = if (isPopularSelected) activeBg else inactiveBg
        binding.btnPopular.setTextColor(if (isPopularSelected) activeText else inactiveText)

        binding.btnTopRated.backgroundTintList = if (!isPopularSelected) activeBg else inactiveBg
        binding.btnTopRated.setTextColor(if (!isPopularSelected) activeText else inactiveText)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}