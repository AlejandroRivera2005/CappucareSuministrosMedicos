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
    fun observarPorId(id: String): Flow<ProductoEntity?>

    @Insert
    suspend fun insertar(producto: ProductoEntity)


    @Update
    suspend fun actualizar(producto: ProductoEntity)

    @Delete
    suspend fun eliminar(task: ProductoEntity)
}