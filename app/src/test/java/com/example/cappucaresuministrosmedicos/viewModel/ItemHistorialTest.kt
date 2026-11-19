package com.example.cappucaresuministrosmedicos.viewModel

import com.example.cappucaresuministrosmedicos.model.local.entity.MotivoMovimiento
import com.example.cappucaresuministrosmedicos.model.local.entity.MovimientoEntity
import com.example.cappucaresuministrosmedicos.model.local.entity.TipoMovimiento
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ItemHistorialTest : StringSpec({
    "convierte ingreso y motivo recepción" {
        val item = MovimientoEntity(
            id = 8, productoId = 3, tipo = TipoMovimiento.INGRESO,
            cantidad = 12, motivo = MotivoMovimiento.RECEPCION,
            fecha = 0L, responsable = "Encargado"
        ).aItem("Guantes")
        item.id shouldBe 8L
        item.productoNombre shouldBe "Guantes"
        item.esIngreso shouldBe true
        item.motivo shouldBe "Recepción"
        item.cantidad shouldBe 12
        item.fecha.matches(Regex("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}")) shouldBe true
        item.responsable shouldBe "Encargado"
    }

    "convierte todos los motivos de salida" {
        val motivos = mapOf(
            MotivoMovimiento.VENTA to "Venta",
            MotivoMovimiento.MERMA to "Merma",
            MotivoMovimiento.AJUSTE to "Ajuste"
        )
        motivos.forEach { (motivo, esperado) ->
            MovimientoEntity(
                productoId = 1, tipo = TipoMovimiento.SALIDA, cantidad = 1,
                motivo = motivo, fecha = 1_700_000_000_000L, responsable = "Usuario"
            ).aItem("Producto").motivo shouldBe esperado
        }
    }

    "movimiento de salida se marca como no ingreso" {
        val item = MovimientoEntity(
            productoId = 1, tipo = TipoMovimiento.SALIDA, cantidad = 2,
            motivo = MotivoMovimiento.VENTA, fecha = 1L, responsable = "U"
        ).aItem("P")
        item.esIngreso shouldBe false
    }
})
