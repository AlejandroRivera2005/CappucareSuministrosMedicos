package com.example.cappucaresuministrosmedicos.model.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey val id: Long,
    val nombre: String,
    val descripcion: String,
    val categoria: String,
    val stockDisponible: Int,
    val unidadMedida: String,
    val stockMinimo: Int,
    val lugarAlmacenamiento: String,
    val activo: Boolean = true
)