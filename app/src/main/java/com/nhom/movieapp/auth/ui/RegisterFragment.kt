package com.nhom.movieapp.auth.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.nhom.movieapp.R
import com.nhom.movieapp.auth.viewmodel.AuthViewModel
import com.nhom.movieapp.data.Resource
import com.nhom.movieapp.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            viewModel.register(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString(),
                binding.etConfirm.text.toString()
            )
        }
        binding.tvGoLogin.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> setLoading(true)
                is Resource.Success -> {
                    setLoading(false)
                    viewModel.consume()
                    // Đăng ký thành công thì Firebase đã tự đăng nhập luôn.
                    findNavController().navigate(R.id.action_register_to_explore)
                }
                is Resource.Error -> {
                    setLoading(false)
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    viewModel.consume()
                }
                null -> setLoading(false)
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progress.isVisible = loading
        binding.btnRegister.isEnabled = !loading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
