package com.example.cappucaresuministrosmedicos.model

import com.example.cappucaresuministrosmedicos.model.local.entity.MotivoMovimiento
import com.example.cappucaresuministrosmedicos.model.local.entity.ProductoEntity
import com.example.cappucaresuministrosmedicos.model.local.entity.TipoMovimiento
import com.example.cappucaresuministrosmedicos.model.remote.InventarioApi
import com.example.cappucaresuministrosmedicos.model.remote.MovimientoDto
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class MovimientoRepositoryTest : StringSpec({
    val api = mockk<InventarioApi>()
    val productos = mockk<ProductoRepository>(relaxed = true)
    val repo = MovimientoRepository(api, productos)
    val producto = ProductoEntity(1, "Guantes", "", "Protección", 15, "cajas", 3, "A1")
    val mov = MovimientoDto(2L, 1L, "INGRESO", 5, "RECEPCION", "Encargado", 123L)

    "registrar llama POST y recupera stock del servidor" {
        coEvery { api.registrarMovimiento(any()) } returns mov
        coEvery { api.obtenerProducto(1L) } returns producto
        runTest {
            val result = repo.registrar(1, TipoMovimiento.INGRESO, 5, MotivoMovimiento.RECEPCION, "Encargado")
            result.getOrThrow().stockDisponible shouldBe 15
        }
        coVerify(exactly = 1) { api.registrarMovimiento(match { it.productoId == 1L && it.cantidad == 5 }) }
    }

    "cantidad no positiva no llama a la API" {
        runTest { repo.registrar(1, TipoMovimiento.SALIDA, 0, MotivoMovimiento.VENTA, "Demo").isFailure shouldBe true }
    }

    "historial por producto utiliza endpoint dedicado" {
        coEvery { api.movimientosPorProducto(1L) } returns listOf(mov)
        runTest { repo.observarPorProducto(1).first().single().cantidad shouldBe 5 }
    }

    "historial reciente usa GET movimientos" {
        coEvery { api.listarMovimientos() } returns listOf(mov)
        runTest { repo.observarRecientes(1).first().size shouldBe 1 }
    }

    "contar desde usa fechas remotas" {
        coEvery { api.listarMovimientos() } returns listOf(mov)
        runTest { repo.contarDesde(100L).first() shouldBe 1 }
    }
})

