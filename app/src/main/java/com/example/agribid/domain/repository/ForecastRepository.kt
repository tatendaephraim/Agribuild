package com.example.agribid.domain.repository // <-- FIX: Package name corrected


import com.example.agribid.domain.model.HarvestForecast
import kotlinx.coroutines.flow.Flow

interface ForecastRepository {
    suspend fun createForecast(forecast: HarvestForecast): Result<String>
    fun getForecasts(country: String?, province: String?): Flow<List<HarvestForecast>>
    suspend fun getForecastById(id: String): HarvestForecast?
}
