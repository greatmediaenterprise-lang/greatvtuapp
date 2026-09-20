package com.example.greatvtu.data.repository

import com.example.greatvtu.data.model.Wallet
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import io.github.jan.supabase.realtime.decodeRecord

class WalletRepository(private val client: SupabaseClient) {

    suspend fun getBalance(userId: String): Double? {
        return try {
            client.postgrest["wallets"]
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeSingleOrNull<Wallet>()?.balance
        } catch (e: Exception) {
            null
        }
    }

    fun observeBalance(userId: String): Flow<Double> {
        val channel = client.realtime.channel("wallet_changes")
        return channel.postgresChangeFlow<PostgresAction.Update>(schema = "public") {
            table = "wallets"
            filter = "user_id=eq.$userId"
        }.map { action ->
            action.decodeRecord<Wallet>().balance
        }
    }
}
