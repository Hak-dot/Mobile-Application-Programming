package com.nhom.movieapp.auth.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nhom.movieapp.R
import com.nhom.movieapp.auth.viewmodel.AuthViewModel
import com.nhom.movieapp.databinding.FragmentProfileBinding

/** Hồ sơ: hiển thị thông tin người dùng đang đăng nhập và xử lý đăng xuất. */
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hiện tạm email từ Firebase/session, rồi ghi đè bằng dữ liệu cache trong Room.
        binding.tvEmail.text = viewModel.currentEmail ?: "Chưa đăng nhập"
        viewModel.user.observe(viewLifecycleOwner) { u ->
            if (u != null) binding.tvEmail.text = "${u.displayName}\n${u.email}"
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            findNavController().navigate(R.id.action_profile_to_login)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}