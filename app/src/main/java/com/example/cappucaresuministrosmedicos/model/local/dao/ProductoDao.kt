package com.example.cappucaresuministrosmedicos.model.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.cappucaresuministrosmedicos.model.local.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    @Query("SELECT * FROM productos ORDER BY id")
    fun observarTodos(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos WHERE id = :id")
    fun observarPorId(id: Long): Flow<ProductoEntity?>

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun obtenerPorId(id: Long): ProductoEntity?

    @Query(
        "SELECT * FROM productos " +
                "WHERE activo = 1 AND stockDisponible <= stockMinimo " +
                "ORDER BY stockDisponible"
    )
    fun observarBajoMinimo(): Flow<List<ProductoEntity>>

    @Insert
    suspend fun insertar(producto: ProductoEntity)

    @Update
    suspend fun actualizar(producto: ProductoEntity)

    @Delete
    suspend fun eliminar(producto: ProductoEntity)
}