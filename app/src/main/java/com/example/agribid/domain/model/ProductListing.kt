package com.example.agribid.domain.model // <-- FIX: Package name corrected

data class ProductListing(
    val productId: String = "",
    val farmerId: String = "",
    val productName: String = "",
    val quantityKg: Double = 0.0,
    val basePriceUSD: Double = 0.0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val country: String = "",
    val province: String = "",
    val district: String = "",
    val imageUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    var distanceKm: Double = 0.0 // Calculated field
)
