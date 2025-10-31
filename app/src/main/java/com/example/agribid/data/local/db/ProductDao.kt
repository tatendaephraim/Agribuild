package com.example.agribid.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.agribid.data.local.db.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM product_listings")
    fun getAllListings(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM product_listings WHERE country = :country")
    fun getListingsByCountry(country: String): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Query("DELETE FROM product_listings WHERE country = :country")
    suspend fun deleteByCountry(country: String)

    @Query("DELETE FROM product_listings")
    suspend fun deleteAll()

    @Query("SELECT * FROM product_listings WHERE productId = :id")
    suspend fun getListingById(id: String): ProductEntity?
}
