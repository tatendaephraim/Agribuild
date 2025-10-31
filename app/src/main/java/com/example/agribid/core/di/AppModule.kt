package com.example.agribid.core.di

import android.content.Context
import androidx.room.Room
import com.example.agribid.data.local.AppDatabase
import com.example.agribid.data.local.db.ProductDao
import com.example.agribid.data.remote.AuthRemoteDataSource
import com.example.agribid.data.remote.BidRemoteDataSource
import com.example.agribid.data.remote.ContractRemoteDataSource
import com.example.agribid.data.remote.ForecastRemoteDataSource
import com.example.agribid.data.remote.ProductRemoteDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth // Correct KTX import
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore // Correct KTX import
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // --- Firebase ---
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = Firebase.firestore

    // --- Room Database ---
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext, // Use application context
            AppDatabase::class.java,
            "agribid_database" // Name for the database file
        )
            .fallbackToDestructiveMigration() // Simple migration strategy for development
            .build()
    }

    @Provides
    @Singleton
    fun provideProductDao(db: AppDatabase): ProductDao {
        return db.productDao()
    }

    // --- Data Sources (Inject dependencies) ---
    @Provides
    @Singleton
    fun provideAuthRemoteDataSource(auth: FirebaseAuth, firestore: FirebaseFirestore): AuthRemoteDataSource {
        return AuthRemoteDataSource(auth, firestore)
    }

    @Provides
    @Singleton
    fun provideProductRemoteDataSource(firestore: FirebaseFirestore): ProductRemoteDataSource {
        return ProductRemoteDataSource(firestore)
    }

    @Provides
    @Singleton
    fun provideBidRemoteDataSource(firestore: FirebaseFirestore): BidRemoteDataSource {
        return BidRemoteDataSource(firestore)
    }

    @Provides
    @Singleton
    fun provideForecastRemoteDataSource(firestore: FirebaseFirestore): ForecastRemoteDataSource {
        return ForecastRemoteDataSource(firestore)
    }

    @Provides
    @Singleton
    fun provideContractRemoteDataSource(firestore: FirebaseFirestore): ContractRemoteDataSource {
        return ContractRemoteDataSource(firestore)
    }

    // --- Coroutine Scope ---
    // Provides a singleton scope for background tasks managed by the application lifecycle
    // **Add the @ApplicationScope qualifier to the provider function**
    @ApplicationScope // Specify which scope this provides
    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

}

