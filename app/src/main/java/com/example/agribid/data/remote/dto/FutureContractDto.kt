package com.example.agribid.data.remote.dto // <-- FIX: Package name corrected

import com.google.firebase.firestore.PropertyName

data class FutureContractDto(
    @get:PropertyName("contract_id")
    @set:PropertyName("contract_id")
    var contractId: String = "",
    @get:PropertyName("farmer_id")
    @set:PropertyName("farmer_id")
    var farmerId: String = "",
    @get:PropertyName("buyer_id")
    @set:PropertyName("buyer_id")
    var buyerId: String = "",
    @get:PropertyName("forecast_id")
    @set:PropertyName("forecast_id")
    var forecastId: String = "",
    @get:PropertyName("price_per_unit_usd")
    @set:PropertyName("price_per_unit_usd")
    var pricePerUnitUSD: Double = 0.0,
    @get:PropertyName("contracted_quantity_kg")
    @set:PropertyName("contracted_quantity_kg")
    var contractedQuantityKg: Double = 0.0,
    @get:PropertyName("delivery_date")
    @set:PropertyName("delivery_date")
    var deliveryDate: Long = 0L,
    var status: String = "PENDING",
    @get:PropertyName("total_value_usd")
    @set:PropertyName("total_value_usd")
    var totalValueUSD: Double = 0.0
)
