package com.example.greatvtu.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.NetworkCell
import androidx.compose.material.icons.rounded.OfflineBolt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.greatvtu.ui.theme.GREATVTUTheme
import com.example.greatvtu.viewmodel.VTUViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VTUScreen(
    viewModel: VTUViewModel?,
    initialTab: Int = 0,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    val vtuStatus by viewModel?.vtuStatus?.collectAsState(initial = null) ?: remember { mutableStateOf(null) }
    val billStatus by viewModel?.billStatus?.collectAsState(initial = null) ?: remember { mutableStateOf(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (selectedTab == 0) "VTU Services" else "Utility Bills") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Go Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                SegmentedButton(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0; viewModel?.clearStatus() },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.NetworkCell, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Airtime/Data")
                    }
                }
                SegmentedButton(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1; viewModel?.clearStatus() },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.OfflineBolt, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Bills/Utility")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTab == 0) {
                VTUForm(
                    status = vtuStatus,
                    onPurchase = { network, type, amt, phone ->
                        viewModel?.purchaseVTU(network, type, amt, phone)
                    }
                )
            } else {
                UtilityForm(
                    status = billStatus,
                    onPay = { provider, type, amt, acc ->
                        viewModel?.payUtility(provider, type, amt, acc)
                    }
                )
            }
        }
    }
}

@Composable
fun VTUForm(
    status: String?,
    onPurchase: (network: String, type: String, amount: Double, phone: String) -> Unit
) {
    var network by remember { mutableStateOf("MTN") }
    var vtuType by remember { mutableStateOf("Airtime") }
    var amountStr by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    val networks = listOf("MTN", "Airtel", "Glo", "9mobile")
    val types = listOf("Airtime", "Data")

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Select Network Provider", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier.fillMaxWidth().selectableGroup().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            networks.forEach { text ->
                Row(
                    modifier = Modifier.selectable(
                        selected = (network == text),
                        onClick = { network = text },
                        role = Role.RadioButton
                    ).padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = (network == text), onClick = null)
                    Text(text = text, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Select Service Type", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier.fillMaxWidth().selectableGroup().padding(vertical = 8.dp)
        ) {
            types.forEach { text ->
                Row(
                    modifier = Modifier.selectable(
                        selected = (vtuType == text),
                        onClick = { vtuType = text },
                        role = Role.RadioButton
                    ).padding(end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = (vtuType == text), onClick = null)
                    Text(text = text, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = amountStr,
            onValueChange = { amountStr = it },
            label = { Text("Amount (₦)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val amt = amountStr.toDoubleOrNull() ?: 0.0
                onPurchase(network, vtuType, amt, phone)
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Complete Transaction", fontWeight = FontWeight.Bold)
        }

        status?.let {
            Spacer(modifier = Modifier.height(16.dp))
            StatusMessageCard(message = it)
        }
    }
}

@Composable
fun UtilityForm(
    status: String?,
    onPay: (provider: String, type: String, amount: Double, account: String) -> Unit
) {
    var provider by remember { mutableStateOf("DSTV") }
    var billType by remember { mutableStateOf("Cable TV") }
    var amountStr by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }

    val providers = listOf("DSTV", "GOTV", "IKEDC", "EKEDC")
    val types = listOf("Cable TV", "Electricity")

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Select Utility Provider", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier.fillMaxWidth().selectableGroup().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            providers.forEach { text ->
                Row(
                    modifier = Modifier.selectable(
                        selected = (provider == text),
                        onClick = {
                            provider = text
                            billType = if (text == "DSTV" || text == "GOTV") "Cable TV" else "Electricity"
                        },
                        role = Role.RadioButton
                    ).padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = (provider == text), onClick = null)
                    Text(text = text, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = accountNumber,
            onValueChange = { accountNumber = it },
            label = { Text(if (billType == "Cable TV") "SmartCard Number" else "Meter Number") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = amountStr,
            onValueChange = { amountStr = it },
            label = { Text("Amount (₦)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val amt = amountStr.toDoubleOrNull() ?: 0.0
                onPay(provider, billType, amt, accountNumber)
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Pay Bill", fontWeight = FontWeight.Bold)
        }

        status?.let {
            Spacer(modifier = Modifier.height(16.dp))
            StatusMessageCard(message = it)
        }
    }
}

@Composable
fun StatusMessageCard(message: String) {
    val isError = message.startsWith("Error")
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = null,
                tint = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun VTUScreenPreview() {
    GREATVTUTheme {
        VTUScreen(viewModel = null, onBack = {})
    }
}
