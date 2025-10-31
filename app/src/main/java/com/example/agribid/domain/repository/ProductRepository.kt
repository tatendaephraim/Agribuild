package com.example.agribid.domain.repository // <-- FIX: Package name corrected


import com.example.agribid.domain.model.ProductListing
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProductListings(country: String?, province: String?): Flow<List<ProductListing>>
    suspend fun createListing(listing: ProductListing): Result<String>
    suspend fun getListingById(id: String): ProductListing?
}
