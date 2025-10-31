package com.example.agribid.data.remote.dto // <-- FIX: Package name corrected

import com.google.firebase.firestore.PropertyName

data class ProductDto(
    @get:PropertyName("product_id")
    @set:PropertyName("product_id")
    var productId: String = "",
    @get:PropertyName("farmer_id")
    @set:PropertyName("farmer_id")
    var farmerId: String = "",
    @get:PropertyName("product_name")
    @set:PropertyName("product_name")
    var productName: String = "",
    @get:PropertyName("quantity_kg")
    @set:PropertyName("quantity_kg")
    var quantityKg: Double = 0.0,
    @get:PropertyName("base_price_usd")
    @set:PropertyName("base_price_usd")
    var basePriceUSD: Double = 0.0,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var country: String = "",
    var province: String = "",
    var district: String = "",
    @get:PropertyName("image_url")
    @set:PropertyName("image_url")
    var imageUrl: String = "",
    @get:PropertyName("created_at")
    @set:PropertyName("created_at")
    var createdAt: Long = System.currentTimeMillis()
)
