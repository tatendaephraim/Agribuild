package com.example.agribid.domain.model // <-- FIX: Package name corrected

data class HarvestForecast(
    val forecastId: String = "",
    val farmerId: String = "",
    val productName: String = "",
    val estimatedHarvestDate: Long = 0L,
    val estimatedQuantityKg: Double = 0.0,
    val pricePerKgUSD: Double = 0.0,
    val country: String = "",
    val province: String = ""
)
