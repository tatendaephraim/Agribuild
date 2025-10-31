package com.example.agribid.domain.model // <-- FIX: Package name corrected
data class FutureContract(
    val contractId: String = "",
    val farmerId: String = "",
    val buyerId: String = "",
    val forecastId: String = "",
    val pricePerUnitUSD: Double = 0.0,
    val contractedQuantityKg: Double = 0.0,
    val deliveryDate: Long = 0L,
    val status: ContractStatus = ContractStatus.PENDING,
    val totalValueUSD: Double = 0.0
)
