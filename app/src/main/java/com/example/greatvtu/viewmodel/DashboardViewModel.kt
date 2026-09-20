package com.example.greatvtu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.greatvtu.data.repository.AuthRepository
import com.example.greatvtu.data.repository.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val authRepository: AuthRepository,
    private val walletRepository: WalletRepository
) : ViewModel() {

    private val _walletBalance = MutableStateFlow(0.0)
    val walletBalance: StateFlow<Double> = _walletBalance.asStateFlow()

    init {
        loadBalance()
        observeBalance()
    }

    private fun loadBalance() {
        val userId = authRepository.currentUser?.id ?: return
        viewModelScope.launch {
            walletRepository.getBalance(userId)?.let {
                _walletBalance.value = it
            }
        }
    }

    private fun observeBalance() {
        val userId = authRepository.currentUser?.id ?: return
        viewModelScope.launch {
            walletRepository.observeBalance(userId).collect { balance ->
                _walletBalance.value = balance
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
