package com.example.cappucaresuministrosmedicos.model

import androidx.room.withTransaction
import com.example.cappucaresuministrosmedicos.model.local.AppDatabase
import com.example.cappucaresuministrosmedicos.model.local.dao.MovimientoDao
import com.example.cappucaresuministrosmedicos.model.local.dao.ProductoDao
import com.example.cappucaresuministrosmedicos.model.local.entity.MotivoMovimiento
import com.example.cappucaresuministrosmedicos.model.local.entity.MovimientoEntity
import com.example.cappucaresuministrosmedicos.model.local.entity.ProductoEntity
import com.example.cappucaresuministrosmedicos.model.local.entity.TipoMovimiento
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovimientoRepository @Inject constructor(
    private val database: AppDatabase,
    private val movimientoDao: MovimientoDao,
    private val productoDao: ProductoDao
) {


    suspend fun registrar(
        productoId: Long,
        tipo: TipoMovimiento,
        cantidad: Int,
        motivo: MotivoMovimiento,
        responsable: String
    ): Result<ProductoEntity> = try {
        val actualizado = database.withTransaction {
            val producto = productoDao.obtenerPorId(productoId)
                ?: throw IllegalStateException("El producto no existe")

            val nuevoStock = when (tipo) {
                TipoMovimiento.INGRESO -> producto.stockDisponible + cantidad
                TipoMovimiento.SALIDA -> producto.stockDisponible - cantidad
            }
            if (nuevoStock < 0) {
                throw IllegalStateException(
                    "No hay stock suficiente (disponible: ${producto.stockDisponible})"
                )
            }

            val productoNuevo = producto.copy(stockDisponible = nuevoStock)
            productoDao.actualizar(productoNuevo)
            movimientoDao.insertar(
                MovimientoEntity(
                    productoId = productoId,
                    tipo = tipo,
                    cantidad = cantidad,
                    motivo = motivo,
                    fecha = System.currentTimeMillis(),
                    responsable = responsable
                )
            )
            productoNuevo
        }
        Result.success(actualizado)
    } catch (e: IllegalStateException) {
        Result.failure(e)
    }

    fun observarPorProducto(productoId: Long): Flow<List<MovimientoEntity>> =
        movimientoDao.observarPorProducto(productoId)

    fun observarRecientes(limite: Int = 20): Flow<List<MovimientoEntity>> =
        movimientoDao.observarRecientes(limite)

    fun contarDesde(desde: Long): Flow<Int> = movimientoDao.contarDesde(desde)
}