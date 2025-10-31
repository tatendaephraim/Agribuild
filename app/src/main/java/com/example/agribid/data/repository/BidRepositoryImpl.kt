package com.example.agribid.data.repository

import com.example.agribid.data.mapper.toDomainModel // Corrected import
import com.example.agribid.data.mapper.toDto       // Corrected import
import com.example.agribid.data.remote.BidRemoteDataSource
import com.example.agribid.domain.model.Bid
import com.example.agribid.domain.model.ContractStatus
import com.example.agribid.domain.repository.BidRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map // Ensure correct Flow map import
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BidRepositoryImpl @Inject constructor(
    private val remoteDataSource: BidRemoteDataSource
) : BidRepository {

    override suspend fun createBid(bid: Bid): Result<String> {
        val bidDto = bid.toDto().copy( // Use mapper
            bidId = if (bid.bidId.isEmpty()) "" else bid.bidId
        )
        return remoteDataSource.createBid(bidDto)
    }

    override fun getBidsForProduct(productId: String): Flow<List<Bid>> {
        return remoteDataSource.getBidsForProduct(productId)
            .map { dtoList ->
                // Correct mapping: Apply toDomainModel to each element
                dtoList.map { dto -> dto.toDomainModel() } // Use mapper
            }
    }

    override fun getBidsForBuyer(buyerId: String): Flow<List<Bid>> {
        return remoteDataSource.getBidsForBuyer(buyerId)
            .map { dtoList ->
                // Correct mapping: Apply toDomainModel to each element
                dtoList.map { dto -> dto.toDomainModel() } // Use mapper
            }
    }

    override suspend fun updateBidStatus(bidId: String, status: ContractStatus): Result<Unit> {
        return remoteDataSource.updateBidStatus(bidId, status)
    }
}

