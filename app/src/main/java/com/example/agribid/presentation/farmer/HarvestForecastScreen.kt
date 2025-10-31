package com.example.agribid.presentation.farmer // <-- FIX: Package name corrected

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HarvestForecastScreen(
    viewModel: FarmerViewModel,
    onForecastSaved: () -> Unit
) {
    val productName by viewModel.productName.collectAsState()
    val estimatedQuantity by viewModel.estimatedQuantity.collectAsState()
    val pricePerKg by viewModel.pricePerKg.collectAsState()
    val harvestDate by viewModel.harvestDate.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Harvest Forecast") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Plan Your Future Harvest", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Let buyers know about your upcoming harvest and secure contracts in advance.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = productName,
                onValueChange = { viewModel.updateProductName(it) },
                label = { Text("Product Name (e.g., Cabbage)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Grass, contentDescription = null) }
            )

            OutlinedTextField(
                value = estimatedQuantity,
                onValueChange = { viewModel.updateEstimatedQuantity(it) },
                label = { Text("Estimated Quantity (kg)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Scale, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = pricePerKg,
                onValueChange = { viewModel.updatePricePerKg(it) },
                label = { Text("Expected Price (USD per kg)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = harvestDate?.let { dateFormatter.format(Date(it)) } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Harvest Date") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.EditCalendar, contentDescription = "Select Date")
                    }
                }
            )

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    viewModel.updateHarvestDate(it)
                                }
                                showDatePicker = false
                            }
                        ) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.saveForecast(onForecastSaved) },
                modifier = Modifier.fillMaxWidth(),
                enabled = productName.isNotBlank() &&
                        estimatedQuantity.toDoubleOrNull() != null &&
                        pricePerKg.toDoubleOrNull() != null &&
                        harvestDate != null
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Forecast")
            }
        }
    }
}
