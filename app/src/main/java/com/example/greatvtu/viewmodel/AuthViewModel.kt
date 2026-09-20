package com.example.greatvtu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.greatvtu.data.repository.AuthRepository
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: UserInfo?) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    val currentUser: UserInfo?
        get() = authRepository.currentUser

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                authRepository.login(email, password)
                _authState.value = AuthState.Success(authRepository.currentUser)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                authRepository.signUp(email, password)
                _authState.value = AuthState.Success(authRepository.currentUser)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Sign up failed")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authRepository.logout()
                _authState.value = AuthState.Idle
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Logout failed")
            }
        }
    }
}
