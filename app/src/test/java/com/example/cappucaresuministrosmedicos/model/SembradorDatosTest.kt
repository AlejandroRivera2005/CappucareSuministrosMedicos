package com.example.cappucaresuministrosmedicos.model


import com.example.cappucaresuministrosmedicos.model.local.dao.MovimientoDao
import com.example.cappucaresuministrosmedicos.model.local.dao.ProductoDao
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class SembradorDatosTest : StringSpec({
    "si no hay productos inserta catálogo demo y movimientos demo" {
        val productos = mockk<ProductoDao>()
        val movimientos = mockk<MovimientoDao>()
        every { productos.observarTodos() } returns flowOf(emptyList())
        coEvery { productos.insertar(any()) } returns Unit
        coEvery { movimientos.insertar(any()) } returns Unit

        runTest { SembradorDatos(productos, movimientos).sembrarProductosDemo() }

        coVerify(exactly = 10) { productos.insertar(any()) }
        coVerify(exactly = 13) { movimientos.insertar(any()) }
    }

    "si ya existen productos no vuelve a sembrar" {
        val productos = mockk<ProductoDao>()
        val movimientos = mockk<MovimientoDao>()
        every { productos.observarTodos() } returns flowOf(
            listOf(
                com.example.cappucaresuministrosmedicos.model.local.entity.ProductoEntity(
                    1, "Demo", "", "Demo", 1, "u", 0, "A"
                )
            )
        )

        runTest { SembradorDatos(productos, movimientos).sembrarProductosDemo() }

        coVerify(exactly = 0) { productos.insertar(any()) }
        coVerify(exactly = 0) { movimientos.insertar(any()) }
    }
})
