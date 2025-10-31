package com.example.agribid.core.di // <-- FIX: Package name corrected

import com.example.agribid.data.repository.BidRepositoryImpl
import com.example.agribid.data.repository.ContractRepositoryImpl
import com.example.agribid.data.repository.ForecastRepositoryImpl
import com.example.agribid.data.repository.ProductRepositoryImpl
import com.example.agribid.data.repository.UserRepositoryImpl
import com.example.agribid.domain.repository.BidRepository
import com.example.agribid.domain.repository.ContractRepository
import com.example.agribid.domain.repository.ForecastRepository
import com.example.agribid.domain.repository.ProductRepository
import com.example.agribid.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindBidRepository(
        bidRepositoryImpl: BidRepositoryImpl
    ): BidRepository

    @Binds
    @Singleton
    abstract fun bindForecastRepository(
        forecastRepositoryImpl: ForecastRepositoryImpl
    ): ForecastRepository

    @Binds
    @Singleton
    abstract fun bindContractRepository(
        contractRepositoryImpl: ContractRepositoryImpl
    ): ContractRepository
}
