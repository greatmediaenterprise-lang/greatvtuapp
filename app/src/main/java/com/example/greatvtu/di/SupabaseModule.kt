package com.example.greatvtu.di

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

object SupabaseModule {
    private const val SUPABASE_URL = "https://ffsaxdtmedzjtbmbsxhx.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZmc2F4ZHRtZWR6anRibWJzeGh4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODk5MTQyNTMsImV4cCI6MjEwNTQ5MDI1M30.ZrrjgiYgYnjhyYjYOZ8vdMguOxYrkispPyaX6BhQhFk"

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Realtime)
        }
    }
}
