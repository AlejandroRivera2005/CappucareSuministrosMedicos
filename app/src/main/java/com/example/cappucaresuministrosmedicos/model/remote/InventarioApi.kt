package com.example.cappucaresuministrosmedicos.model.remote

import com.example.cappucaresuministrosmedicos.model.local.entity.ProductoEntity
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface InventarioApi {
    @GET("api/productos")
    suspend fun listarProductos(): List<ProductoEntity>

    @GET("api/productos/{id}")
    suspend fun obtenerProducto(@Path("id") id: Long): ProductoEntity

    @POST("api/productos")
    suspend fun crearProducto(@Body producto: ProductoEntity): ProductoEntity

    @PUT("api/productos/{id}")
    suspend fun actualizarProducto(@Path("id") id: Long, @Body producto: ProductoEntity): ProductoEntity

    @DELETE("api/productos/{id}")
    suspend fun eliminarProducto(@Path("id") id: Long)

    @GET("api/movimientos")
    suspend fun listarMovimientos(): List<MovimientoDto>

    @GET("api/movimientos/producto/{productoId}")
    suspend fun movimientosPorProducto(@Path("productoId") productoId: Long): List<MovimientoDto>

    @POST("api/movimientos")
    suspend fun registrarMovimiento(@Body movimiento: MovimientoDto): MovimientoDto
}

data class MovimientoDto(
    val id: Long? = null,
    val productoId: Long,
    val tipo: String,
    val cantidad: Int,
    val motivo: String,
    val responsable: String,
    val fecha: Long = 0L
)
