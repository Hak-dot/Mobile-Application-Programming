package com.nhom.movieapp.explore.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nhom.movieapp.databinding.FragmentExploreBinding

/** Bạn B làm tiếp: danh sách phim, tìm kiếm, chi tiết. */
class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // tránh rò rỉ bộ nhớ (LeakCanary sẽ báo nếu quên)
    }
}
