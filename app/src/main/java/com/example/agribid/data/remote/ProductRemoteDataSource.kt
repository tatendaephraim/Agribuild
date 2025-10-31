package com.example.agribid.data.remote

import com.example.agribid.data.remote.dto.ProductDto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose // <-- FIX: Added missing import
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProductRemoteDataSource @Inject constructor( // <-- FIX: Added @Inject
    private val db: FirebaseFirestore
) {

    fun getProductListings(country: String?, province: String?): Flow<List<ProductDto>> = callbackFlow {
        var query: Query = db.collection("products")

        if (country != null) {
            query = query.whereEqualTo("country", country)
        }
        if (province != null) {
            query = query.whereEqualTo("province", province)
        }

        val listener = query.orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // Close the flow on error
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(snapshot.toObjects(ProductDto::class.java)) // Send the new list
                }
            }
        awaitClose { listener.remove() } // This now resolves
    }

    suspend fun createListing(productDto: ProductDto): Result<String> {
        return try {
            val documentRef = db.collection("products").document(productDto.productId)
            documentRef.set(productDto).await()
            Result.success(documentRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getListingById(id: String): ProductDto? {
        return try {
            db.collection("products").document(id).get().await()
                .toObject(ProductDto::class.java)
        } catch (e: Exception) {
            null
        }
    }
}

