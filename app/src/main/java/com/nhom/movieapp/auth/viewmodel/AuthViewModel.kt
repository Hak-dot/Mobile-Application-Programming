package com.nhom.movieapp.auth.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nhom.movieapp.auth.data.AuthRepository
import com.nhom.movieapp.auth.data.SessionManager
import com.nhom.movieapp.data.Resource
import kotlinx.coroutines.launch
import androidx.lifecycle.asLiveData
import com.nhom.movieapp.data.local.AppDatabase
import com.nhom.movieapp.data.local.UserEntity
import kotlinx.coroutines.NonCancellable

class AuthViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = AuthRepository()
    private val session = SessionManager(app)

    private val userDao = AppDatabase.getInstance(app).userDao()

    val user: LiveData<UserEntity?>
        get() = repository.currentUser?.uid
            ?.let { userDao.observe(it).asLiveData() }
            ?: MutableLiveData(null)

    private val _state = MutableLiveData<Resource<Unit>?>()
    val state: LiveData<Resource<Unit>?> = _state

    val currentEmail: String? get() = repository.currentUser?.email ?: session.email

    fun login(email: String, password: String) {
        val e = email.trim()
        validate(e, password)?.let { _state.value = Resource.Error(it); return }
        execute(e) { repository.login(e, password) }
    }

    fun register(email: String, password: String, confirm: String) {
        val e = email.trim()
        validate(e, password)?.let { _state.value = Resource.Error(it); return }
        if (password != confirm) {
            _state.value = Resource.Error("Mật khẩu nhập lại không khớp.")
            return
        }
        execute(e) { repository.register(e, password) }
    }

    fun logout() {
        repository.logout()
        session.clear()
        viewModelScope.launch(NonCancellable) { userDao.clear() }
    }

    fun consume() {
        _state.value = null
    }

    private fun execute(email: String, action: suspend () -> Resource<Unit>) {
        _state.value = Resource.Loading
        viewModelScope.launch {
            val result = action()
            if (result is Resource.Success) {
                session.save(email)
                repository.currentUser?.let {
                    userDao.upsert(
                        UserEntity(it.uid, it.email ?: email, email.substringBefore('@'), System.currentTimeMillis())
                    )
                }
            }
            _state.value = result
        }
    }

    private fun validate(email: String, password: String): String? = when {
        email.isEmpty() || password.isEmpty() -> "Vui lòng nhập đầy đủ email và mật khẩu."
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email không hợp lệ."
        password.length < 6 -> "Mật khẩu tối thiểu 6 ký tự."
        else -> null
    }
}