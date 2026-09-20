package com.example.greatvtu

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.greatvtu.data.repository.AuthRepository
import com.example.greatvtu.data.repository.TransactionRepository
import com.example.greatvtu.data.repository.WalletRepository
import com.example.greatvtu.di.SupabaseModule
import com.example.greatvtu.ui.navigation.NavKey
import com.example.greatvtu.ui.screen.DashboardScreen
import com.example.greatvtu.ui.screen.LoginScreen
import com.example.greatvtu.ui.screen.VTUScreen
import com.example.greatvtu.ui.theme.GREATVTUTheme
import com.example.greatvtu.util.BiometricHelper
import com.example.greatvtu.viewmodel.AuthViewModel
import com.example.greatvtu.viewmodel.DashboardViewModel
import com.example.greatvtu.viewmodel.TransactionViewModel
import com.example.greatvtu.viewmodel.VTUViewModel

class MainActivity : FragmentActivity() {
    private val authRepository by lazy { AuthRepository(SupabaseModule.client) }
    private val walletRepository by lazy { WalletRepository(SupabaseModule.client) }
    private val transactionRepository by lazy { TransactionRepository(SupabaseModule.client) }
    private val biometricHelper by lazy { BiometricHelper(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GREATVTUTheme {
                val initialKey = if (authRepository.currentUser != null) NavKey.Dashboard else NavKey.Login
                val backStack = remember { mutableStateListOf<Any>(initialKey) }

                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator()
                    ),
                    entryProvider = entryProvider {
                        entry<NavKey.Login> {
                            val viewModel = viewModel<AuthViewModel>(
                                factory = object : ViewModelProvider.Factory {
                                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                        return AuthViewModel(authRepository) as T
                                    }
                                }
                            )
                            LoginScreen(
                                viewModel = viewModel,
                                biometricHelper = biometricHelper,
                                onLoginSuccess = {
                                    backStack.clear()
                                    backStack.add(NavKey.Dashboard)
                                }
                            )
                        }
                        entry<NavKey.Dashboard> {
                            val dashViewModel = viewModel<DashboardViewModel>(
                                factory = object : ViewModelProvider.Factory {
                                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                        return DashboardViewModel(authRepository, walletRepository) as T
                                    }
                                }
                            )
                            val txViewModel = viewModel<TransactionViewModel>(
                                factory = object : ViewModelProvider.Factory {
                                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                        return TransactionViewModel(authRepository, transactionRepository) as T
                                    }
                                }
                            )
                            DashboardScreen(
                                viewModel = dashViewModel,
                                transactionViewModel = txViewModel,
                                onLogout = {
                                    dashViewModel.logout()
                                    backStack.clear()
                                    backStack.add(NavKey.Login)
                                },
                                onNavigateToVTU = { backStack.add(NavKey.VTU) },
                                onNavigateToUtilities = { backStack.add(NavKey.Utilities) }
                            )
                        }
                        entry<NavKey.VTU> {
                            val vtuViewModel = viewModel<VTUViewModel>(
                                factory = object : ViewModelProvider.Factory {
                                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                        return VTUViewModel(authRepository, walletRepository, transactionRepository) as T
                                    }
                                }
                            )
                            VTUScreen(
                                viewModel = vtuViewModel,
                                initialTab = 0,
                                onBack = { backStack.removeLastOrNull() }
                            )
                        }
                        entry<NavKey.Utilities> {
                            val vtuViewModel = viewModel<VTUViewModel>(
                                factory = object : ViewModelProvider.Factory {
                                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                        return VTUViewModel(authRepository, walletRepository, transactionRepository) as T
                                    }
                                }
                            )
                            VTUScreen(
                                viewModel = vtuViewModel,
                                initialTab = 1,
                                onBack = { backStack.removeLastOrNull() }
                            )
                        }
                    }
                )
            }
        }
    }
}
