package com.nhom.movieapp.auth.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhom.movieapp.auth.data.AuthRepository
import com.nhom.movieapp.data.Resource
import kotlinx.coroutines.launch

/**
 * state: null = chưa có gì, Loading / Success / Error theo từng lần bấm nút.
 * Fragment xử lý xong thì gọi consume() để tránh phát lại khi xoay màn hình.
 */
class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _state = MutableLiveData<Resource<Unit>?>()
    val state: LiveData<Resource<Unit>?> = _state

    val currentEmail: String? get() = repository.currentUser?.email

    fun login(email: String, password: String) {
        val e = email.trim()
        validate(e, password)?.let { _state.value = Resource.Error(it); return }
        execute { repository.login(e, password) }
    }

    fun register(email: String, password: String, confirm: String) {
        val e = email.trim()
        validate(e, password)?.let { _state.value = Resource.Error(it); return }
        if (password != confirm) {
            _state.value = Resource.Error("Mật khẩu nhập lại không khớp.")
            return
        }
        execute { repository.register(e, password) }
    }

    fun logout() = repository.logout()

    fun consume() {
        _state.value = null
    }

    private fun execute(action: suspend () -> Resource<Unit>) {
        _state.value = Resource.Loading
        viewModelScope.launch { _state.value = action() }
    }

    private fun validate(email: String, password: String): String? = when {
        email.isEmpty() || password.isEmpty() -> "Vui lòng nhập đầy đủ email và mật khẩu."
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email không hợp lệ."
        password.length < 6 -> "Mật khẩu tối thiểu 6 ký tự."
        else -> null
    }
}
