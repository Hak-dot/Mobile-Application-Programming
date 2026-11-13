package com.nhom.movieapp.auth.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.nhom.movieapp.R
import com.nhom.movieapp.auth.viewmodel.AuthViewModel
import com.nhom.movieapp.databinding.FragmentProfileBinding

/** Hồ sơ: ảnh đại diện, tên, email và đăng xuất. */
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) viewModel.setAvatar(uri)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvEmail.text = viewModel.currentEmail ?: "Chưa đăng nhập"
        viewModel.user.observe(viewLifecycleOwner) { u ->
            if (u != null) binding.tvEmail.text = "${u.displayName}\n${u.email}"
        }

        viewModel.avatar.observe(viewLifecycleOwner) { file ->
            binding.ivAvatar.load(file) {
                placeholder(R.drawable.ic_profile)
                error(R.drawable.ic_profile)
            }
        }

        binding.tvChangeAvatar.setOnClickListener {
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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