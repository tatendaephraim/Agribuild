package com.example.agribid.data.remote

import com.example.agribid.data.remote.dto.BidDto
import com.example.agribid.domain.model.ContractStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose // <-- FIX: Added missing import
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BidRemoteDataSource @Inject constructor(
    private val db: FirebaseFirestore
) {

    suspend fun createBid(bidDto: BidDto): Result<String> {
        return try {
            val docRef = db.collection("bids").document(bidDto.bidId)
            docRef.set(bidDto).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBidStatus(bidId: String, status: ContractStatus): Result<Unit> {
        return try {
            db.collection("bids").document(bidId)
                .update("status", status.name)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getBidsForProduct(productId: String): Flow<List<BidDto>> = callbackFlow {
        val listener = db.collection("bids")
            .whereEqualTo("productId", productId)
            .orderBy("offeredPriceUSD", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(snapshot.toObjects(BidDto::class.java))
                }
            }
        awaitClose { listener.remove() } // This now resolves
    }

    fun getBidsForBuyer(buyerId: String): Flow<List<BidDto>> = callbackFlow {
        val listener = db.collection("bids")
            .whereEqualTo("buyerId", buyerId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(snapshot.toObjects(BidDto::class.java))
                }
            }
        awaitClose { listener.remove() } // This now resolves
    }
}
