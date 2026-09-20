package com.example.greatvtu.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.NetworkCell
import androidx.compose.material.icons.rounded.OfflineBolt
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.greatvtu.ui.navigation.NavKey
import com.example.greatvtu.viewmodel.DashboardViewModel
import com.example.greatvtu.viewmodel.TransactionViewModel
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    transactionViewModel: TransactionViewModel,
    onLogout: () -> Unit,
    onNavigateToVTU: () -> Unit,
    onNavigateToUtilities: () -> Unit
) {
    var currentDestination by remember { mutableStateOf<NavKey>(NavKey.Home) }
    val balance by viewModel.walletBalance.collectAsState()

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            item(
                selected = currentDestination == NavKey.Home,
                onClick = { currentDestination = NavKey.Home },
                icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
                label = { Text("Home") }
            )
            item(
                selected = currentDestination == NavKey.Transactions,
                onClick = { currentDestination = NavKey.Transactions },
                icon = { Icon(Icons.Rounded.History, contentDescription = "Transactions") },
                label = { Text("History") }
            )
            item(
                selected = currentDestination == NavKey.Profile,
                onClick = { currentDestination = NavKey.Profile },
                icon = { Icon(Icons.Rounded.Person, contentDescription = "Profile") },
                label = { Text("Profile") }
            )
        }
    ) {
        Scaffold(
            floatingActionButton = {
                if (currentDestination == NavKey.Home) {
                    LargeFloatingActionButton(
                        onClick = onNavigateToVTU,
                        shape = RoundedCornerShape(28.dp),
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = "Quick Buy", modifier = Modifier.size(36.dp))
                    }
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when (currentDestination) {
                    is NavKey.Home -> HomeScreen(balance, onNavigateToVTU, onNavigateToUtilities)
                    is NavKey.Transactions -> TransactionsScreen(transactionViewModel)
                    is NavKey.Profile -> ProfileScreen(onLogout)
                    else -> HomeScreen(balance, onNavigateToVTU, onNavigateToUtilities)
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    balance: Double,
    onNavigateToVTU: () -> Unit,
    onNavigateToUtilities: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "GREAT VTU",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth().height(160.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Wallet Balance",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "₦${String.format(Locale.getDefault(), "%,.2f", balance)}",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Services",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.Start),
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ServiceCard(
                name = "VTU Airtime/Data",
                icon = Icons.Rounded.NetworkCell,
                onClick = onNavigateToVTU,
                modifier = Modifier.weight(1f)
            )
            ServiceCard(
                name = "Bills & Utilities",
                icon = Icons.Rounded.OfflineBolt,
                onClick = onNavigateToUtilities,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ServiceCard(name: String, icon: ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
            Spacer(modifier = Modifier.height(8.dp))
            Text(name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Rounded.Person, contentDescription = null, modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))
        Text("Profile Screen", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Logout", fontWeight = FontWeight.Bold)
        }
    }
}
