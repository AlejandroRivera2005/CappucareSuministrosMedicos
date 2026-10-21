package com.example.cappucaresuministrosmedicos.model

import com.example.cappucaresuministrosmedicos.model.local.dao.MovimientoDao
import com.example.cappucaresuministrosmedicos.model.local.entity.MovimientoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovimientoRepository @Inject constructor(
    private val movimientoDao: MovimientoDao
) {
    suspend fun insertar(movimiento: MovimientoEntity) = movimientoDao.insertar(movimiento)

    fun observarPorProducto(productoId: Long): Flow<List<MovimientoEntity>> =
        movimientoDao.observarPorProducto(productoId)

    fun observarRecientes(limite: Int = 20): Flow<List<MovimientoEntity>> =
        movimientoDao.observarRecientes(limite)

    fun contarDesde(desde: Long): Flow<Int> = movimientoDao.contarDesde(desde)
}