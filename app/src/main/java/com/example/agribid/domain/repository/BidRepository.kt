package com.example.agribid.domain.repository // <-- FIX: Package name corrected


import com.example.agribid.domain.model.Bid
import com.example.agribid.domain.model.ContractStatus
import kotlinx.coroutines.flow.Flow

interface BidRepository {
    suspend fun createBid(bid: Bid): Result<String>
    fun getBidsForProduct(productId: String): Flow<List<Bid>>
    fun getBidsForBuyer(buyerId: String): Flow<List<Bid>>
    suspend fun updateBidStatus(bidId: String, status: ContractStatus): Result<Unit>
}
