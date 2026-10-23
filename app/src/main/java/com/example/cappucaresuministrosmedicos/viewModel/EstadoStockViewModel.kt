package com.example.cappucaresuministrosmedicos.viewModel

import com.example.cappucaresuministrosmedicos.model.local.entity.ProductoEntity

enum class EstadoStock { DISPONIBLE, BAJO_MINIMO, AGOTADO }

fun ProductoEntity.estadoStock(): EstadoStock = when {
    stockDisponible <= 0 -> EstadoStock.AGOTADO
    stockDisponible <= stockMinimo -> EstadoStock.BAJO_MINIMO
    else -> EstadoStock.DISPONIBLE
}