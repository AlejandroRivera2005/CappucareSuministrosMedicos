package com.example.cappucaresuministrosmedicos.model

import com.example.cappucaresuministrosmedicos.model.local.entity.ProductoEntity
import com.example.cappucaresuministrosmedicos.model.remote.InventarioApi
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class ProductoRepositoryTest : StringSpec({
    val api = mockk<InventarioApi>()
    val repo = ProductoRepository(api)
    val producto = ProductoEntity(7, "Guantes", "Nitrilo", "Insumos", 2, "cajas", 3, "Bodega")

    "listar consulta Spring Boot" {
        coEvery { api.listarProductos() } returns listOf(producto)
        runTest { repo.observarTodos().first() shouldBe listOf(producto) }
    }

    "obtener por id consulta Spring Boot" {
        coEvery { api.obtenerProducto(7L) } returns producto
        runTest { repo.obtenerPorId(7L) shouldBe producto }
    }

    "alertas filtra bajo minimo" {
        coEvery { api.listarProductos() } returns listOf(producto)
        runTest { repo.observarBajoMinimo().first() shouldBe listOf(producto) }
    }

    "crear usa POST" {
        coEvery { api.crearProducto(producto) } returns producto
        runTest { repo.insertar(producto) }
        coVerify(exactly = 1) { api.crearProducto(producto) }
    }

    "editar usa PUT" {
        coEvery { api.actualizarProducto(7L, producto) } returns producto
        runTest { repo.actualizar(producto) }
        coVerify(exactly = 1) { api.actualizarProducto(7L, producto) }
    }

    "eliminar usa DELETE" {
        coEvery { api.eliminarProducto(7L) } returns Unit
        runTest { repo.eliminar(producto) }
        coVerify(exactly = 1) { api.eliminarProducto(7L) }
    }
})
