package com.example.agribid.data.remote

import com.example.agribid.data.remote.dto.FutureContractDto
import com.example.agribid.domain.model.ContractStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose // <-- FIX: Added missing import
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ContractRemoteDataSource @Inject constructor(
    private val db: FirebaseFirestore
) {
    suspend fun createContract(contractDto: FutureContractDto): Result<String> {
        return try {
            val docRef = db.collection("contracts").document(contractDto.contractId)
            docRef.set(contractDto).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateContractStatus(contractId: String, status: ContractStatus): Result<Unit> {
        return try {
            db.collection("contracts").document(contractId)
                .update("status", status.name)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getContractsForFarmer(farmerId: String): Flow<List<FutureContractDto>> = callbackFlow {
        val listener = db.collection("contracts")
            .whereEqualTo("farmerId", farmerId)
            .orderBy("deliveryDate", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(snapshot.toObjects(FutureContractDto::class.java))
                }
            }
        awaitClose { listener.remove() } // This now resolves
    }

    fun getContractsForBuyer(buyerId: String): Flow<List<FutureContractDto>> = callbackFlow {
        val listener = db.collection("contracts")
            .whereEqualTo("buyerId", buyerId)
            .orderBy("deliveryDate", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(snapshot.toObjects(FutureContractDto::class.java))
                }
            }
        awaitClose { listener.remove() } // This now resolves
    }
}
