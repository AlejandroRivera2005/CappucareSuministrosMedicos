package com.example.cappucaresuministrosmedicos.model

import com.example.cappucaresuministrosmedicos.model.local.dao.ProductoDao
import com.example.cappucaresuministrosmedicos.model.local.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductoRepository @Inject constructor(
    private val productoDao: ProductoDao
) {
    fun observarTodos(): Flow<List<ProductoEntity>> = productoDao.observarTodos()

    fun observarPorId(id: Long): Flow<ProductoEntity?> = productoDao.observarPorId(id)

    suspend fun insertar(producto: ProductoEntity) = productoDao.insertar(producto)

    suspend fun actualizar(producto: ProductoEntity) = productoDao.actualizar(producto)

    suspend fun eliminar(producto: ProductoEntity) = productoDao.eliminar(producto)
}