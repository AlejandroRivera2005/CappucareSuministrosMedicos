package com.example.cappucaresuministrosmedicos.model

import com.example.cappucaresuministrosmedicos.model.local.entity.ProductoEntity
import com.example.cappucaresuministrosmedicos.model.remote.InventarioApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductoRepository @Inject constructor(private val api: InventarioApi) {
    private val revision = MutableStateFlow(0L)

    fun observarTodos(): Flow<List<ProductoEntity>> = revision.mapLatest {
        api.listarProductos().sortedBy { it.id }
    }.catch { e ->
        if (e is CancellationException) throw e
        emit(emptyList())
    }

    fun observarPorId(id: Long): Flow<ProductoEntity?> = revision.mapLatest {
        try {
            api.obtenerProducto(id)
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 404) null else throw e
        }
    }.catch { e ->
        if (e is CancellationException) throw e
        emit(null)
    }

    fun observarBajoMinimo(): Flow<List<ProductoEntity>> = observarTodos().mapLatest { lista ->
        lista.filter { it.activo && it.stockDisponible <= it.stockMinimo }
            .sortedBy { it.stockDisponible }
    }

    suspend fun obtenerPorId(id: Long): ProductoEntity? = try {
        api.obtenerProducto(id)
    } catch (e: retrofit2.HttpException) {
        if (e.code() == 404) null else throw e
    }

    suspend fun insertar(producto: ProductoEntity) {
        api.crearProducto(producto)
        refrescar()
    }

    suspend fun actualizar(producto: ProductoEntity) {
        api.actualizarProducto(producto.id, producto)
        refrescar()
    }

    suspend fun eliminar(producto: ProductoEntity) {
        api.eliminarProducto(producto.id)
        refrescar()
    }

    fun refrescar() { revision.value += 1 }
}
