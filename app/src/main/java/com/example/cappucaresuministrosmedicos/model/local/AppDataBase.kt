package com.example.cappucaresuministrosmedicos.model.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cappucaresuministrosmedicos.model.local.dao.MovimientoDao
import com.example.cappucaresuministrosmedicos.model.local.dao.ProductoDao
import com.example.cappucaresuministrosmedicos.model.local.dao.UsuarioDao
import com.example.cappucaresuministrosmedicos.model.local.entity.MovimientoEntity
import com.example.cappucaresuministrosmedicos.model.local.entity.ProductoEntity
import com.example.cappucaresuministrosmedicos.model.local.entity.UsuarioEntity

@Database(
    entities = [ProductoEntity::class, MovimientoEntity::class, UsuarioEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao
    abstract fun movimientoDao(): MovimientoDao
    abstract fun usuarioDao(): UsuarioDao
}