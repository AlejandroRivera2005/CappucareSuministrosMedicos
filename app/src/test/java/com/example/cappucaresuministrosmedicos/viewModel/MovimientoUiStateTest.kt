package com.example.cappucaresuministrosmedicos.viewModel

import com.example.cappucaresuministrosmedicos.model.local.entity.ProductoEntity
import com.example.cappucaresuministrosmedicos.model.local.entity.MotivoMovimiento
import com.example.cappucaresuministrosmedicos.model.local.entity.TipoMovimiento
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class MovimientoUiStateTest : StringSpec({
    val producto = ProductoEntity(1, "Guantes", "", "P", 5, "cajas", 3, "A")

    "ingreso calcula stock resultante y motivos permitidos" {
        val state = MovimientoUiState(producto = producto, tipo = TipoMovimiento.INGRESO, cantidad = 4)
        state.stockResultante shouldBe 9
        state.motivos shouldBe listOf(MotivoMovimiento.RECEPCION, MotivoMovimiento.AJUSTE)
        state.quedaBajoMinimo shouldBe false
    }

    "salida calcula stock y motivos permitidos" {
        val state = MovimientoUiState(producto = producto, tipo = TipoMovimiento.SALIDA, cantidad = 3)
        state.stockResultante shouldBe 2
        state.motivos shouldBe listOf(MotivoMovimiento.VENTA, MotivoMovimiento.MERMA, MotivoMovimiento.AJUSTE)
        state.quedaBajoMinimo shouldBe true
    }

    "sin producto no calcula stock ni alerta" {
        val state = MovimientoUiState(producto = null)
        state.stockResultante shouldBe null
        state.quedaBajoMinimo shouldBe false
    }

    "salida exacta al mínimo activa alerta" {
        val state = MovimientoUiState(producto = producto, tipo = TipoMovimiento.SALIDA, cantidad = 2)
        state.stockResultante shouldBe 3
        state.quedaBajoMinimo shouldBe true
    }
})
