package com.example.agribid.data.local.db // <-- FIX: Package name corrected

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_listings")
data class ProductEntity(
    @PrimaryKey
    val productId: String,
    val farmerId: String,
    val productName: String,
    val quantityKg: Double,
    val basePriceUSD: Double,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val province: String,
    val district: String,
    val imageUrl: String,
    val createdAt: Long,
    val lastRefreshed: Long = System.currentTimeMillis()
)
