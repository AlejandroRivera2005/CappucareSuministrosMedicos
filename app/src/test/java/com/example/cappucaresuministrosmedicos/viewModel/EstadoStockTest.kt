package com.example.cappucaresuministrosmedicos.viewModel

import com.example.cappucaresuministrosmedicos.model.local.entity.ProductoEntity
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class EstadoStockTest : StringSpec({
    fun producto(stock: Int, minimo: Int = 5) = ProductoEntity(
        id = 1, nombre = "Producto", descripcion = "", categoria = "General",
        stockDisponible = stock, unidadMedida = "unid.", stockMinimo = minimo,
        lugarAlmacenamiento = "Bodega"
    )

    "stock cero o negativo está agotado" {
        producto(0).estadoStock() shouldBe EstadoStock.AGOTADO
        producto(-1).estadoStock() shouldBe EstadoStock.AGOTADO
    }

    "stock igual o inferior al mínimo está bajo mínimo" {
        producto(5).estadoStock() shouldBe EstadoStock.BAJO_MINIMO
        producto(2).estadoStock() shouldBe EstadoStock.BAJO_MINIMO
    }

    "stock superior al mínimo está disponible" {
        producto(6).estadoStock() shouldBe EstadoStock.DISPONIBLE
    }
})
