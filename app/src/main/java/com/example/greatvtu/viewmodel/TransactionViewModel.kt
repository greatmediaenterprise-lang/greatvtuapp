package com.example.greatvtu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.greatvtu.data.model.Transaction
import com.example.greatvtu.data.repository.AuthRepository
import com.example.greatvtu.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val authRepository: AuthRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedTransaction = MutableStateFlow<Transaction?>(null)
    val selectedTransaction = _selectedTransaction.asStateFlow()

    init {
        loadTransactions()
    }

    fun loadTransactions() {
        val userId = authRepository.currentUser?.id ?: "demo"
        viewModelScope.launch {
            transactionRepository.getTransactions(userId)
        }
    }

    val transactions = combine(
        transactionRepository.localTransactions,
        _searchQuery
    ) { txList, query ->
        if (query.isBlank()) {
            txList
        } else {
            txList.filter {
                it.service_name.contains(query, ignoreCase = true) ||
                it.recipient.contains(query, ignoreCase = true) ||
                it.reference.contains(query, ignoreCase = true) ||
                it.type.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectTransaction(transaction: Transaction?) {
        _selectedTransaction.value = transaction
    }
}

// Extension to avoid compilation error if asStateFlow isn't imported correctly
fun <T> MutableStateFlow<T>.asStateFlow() = this
