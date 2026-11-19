package com.example.cappucaresuministrosmedicos.model

import com.example.cappucaresuministrosmedicos.model.local.dao.ProductoDao
import com.example.cappucaresuministrosmedicos.model.local.entity.ProductoEntity
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class ProductoRepositoryTest : StringSpec({
    val dao = mockk<ProductoDao>()
    val repository = ProductoRepository(dao)
    val producto = ProductoEntity(
        id = 7, nombre = "Guantes", descripcion = "Nitrilo", categoria = "Insumos",
        stockDisponible = 12, unidadMedida = "cajas", stockMinimo = 3,
        lugarAlmacenamiento = "Bodega A"
    )

    "observarTodos delega el flujo del DAO" {
        every { dao.observarTodos() } returns flowOf(listOf(producto))
        repository.observarTodos().first() shouldBe listOf(producto)
    }

    "observarPorId delega el identificador" {
        every { dao.observarPorId(7L) } returns flowOf(producto)
        repository.observarPorId(7L).first() shouldBe producto
    }

    "observarPorId propaga ausencia" {
        every { dao.observarPorId(404L) } returns flowOf(null)
        repository.observarPorId(404L).first() shouldBe null
    }

    "observarBajoMinimo delega el flujo" {
        every { dao.observarBajoMinimo() } returns flowOf(listOf(producto))
        repository.observarBajoMinimo().first() shouldBe listOf(producto)
    }

    "obtenerPorId delega la consulta suspendida" {
        coEvery { dao.obtenerPorId(7L) } returns producto
        runTest { repository.obtenerPorId(7L) shouldBe producto }
        coVerify(exactly = 1) { dao.obtenerPorId(7L) }
    }

    "insertar actualizar y eliminar delegan al DAO" {
        coEvery { dao.insertar(producto) } returns Unit
        coEvery { dao.actualizar(producto) } returns Unit
        coEvery { dao.eliminar(producto) } returns Unit
        runTest {
            repository.insertar(producto)
            repository.actualizar(producto)
            repository.eliminar(producto)
        }
        coVerify(exactly = 1) { dao.insertar(producto) }
        coVerify(exactly = 1) { dao.actualizar(producto) }
        coVerify(exactly = 1) { dao.eliminar(producto) }
    }
})
