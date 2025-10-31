package com.example.agribid.data.repository // <-- FIX: Package name corrected

import com.example.agribid.data.mapper.toDomainModel
import com.example.agribid.data.mapper.toDto
import com.example.agribid.data.remote.ForecastRemoteDataSource
import com.example.agribid.domain.model.HarvestForecast
import com.example.agribid.domain.repository.ForecastRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // <-- FIX: Added annotation
class ForecastRepositoryImpl @Inject constructor( // <-- FIX: Added @Inject
    private val remoteDataSource: ForecastRemoteDataSource
) : ForecastRepository {

    override suspend fun createForecast(forecast: HarvestForecast): Result<String> {
        return remoteDataSource.createForecast(forecast.toDto())
    }

    override fun getForecasts(country: String?, province: String?): Flow<List<HarvestForecast>> {
        return remoteDataSource.getForecasts(country, province)
            .map { dtoList ->
                dtoList.map { it.toDomainModel() }
            }
    }

    override suspend fun getForecastById(id: String): HarvestForecast? {
        return remoteDataSource.getForecastById(id)?.toDomainModel()
    }
}
