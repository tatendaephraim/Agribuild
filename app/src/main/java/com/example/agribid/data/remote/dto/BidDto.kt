package com.example.agribid.data.remote.dto // <-- FIX: Package name corrected

import com.google.firebase.firestore.PropertyName

data class BidDto(
    @get:PropertyName("bid_id")
    @set:PropertyName("bid_id")
    var bidId: String = "",
    @get:PropertyName("buyer_id")
    @set:PropertyName("buyer_id")
    var buyerId: String = "",
    @get:PropertyName("product_id")
    @set:PropertyName("product_id")
    var productId: String = "",
    @get:PropertyName("offered_price_usd")
    @set:PropertyName("offered_price_usd")
    var offeredPriceUSD: Double = 0.0,
    @get:PropertyName("quantity_kg")
    @set:PropertyName("quantity_kg")
    var quantityKg: Double = 0.0,
    var status: String = "PENDING",
    @get:PropertyName("created_at")
    @set:PropertyName("created_at")
    var createdAt: Long = System.currentTimeMillis()
)
