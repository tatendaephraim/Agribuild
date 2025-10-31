package com.example.agribid.data.remote.dto // <-- FIX: Package name corrected

import com.google.firebase.firestore.PropertyName

// This model maps to your 'users' collection in Firestore
data class UserDto(
    @get:PropertyName("user_id")
    @set:PropertyName("user_id")
    var userId: String = "",
    var role: String = "BUYER",
    var name: String = "",
    var country: String = "",
    var province: String = "",
    var district: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var rating: Double = 0.0,
    @get:PropertyName("is_exporter")
    @set:PropertyName("is_exporter")
    var isExporter: Boolean = false,
    @get:PropertyName("business_name")
    @set:PropertyName("business_name")
    var businessName: String = "",
    var certifications: List<String> = emptyList()
)
