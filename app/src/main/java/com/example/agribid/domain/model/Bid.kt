package com.example.agribid.domain.model // <-- FIX: Package name corrected
data class Bid(
    val bidId: String = "",
    val buyerId: String = "",
    val productId: String = "",
    val offeredPriceUSD: Double = 0.0,
    val quantityKg: Double = 0.0,
    val status: ContractStatus = ContractStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)
