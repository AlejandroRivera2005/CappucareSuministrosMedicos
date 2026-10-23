package com.example.cappucaresuministrosmedicos.model

import com.example.cappucaresuministrosmedicos.model.local.dao.MovimientoDao
import com.example.cappucaresuministrosmedicos.model.local.dao.ProductoDao
import com.example.cappucaresuministrosmedicos.model.local.entity.MotivoMovimiento
import com.example.cappucaresuministrosmedicos.model.local.entity.MovimientoEntity
import com.example.cappucaresuministrosmedicos.model.local.entity.ProductoEntity
import com.example.cappucaresuministrosmedicos.model.local.entity.TipoMovimiento
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/** Carga datos ficticios de ejemplo la primera vez que se abre la app. */
@Singleton
class SembradorDatos @Inject constructor(
    private val productoDao: ProductoDao,
    private val movimientoDao: MovimientoDao
) {

    suspend fun sembrarProductosDemo() {
        // Si ya hay productos, no se toca nada
        if (productoDao.observarTodos().first().isNotEmpty()) return

        productosDemo.forEach { productoDao.insertar(it) }
        movimientosDemo().forEach { movimientoDao.insertar(it) }
    }

    private val productosDemo = listOf(
        ProductoEntity(
            id = 1, nombre = "Guantes de nitrilo M",
            descripcion = "Código GN-M-001 · Caja de 100 guantes talla M",
            categoria = "Protección", stockDisponible = 120, unidadMedida = "cajas",
            stockMinimo = 40, lugarAlmacenamiento = "Estante A2"
        ),
        ProductoEntity(
            id = 2, nombre = "Mascarillas quirúrgicas",
            descripcion = "Código MQ-014 · Mascarilla desechable de tres capas",
            categoria = "Protección", stockDisponible = 650, unidadMedida = "unid.",
            stockMinimo = 200, lugarAlmacenamiento = "Estante B1"
        ),
        ProductoEntity(
            id = 3, nombre = "Alcohol gel 500 ml",
            descripcion = "Código AG-500 · Alcohol gel antibacterial",
            categoria = "Higiene", stockDisponible = 14, unidadMedida = "unid.",
            stockMinimo = 30, lugarAlmacenamiento = "Estante C3"
        ),
        ProductoEntity(
            id = 4, nombre = "Jeringa 5 ml",
            descripcion = "Código JE-005 · Jeringa desechable estéril con aguja",
            categoria = "Instrumental", stockDisponible = 0, unidadMedida = "unid.",
            stockMinimo = 50, lugarAlmacenamiento = "Estante A4"
        ),
        ProductoEntity(
            id = 5, nombre = "Termómetro digital",
            descripcion = "Código TD-021 · Termómetro clínico de punta flexible",
            categoria = "Instrumental", stockDisponible = 38, unidadMedida = "unid.",
            stockMinimo = 10, lugarAlmacenamiento = "Estante D1"
        ),
        ProductoEntity(
            id = 6, nombre = "Cánula nasal",
            descripcion = "Código CN-008 · Cánula nasal para oxígeno, adulto",
            categoria = "Respiratorio", stockDisponible = 8, unidadMedida = "unid.",
            stockMinimo = 15, lugarAlmacenamiento = "Estante E2"
        ),
        ProductoEntity(
            id = 7, nombre = "Guantes de nitrilo S",
            descripcion = "Código GN-S-002 · Caja de 100 guantes talla S",
            categoria = "Protección", stockDisponible = 22, unidadMedida = "cajas",
            stockMinimo = 40, lugarAlmacenamiento = "Estante A2"
        ),
        ProductoEntity(
            id = 8, nombre = "Gasas estériles 10×10",
            descripcion = "Código GE-101 · Paquete de gasas estériles",
            categoria = "Curación", stockDisponible = 35, unidadMedida = "paquetes",
            stockMinimo = 60, lugarAlmacenamiento = "Estante C1"
        ),
        ProductoEntity(
            id = 9, nombre = "Suero fisiológico 500 ml",
            descripcion = "Código SF-500 · Solución salina al 0,9 %",
            categoria = "Curación", stockDisponible = 80, unidadMedida = "unid.",
            stockMinimo = 30, lugarAlmacenamiento = "Estante C2"
        ),
        ProductoEntity(
            id = 10, nombre = "Mascarilla N95 (descontinuada)",
            descripcion = "Código MN-095 · Modelo ya no se compra",
            categoria = "Protección", stockDisponible = 12, unidadMedida = "unid.",
            stockMinimo = 20, lugarAlmacenamiento = "Bodega 2",
            activo = false
        )
    )

    private fun movimientosDemo(): List<MovimientoEntity> {
        val ahora = System.currentTimeMillis()
        fun hace(dias: Int, horas: Int = 0) =
            ahora - dias * 86_400_000L - horas * 3_600_000L

        val encargado = "Encargado Demo"
        val vendedor = "Vendedor Demo"

        fun mov(
            productoId: Long, tipo: TipoMovimiento, cantidad: Int,
            motivo: MotivoMovimiento, fecha: Long, responsable: String
        ) = MovimientoEntity(
            productoId = productoId, tipo = tipo, cantidad = cantidad,
            motivo = motivo, fecha = fecha, responsable = responsable
        )

        return listOf(
            // Alcohol gel: 0 + 30 - 10 - 6 = 14
            mov(3, TipoMovimiento.INGRESO, 30, MotivoMovimiento.RECEPCION, hace(5), encargado),
            mov(3, TipoMovimiento.SALIDA, 10, MotivoMovimiento.VENTA, hace(1), vendedor),
            mov(3, TipoMovimiento.SALIDA, 6, MotivoMovimiento.VENTA, hace(0, 2), encargado),
            // Jeringa: 100 - 60 - 40 = 0
            mov(4, TipoMovimiento.INGRESO, 100, MotivoMovimiento.RECEPCION, hace(12), encargado),
            mov(4, TipoMovimiento.SALIDA, 60, MotivoMovimiento.VENTA, hace(6), vendedor),
            mov(4, TipoMovimiento.SALIDA, 40, MotivoMovimiento.VENTA, hace(3), vendedor),
            // Guantes M: 150 - 30 = 120
            mov(1, TipoMovimiento.INGRESO, 150, MotivoMovimiento.RECEPCION, hace(10), encargado),
            mov(1, TipoMovimiento.SALIDA, 30, MotivoMovimiento.VENTA, hace(2), vendedor),
            // Mascarillas: 800 - 100 - 50 = 650
            mov(2, TipoMovimiento.INGRESO, 800, MotivoMovimiento.RECEPCION, hace(9), encargado),
            mov(2, TipoMovimiento.SALIDA, 100, MotivoMovimiento.VENTA, hace(4), vendedor),
            mov(2, TipoMovimiento.SALIDA, 50, MotivoMovimiento.MERMA, hace(2, 5), encargado),
            // Cánula nasal: 20 - 12 = 8
            mov(6, TipoMovimiento.INGRESO, 20, MotivoMovimiento.RECEPCION, hace(8), encargado),
            mov(6, TipoMovimiento.SALIDA, 12, MotivoMovimiento.VENTA, hace(1, 3), vendedor)
        )
    }
}