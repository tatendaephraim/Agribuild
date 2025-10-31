package com.example.agribid.data.mapper // <-- FIX: Moved to 'mapper' package

import com.example.agribid.data.local.db.ProductEntity
import com.example.agribid.data.remote.dto.BidDto
import com.example.agribid.data.remote.dto.FutureContractDto
import com.example.agribid.data.remote.dto.HarvestForecastDto
import com.example.agribid.data.remote.dto.ProductDto
import com.example.agribid.data.remote.dto.UserDto
import com.example.agribid.domain.model.Bid
import com.example.agribid.domain.model.ContractStatus
import com.example.agribid.domain.model.FutureContract
import com.example.agribid.domain.model.HarvestForecast
import com.example.agribid.domain.model.ProductListing
import com.example.agribid.domain.model.User
import com.example.agribid.domain.model.UserRole

// --- User Mappers ---
fun UserDto.toDomainModel(): User {
    return User(
        userId = this.userId,
        role = try { UserRole.valueOf(this.role) } catch (e: Exception) { UserRole.BUYER },
        name = this.name,
        country = this.country,
        province = this.province,
        district = this.district,
        latitude = this.latitude,
        longitude = this.longitude,
        rating = this.rating,
        isExporter = this.isExporter,
        businessName = this.businessName,
        certifications = this.certifications
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        userId = this.userId,
        role = this.role.name,
        name = this.name,
        country = this.country,
        province = this.province,
        district = this.district,
        latitude = this.latitude,
        longitude = this.longitude,
        rating = this.rating,
        isExporter = this.isExporter,
        businessName = this.businessName,
        certifications = this.certifications
    )
}


// --- Product Mappers ---
fun ProductDto.toDomainModel(): ProductListing {
    return ProductListing(
        productId = this.productId,
        farmerId = this.farmerId,
        productName = this.productName,
        quantityKg = this.quantityKg,
        basePriceUSD = this.basePriceUSD,
        latitude = this.latitude,
        longitude = this.longitude,
        country = this.country,
        province = this.province,
        district = this.district,
        imageUrl = this.imageUrl,
        createdAt = this.createdAt
    )
}

fun ProductEntity.toDomainModel(): ProductListing {
    return ProductListing(
        productId = this.productId,
        farmerId = this.farmerId,
        productName = this.productName,
        quantityKg = this.quantityKg,
        basePriceUSD = this.basePriceUSD,
        latitude = this.latitude,
        longitude = this.longitude,
        country = this.country,
        province = this.province,
        district = this.district,
        imageUrl = this.imageUrl,
        createdAt = this.createdAt
    )
}

// FIX: Corrected the parameter name here
fun ProductDto.toEntity(dbCountryKey: String): ProductEntity {
    return ProductEntity(
        productId = this.productId,
        farmerId = this.farmerId,
        productName = this.productName,
        quantityKg = this.quantityKg,
        basePriceUSD = this.basePriceUSD,
        latitude = this.latitude,
        longitude = this.longitude,
        country = dbCountryKey, // Use the provided key for the DB country field
        province = this.province,
        district = this.district,
        imageUrl = this.imageUrl,
        createdAt = this.createdAt
    )
}

fun ProductListing.toDto(): ProductDto {
    return ProductDto(
        productId = this.productId,
        farmerId = this.farmerId,
        productName = this.productName,
        quantityKg = this.quantityKg,
        basePriceUSD = this.basePriceUSD,
        latitude = this.latitude,
        longitude = this.longitude,
        country = this.country,
        province = this.province,
        district = this.district,
        imageUrl = this.imageUrl,
        createdAt = this.createdAt
    )
}

// --- Bid Mappers ---
fun BidDto.toDomainModel(): Bid {
    return Bid(
        bidId = this.bidId,
        buyerId = this.buyerId,
        productId = this.productId,
        offeredPriceUSD = this.offeredPriceUSD,
        quantityKg = this.quantityKg,
        status = try {
            ContractStatus.valueOf(this.status)
        } catch (e: Exception) {
            ContractStatus.PENDING
        },
        createdAt = this.createdAt
    )
}

fun Bid.toDto(): BidDto {
    return BidDto(
        bidId = this.bidId,
        buyerId = this.buyerId,
        productId = this.productId,
        offeredPriceUSD = this.offeredPriceUSD,
        quantityKg = this.quantityKg,
        status = this.status.name,
        createdAt = this.createdAt
    )
}

// --- Forecast Mappers ---
fun HarvestForecastDto.toDomainModel(): HarvestForecast {
    return HarvestForecast(
        forecastId = this.forecastId,
        farmerId = this.farmerId,
        productName = this.productName,
        estimatedHarvestDate = this.estimatedHarvestDate,
        estimatedQuantityKg = this.estimatedQuantityKg,
        pricePerKgUSD = this.pricePerKgUSD,
        country = this.country,
        province = this.province
    )
}

fun HarvestForecast.toDto(): HarvestForecastDto {
    return HarvestForecastDto(
        forecastId = this.forecastId,
        farmerId = this.farmerId,
        productName = this.productName,
        estimatedHarvestDate = this.estimatedHarvestDate,
        estimatedQuantityKg = this.estimatedQuantityKg,
        pricePerKgUSD = this.pricePerKgUSD,
        country = this.country,
        province = this.province
    )
}

// --- Contract Mappers ---
fun FutureContractDto.toDomainModel(): FutureContract {
    return FutureContract(
        contractId = this.contractId,
        farmerId = this.farmerId,
        buyerId = this.buyerId,
        forecastId = this.forecastId,
        pricePerUnitUSD = this.pricePerUnitUSD,
        contractedQuantityKg = this.contractedQuantityKg,
        deliveryDate = this.deliveryDate,
        status = try {
            ContractStatus.valueOf(this.status)
        } catch (e: Exception) {
            ContractStatus.PENDING
        },
        totalValueUSD = this.totalValueUSD
    )
}

fun FutureContract.toDto(): FutureContractDto {
    return FutureContractDto(
        contractId = this.contractId,
        farmerId = this.farmerId,
        buyerId = this.buyerId,
        forecastId = this.forecastId,
        pricePerUnitUSD = this.pricePerUnitUSD,
        contractedQuantityKg = this.contractedQuantityKg,
        deliveryDate = this.deliveryDate,
        status = this.status.name,
        totalValueUSD = this.totalValueUSD
    )
}
