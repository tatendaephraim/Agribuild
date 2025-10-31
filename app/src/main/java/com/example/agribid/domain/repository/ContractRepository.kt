package com.example.agribid.domain.repository // <-- FIX: Package name corrected


import com.example.agribid.domain.model.ContractStatus
import com.example.agribid.domain.model.FutureContract
import kotlinx.coroutines.flow.Flow

interface ContractRepository {
    suspend fun createContract(contract: FutureContract): Result<String>
    fun getContractsForFarmer(farmerId: String): Flow<List<FutureContract>>
    fun getContractsForBuyer(buyerId: String): Flow<List<FutureContract>>
    suspend fun updateContractStatus(contractId: String, status: ContractStatus): Result<Unit>
}
