package com.example.agribid.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.res.painterResource
import com.agribid.R
import com.example.agribid.domain.model.ProductListing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListItem(
    product: ProductListing,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(), // Ensure Card fills width
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface // Use theme surface color
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth() // Ensure Row fills width
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image with Coil
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(product.imageUrl.ifBlank { null }) // Pass null if URL is blank
                    .crossfade(true)
                    .placeholder(com.google.android.gms.base.R.drawable.common_full_open_on_phone) // <-- FIXED: Show this while loading
                    .error(com.google.firebase.database.collection.R.drawable.common_google_signin_btn_icon_dark_normal)       // Show this if loading fails
                    .fallback(com.google.firebase.appcheck.interop.R.drawable.common_google_signin_btn_icon_dark_normal_background)      // Show this if data is null
                    .build(),
                contentDescription = product.productName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.medium)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Text Content Column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.productName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$${String.format("%.2f", product.basePriceUSD)}/kg",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Location Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${product.district}, ${product.province}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Quantity Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Scale,
                        contentDescription = "Quantity",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        // Format quantity nicely (e.g., as Int if no decimal)
                        text = "Available: ${
                            try {
                                product.quantityKg.toInt().toString()
                            } catch (e: Exception) {
                                "%.1f".format(product.quantityKg)
                            }
                        } kg",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } // End Text Column

            Spacer(modifier = Modifier.width(8.dp)) // Add some space before distance

            // Distance Text (only if valid distance)
            if (product.distanceKm.isFinite() && product.distanceKm >= 0) {
                Text(
                    text = "${String.format("%.0f", product.distanceKm)} km",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary, // Use secondary theme color
                    modifier = Modifier.align(Alignment.Bottom) // Align to bottom of the row
                )
            }
        } // End Row
    } // End Card
}
