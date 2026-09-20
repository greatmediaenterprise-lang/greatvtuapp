package com.example.greatvtu.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val id: String,
    val user_id: String,
    val type: String, // "VTU" or "Utility"
    val service_name: String, // e.g., "MTN Airtime", "DSTV"
    val amount: Double,
    val status: String, // "SUCCESS", "FAILED"
    val created_at: String,
    val recipient: String,
    val reference: String
)
