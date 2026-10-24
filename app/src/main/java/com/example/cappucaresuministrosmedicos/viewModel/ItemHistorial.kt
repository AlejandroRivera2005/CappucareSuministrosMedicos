package com.example.cappucaresuministrosmedicos.viewModel

import com.example.cappucaresuministrosmedicos.model.local.entity.MotivoMovimiento
import com.example.cappucaresuministrosmedicos.model.local.entity.MovimientoEntity
import com.example.cappucaresuministrosmedicos.model.local.entity.TipoMovimiento
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ItemHistorial(
    val id: Long,
    val productoNombre: String,
    val esIngreso: Boolean,
    val motivo: String,
    val cantidad: Int,
    val fecha: String,
    val responsable: String
)

fun MovimientoEntity.aItem(productoNombre: String) = ItemHistorial(
    id = id,
    productoNombre = productoNombre,
    esIngreso = tipo == TipoMovimiento.INGRESO,
    motivo = when (motivo) {
        MotivoMovimiento.RECEPCION -> "Recepción"
        MotivoMovimiento.VENTA -> "Venta"
        MotivoMovimiento.MERMA -> "Merma"
        MotivoMovimiento.AJUSTE -> "Ajuste"
    },
    cantidad = cantidad,
    fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CL")).format(Date(fecha)),
    responsable = responsable
)