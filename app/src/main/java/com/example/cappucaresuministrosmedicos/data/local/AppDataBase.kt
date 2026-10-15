package com.example.cappucaresuministrosmedicos.data.local

import ProductoDao
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cappucaresuministrosmedicos.data.local.entity.ProductoEntity


@Database(
    entities = [ProductoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
    abstract val taskDao: ProductoDao
}