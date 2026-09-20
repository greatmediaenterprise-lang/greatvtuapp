package com.example.greatvtu.ui.screen

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.greatvtu.data.model.Transaction
import com.example.greatvtu.ui.theme.GREATVTUTheme
import com.example.greatvtu.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun TransactionsScreen(viewModel: TransactionViewModel) {
    val navigator = rememberListDetailPaneScaffoldNavigator<Transaction>()
    val transactions by viewModel.transactions.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val scope = rememberCoroutineScope()

    NavigableListDetailPaneScaffold(
        navigator = navigator,
        listPane = {
            AnimatedPane {
                TransactionListPane(
                    transactions = transactions,
                    searchQuery = searchQuery,
                    onSearchChange = viewModel::setSearchQuery,
                    onTransactionClick = { tx ->
                        scope.launch {
                            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, tx)
                        }
                    }
                )
            }
        },
        detailPane = {
            AnimatedPane {
                val selectedTx = navigator.currentDestination?.contentKey
                if (selectedTx != null) {
                    TransactionDetailPane(
                        transaction = selectedTx,
                        onClose = {
                            scope.launch {
                                navigator.navigateBack()
                            }
                        }
                    )
                } else {
                    EmptyDetailPane()
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListPane(
    transactions: List<Transaction>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onTransactionClick: (Transaction) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Transaction History",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search transactions...") },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            trailingIcon = if (searchQuery.isNotEmpty()) {
                { IconButton(onClick = { onSearchChange("") }) { Icon(Icons.Rounded.Close, contentDescription = null) } }
            } else null,
            shape = CircleShape
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (transactions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No transactions found", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(transactions) { tx ->
                    TransactionItem(tx, onClick = { onTransactionClick(tx) })
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}

@Composable
fun TransactionItem(tx: Transaction, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = if (tx.type == "VTU") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (tx.type == "VTU") Icons.Rounded.History else Icons.AutoMirrored.Rounded.ReceiptLong,
                    contentDescription = null,
                    tint = if (tx.type == "VTU") MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(tx.service_name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text(tx.recipient, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("₦${tx.amount}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
            Text(tx.status, style = MaterialTheme.typography.labelSmall, color = if (tx.status == "SUCCESS") Color(0xFF4CAF50) else Color.Red)
        }
    }
}

@Composable
fun TransactionDetailPane(transaction: Transaction, onClose: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Transaction Receipt", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onClose) {
                Icon(Icons.Rounded.Close, contentDescription = "Close")
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                ReceiptRow("Service", transaction.service_name)
                ReceiptRow("Type", transaction.type)
                ReceiptRow("Recipient", transaction.recipient)
                ReceiptRow("Amount", "₦${transaction.amount}")
                ReceiptRow("Status", transaction.status)
                ReceiptRow("Date", transaction.created_at)
                ReceiptRow("Reference", transaction.reference)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "GREAT VTU OFFICIAL RECEIPT",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline,
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ReceiptRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Spacer(modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun EmptyDetailPane() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ReceiptLong,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.outlineVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Select a transaction to view receipt", color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 600)
@Composable
fun TransactionsScreenPreview() {
    GREATVTUTheme {
        // We can't easily preview with ViewModel, but we can preview the layout if needed.
    }
}
