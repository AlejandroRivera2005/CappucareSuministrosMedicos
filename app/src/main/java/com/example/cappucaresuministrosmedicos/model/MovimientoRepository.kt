package com.example.cappucaresuministrosmedicos.model

import com.example.cappucaresuministrosmedicos.model.local.entity.MotivoMovimiento
import com.example.cappucaresuministrosmedicos.model.local.entity.MovimientoEntity
import com.example.cappucaresuministrosmedicos.model.local.entity.ProductoEntity
import com.example.cappucaresuministrosmedicos.model.local.entity.TipoMovimiento
import com.example.cappucaresuministrosmedicos.model.remote.InventarioApi
import com.example.cappucaresuministrosmedicos.model.remote.MovimientoDto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovimientoRepository @Inject constructor(
    private val api: InventarioApi,
    private val productos: ProductoRepository
) {
    private val revision = MutableStateFlow(0L)

    suspend fun registrar(
        productoId: Long,
        tipo: TipoMovimiento,
        cantidad: Int,
        motivo: MotivoMovimiento,
        responsable: String
    ): Result<ProductoEntity> = try {
        require(cantidad > 0) { "La cantidad debe ser mayor que cero" }
        api.registrarMovimiento(MovimientoDto(
            productoId = productoId,
            tipo = tipo.name,
            cantidad = cantidad,
            motivo = motivo.name,
            responsable = responsable
        ))
        val actualizado = api.obtenerProducto(productoId)
        revision.value += 1
        productos.refrescar()
        Result.success(actualizado)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun observarPorProducto(productoId: Long): Flow<List<MovimientoEntity>> =
        revision.mapLatest { api.movimientosPorProducto(productoId).map { it.aEntidad() } }
            .catch { e ->
                if (e is CancellationException) throw e
                emit(emptyList())
            }

    fun observarRecientes(limite: Int = 20): Flow<List<MovimientoEntity>> =
        revision.mapLatest { api.listarMovimientos().map { it.aEntidad() }.take(limite) }
            .catch { e ->
                if (e is CancellationException) throw e
                emit(emptyList())
            }

    fun contarDesde(desde: Long): Flow<Int> =
        revision.mapLatest { api.listarMovimientos().count { it.fecha >= desde } }
            .catch { e ->
                if (e is CancellationException) throw e
                emit(0)
            }

    private fun MovimientoDto.aEntidad() = MovimientoEntity(
        id = id ?: 0L,
        productoId = productoId,
        tipo = TipoMovimiento.valueOf(tipo.uppercase()),
        cantidad = cantidad,
        motivo = MotivoMovimiento.entries.firstOrNull { it.name == motivo.uppercase() }
            ?: MotivoMovimiento.AJUSTE,
        responsable = responsable,
        fecha = fecha
    )
}
