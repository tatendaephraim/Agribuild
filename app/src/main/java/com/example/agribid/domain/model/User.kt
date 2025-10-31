package com.example.agribid.domain.model // <-- FIX: Package name corrected

data class User(
    val userId: String = "",
    val role: UserRole = UserRole.BUYER,
    val name: String = "",
    val country: String = "",
    val province: String = "",
    val district: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val rating: Double = 0.0,
    val isExporter: Boolean = false,
    val businessName: String = "",
    val certifications: List<String> = emptyList()
)
