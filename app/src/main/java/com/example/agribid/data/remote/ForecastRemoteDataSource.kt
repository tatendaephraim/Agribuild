package com.example.agribid.data.remote

import com.example.agribid.data.remote.dto.HarvestForecastDto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose // <-- FIX: Added missing import
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ForecastRemoteDataSource @Inject constructor(
    private val db: FirebaseFirestore
) {

    suspend fun createForecast(forecastDto: HarvestForecastDto): Result<String> {
        return try {
            val docRef = db.collection("forecasts").document(forecastDto.forecastId)
            docRef.set(forecastDto).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getForecasts(country: String?, province: String?): Flow<List<HarvestForecastDto>> = callbackFlow {
        var query: Query = db.collection("forecasts")

        if (country != null) {
            query = query.whereEqualTo("country", country)
        }
        if (province != null) {
            query = query.whereEqualTo("province", province)
        }

        val listener = query.orderBy("estimatedHarvestDate", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(snapshot.toObjects(HarvestForecastDto::class.java))
                }
            }
        awaitClose { listener.remove() } // This now resolves
    }

    suspend fun getForecastById(id: String): HarvestForecastDto? {
        return try {
            db.collection("forecasts").document(id).get().await()
                .toObject(HarvestForecastDto::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
