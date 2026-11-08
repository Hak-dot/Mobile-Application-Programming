package com.nhom.movieapp.auth.data

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.nhom.movieapp.data.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

/** Bọc FirebaseAuth: ViewModel chỉ làm việc với lớp này, không gọi Firebase trực tiếp. */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    val currentUser: FirebaseUser? get() = auth.currentUser

    suspend fun register(email: String, password: String): Resource<Unit> = call {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun login(email: String, password: String): Resource<Unit> = call {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    fun logout() = auth.signOut()

    private suspend fun call(block: suspend () -> Unit): Resource<Unit> =
        try {
            block()
            Resource.Success(Unit)
        } catch (e: CancellationException) {
            throw e // không nuốt lỗi hủy coroutine
        } catch (e: Exception) {
            Resource.Error(toMessage(e))
        }

    // Thứ tự quan trọng: WeakPassword là con của InvalidCredentials nên phải đứng trước.
    private fun toMessage(e: Exception): String = when (e) {
        is FirebaseAuthWeakPasswordException -> "Mật khẩu quá yếu (tối thiểu 6 ký tự)."
        is FirebaseAuthUserCollisionException -> "Email này đã được đăng ký."
        is FirebaseAuthInvalidUserException,
        is FirebaseAuthInvalidCredentialsException -> "Email hoặc mật khẩu không đúng."
        is FirebaseNetworkException -> "Không có kết nối mạng."
        else -> e.localizedMessage ?: "Đã xảy ra lỗi. Vui lòng thử lại."
    }
}
