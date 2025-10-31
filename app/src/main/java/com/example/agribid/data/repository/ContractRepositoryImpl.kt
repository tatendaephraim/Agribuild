package com.example.agribid.data.repository // <-- FIX: Package name corrected

import com.example.agribid.data.mapper.toDomainModel
import com.example.agribid.data.mapper.toDto
import com.example.agribid.data.remote.ContractRemoteDataSource
import com.example.agribid.domain.model.ContractStatus
import com.example.agribid.domain.model.FutureContract
import com.example.agribid.domain.repository.ContractRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // <-- FIX: Added annotation
class ContractRepositoryImpl @Inject constructor( // <-- FIX: Added @Inject
    private val remoteDataSource: ContractRemoteDataSource
) : ContractRepository {

    override suspend fun createContract(contract: FutureContract): Result<String> {
        return remoteDataSource.createContract(contract.toDto())
    }

    override fun getContractsForFarmer(farmerId: String): Flow<List<FutureContract>> {
        return remoteDataSource.getContractsForFarmer(farmerId)
            .map { dtoList ->
                dtoList.map { it.toDomainModel() }
            }
    }

    override fun getContractsForBuyer(buyerId: String): Flow<List<FutureContract>> {
        return remoteDataSource.getContractsForBuyer(buyerId)
            .map { dtoList ->
                dtoList.map { it.toDomainModel() }
            }
    }

    override suspend fun updateContractStatus(contractId: String, status: ContractStatus): Result<Unit> {
        return remoteDataSource.updateContractStatus(contractId, status)
    }
}
