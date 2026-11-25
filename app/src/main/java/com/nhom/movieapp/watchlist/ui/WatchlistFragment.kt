
package com.nhom.movieapp.watchlist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.nhom.movieapp.databinding.FragmentWatchlistBinding
import kotlinx.coroutines.launch

class WatchlistFragment : Fragment() {

    private var _binding: FragmentWatchlistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WatchlistViewModel by viewModels()

    private lateinit var adapter: WatchlistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWatchlistBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = WatchlistAdapter(
            onWatchedClick = { movie ->
                viewModel.updateWatched(movie.id, !movie.isWatched)
            },
            onRemoveClick = { movie ->
                viewModel.removeMovie(movie.id)
                Toast.makeText(
                    requireContext(),
                    "Đã xóa ${movie.title}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        binding.recyclerViewWatchlist.layoutManager =
            LinearLayoutManager(requireContext())

        binding.recyclerViewWatchlist.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.watchlist.collect { movies ->
                    adapter.submitList(movies)
                    binding.recyclerViewWatchlist.visibility =
                        if (movies.isEmpty()) View.GONE else View.VISIBLE
                    binding.tvEmpty.visibility =
                        if (movies.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
