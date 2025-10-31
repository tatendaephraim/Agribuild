package com.example.agribid.presentation.buyer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.agribid.domain.model.ProductListing
import com.example.agribid.presentation.components.ProductListItem

@Composable
fun ProductListScreen(
    viewModel: BuyerViewModel,
    onProductClick: (ProductListing) -> Unit
) {
    val products by viewModel.productListings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (products.isEmpty()) {
                Text("No products found.", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn {
                    items(products) { product ->
                        ProductListItem(
                            product = product,
                            onClick = { onProductClick(product) }
                        )
                    }
                }
            }
        }
    }
}