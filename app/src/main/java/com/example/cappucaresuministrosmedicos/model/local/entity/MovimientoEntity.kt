package com.example.cappucaresuministrosmedicos.model.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TipoMovimiento { INGRESO, SALIDA }
enum class MotivoMovimiento { RECEPCION, VENTA, MERMA, AJUSTE }

@Entity(
    tableName = "movimientos",
    foreignKeys = [ForeignKey(
        entity = ProductoEntity::class,
        parentColumns = ["id"],
        childColumns = ["productoId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("productoId")]
)
data class MovimientoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productoId: Long,
    val tipo: TipoMovimiento,
    val cantidad: Int,
    val motivo: MotivoMovimiento,
    val fecha: Long,
    val responsable: String
)