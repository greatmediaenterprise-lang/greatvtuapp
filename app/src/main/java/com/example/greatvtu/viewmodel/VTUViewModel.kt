package com.example.greatvtu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.greatvtu.data.repository.AuthRepository
import com.example.greatvtu.data.repository.TransactionRepository
import com.example.greatvtu.data.repository.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VTUViewModel(
    private val authRepository: AuthRepository,
    private val walletRepository: WalletRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _vtuStatus = MutableStateFlow<String?>(null)
    val vtuStatus = _vtuStatus.asStateFlow()

    private val _billStatus = MutableStateFlow<String?>(null)
    val billStatus = _billStatus.asStateFlow()

    fun clearStatus() {
        _vtuStatus.value = null
        _billStatus.value = null
    }

    fun purchaseVTU(network: String, type: String, amount: Double, phoneNumber: String) {
        val userId = authRepository.currentUser?.id ?: "demo"
        if (phoneNumber.isBlank() || amount <= 0) {
            _vtuStatus.value = "Error: Invalid details"
            return
        }
        
        viewModelScope.launch {
            val balance = walletRepository.getBalance(userId) ?: 50000.0 // fallback demo balance
            if (balance < amount) {
                _vtuStatus.value = "Error: Insufficient balance"
                return@launch
            }

            val tx = transactionRepository.createTransaction(
                userId = userId,
                type = "VTU",
                serviceName = "$network $type",
                amount = amount,
                recipient = phoneNumber
            )
            if (tx != null) {
                _vtuStatus.value = "Success: Purchased $network $type of ₦$amount for $phoneNumber"
            } else {
                _vtuStatus.value = "Error: Transaction failed"
            }
        }
    }

    fun payUtility(provider: String, type: String, amount: Double, accountNumber: String) {
        val userId = authRepository.currentUser?.id ?: "demo"
        if (accountNumber.isBlank() || amount <= 0) {
            _billStatus.value = "Error: Invalid details"
            return
        }

        viewModelScope.launch {
            val balance = walletRepository.getBalance(userId) ?: 50000.0
            if (balance < amount) {
                _billStatus.value = "Error: Insufficient balance"
                return@launch
            }

            val tx = transactionRepository.createTransaction(
                userId = userId,
                type = "Utility",
                serviceName = "$provider $type",
                amount = amount,
                recipient = accountNumber
            )
            if (tx != null) {
                _billStatus.value = "Success: Paid ₦$amount to $provider ($accountNumber)"
            } else {
                _billStatus.value = "Error: Payment failed"
            }
        }
    }
}
