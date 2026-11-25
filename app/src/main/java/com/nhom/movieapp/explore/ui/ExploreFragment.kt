
package com.nhom.movieapp.explore.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
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
        MovieViewModel.Factory(
            app.movieRepository,
            MovieRepository.CATEGORY_POPULAR
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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
            findNavController().navigate(
                R.id.action_explore_to_detail,
                bundle
            )
        }

        binding.recyclerViewMovies.layoutManager =
            LinearLayoutManager(requireContext())

        binding.recyclerViewMovies.adapter = adapter

        // Hiển thị danh sách phim phổ biến
        viewModel.movies.observe(viewLifecycleOwner) { movieList ->
            if (binding.etSearch.text.isNullOrBlank()) {
                adapter.submitList(movieList)
            }
        }

        // Hiển thị kết quả tìm kiếm
        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            if (!binding.etSearch.text.isNullOrBlank()) {
                adapter.submitList(results)
            }
        }

        // Xử lý lỗi tìm kiếm
        viewModel.searchState.observe(viewLifecycleOwner) { resource ->
            if (resource is Resource.Error) {
                Toast.makeText(
                    requireContext(),
                    resource.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Khi nhập từ khóa, gọi API tìm kiếm
        binding.etSearch.doAfterTextChanged { editable ->
            val keyword = editable?.toString()?.trim().orEmpty()

            if (keyword.isEmpty()) {
                adapter.submitList(viewModel.movies.value)
            } else if (keyword.length >= 2) {
                viewModel.searchMovies(keyword)
            }
        }

        // Trạng thái tải danh sách phim
        viewModel.refreshState.observe(viewLifecycleOwner) { resource ->
            if (resource is Resource.Error) {
                Toast.makeText(
                    requireContext(),
                    resource.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
