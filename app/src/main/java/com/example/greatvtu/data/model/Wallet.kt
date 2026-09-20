package com.example.greatvtu.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Wallet(
    val id: String,
    val user_id: String,
    val balance: Double
)
