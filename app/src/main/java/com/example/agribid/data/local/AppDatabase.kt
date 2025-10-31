package com.example.agribid.data.local // <-- FIX: Package name corrected

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.agribid.data.local.db.ProductDao
import com.example.agribid.data.local.db.ProductEntity

@Database(entities = [ProductEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}
