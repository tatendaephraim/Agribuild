package com.example.agribid.data.remote.dto // <-- FIX: Package name corrected

import com.google.firebase.firestore.PropertyName

data class HarvestForecastDto(
    @get:PropertyName("forecast_id")
    @set:PropertyName("forecast_id")
    var forecastId: String = "",
    @get:PropertyName("farmer_id")
    @set:PropertyName("farmer_id")
    var farmerId: String = "",
    @get:PropertyName("product_name")
    @set:PropertyName("product_name")
    var productName: String = "",
    @get:PropertyName("estimated_harvest_date")
    @set:PropertyName("estimated_harvest_date")
    var estimatedHarvestDate: Long = 0L,
    @get:PropertyName("estimated_quantity_kg")
    @set:PropertyName("estimated_quantity_kg")
    var estimatedQuantityKg: Double = 0.0,
    @get:PropertyName("price_per_kg_usd")
    @set:PropertyName("price_per_kg_usd")
    var pricePerKgUSD: Double = 0.0,
    var country: String = "",
    var province: String = ""
)
