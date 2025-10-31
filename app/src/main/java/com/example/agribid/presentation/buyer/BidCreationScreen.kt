package com.example.agribid.presentation.buyer // <-- FIX: Package name corrected

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BidCreationScreen(
    viewModel: BuyerViewModel,
    onBidSubmitted: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val listing by viewModel.currentListing.collectAsState()
    val bidAmount by viewModel.bidAmount.collectAsState()
    val bidQuantity by viewModel.bidQuantity.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Place Bid") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->

        listing?.let { product ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = product.productName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Base Price: $${product.basePriceUSD}/kg")
                        Text("Available: ${product.quantityKg} kg")
                        Text("Distance: ${String.format("%.1f", product.distanceKm)} km")
                    }
                }

                OutlinedTextField(
                    value = bidAmount,
                    onValueChange = { viewModel.updateBidAmount(it) },
                    label = { Text("Your Bid (USD per kg)") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = bidQuantity,
                    onValueChange = { viewModel.updateBidQuantity(it) },
                    label = { Text("Quantity (kg)") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Scale, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                val bidPrice = bidAmount.toDoubleOrNull() ?: 0.0
                val quantity = bidQuantity.toDoubleOrNull() ?: 0.0
                val transportCost = viewModel.calculateTransportCost(product.distanceKm)
                val productCost = bidPrice * quantity
                val totalCost = productCost + transportCost

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Cost Breakdown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        CostRow(label = "Product Cost:", value = "$${String.format("%.2f", productCost)}")
                        CostRow(label = "Est. Transport:", value = "$${String.format("%.2f", transportCost)}", isSubtle = true)
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        CostRow(label = "Total Est. Cost:", value = "$${String.format("%.2f", totalCost)}", isTotal = true)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        viewModel.submitBid()
                        onBidSubmitted()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = bidPrice > 0 && quantity > 0 && quantity <= product.quantityKg
                ) {
                    Icon(Icons.Default.Gavel, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Submit Bid")
                }
            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun CostRow(label: String, value: String, isSubtle: Boolean = false, isTotal: Boolean = false) {
    val contentColor = if (isSubtle) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
    val textStyle = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge
    val fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = textStyle, fontWeight = fontWeight, color = contentColor)
        Text(
            text = value,
            style = textStyle,
            fontWeight = fontWeight,
            color = if (isTotal) MaterialTheme.colorScheme.primary else contentColor
        )
    }
}
