package com.example.cappucaresuministrosmedicos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val descripcion: String,
    val categoria: String,
    val stock_disponible: Int,
    val unidad_medida: String,
    val stock_minimo: Int,
    val lugar_almacenamiento: String
)