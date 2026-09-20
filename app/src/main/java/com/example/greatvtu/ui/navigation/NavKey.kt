package com.example.greatvtu.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface NavKey {
    @Serializable
    data object Login : NavKey
    
    @Serializable
    data object Dashboard : NavKey
    
    @Serializable
    data object Home : NavKey
    
    @Serializable
    data object Transactions : NavKey
    
    @Serializable
    data object Profile : NavKey

    @Serializable
    data object VTU : NavKey

    @Serializable
    data object Utilities : NavKey
}
