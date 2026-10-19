package com.example.cappucaresuministrosmedicos.model.local.entity
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class PerfilUsuario { ENCARGADO, AUTORIZADO }

@Entity(tableName = "usuarios", indices = [Index(value = ["correo"], unique = true)])
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val correo: String,
    val perfil: PerfilUsuario,
    val contrasenaHash: String,
    val aprobado: Boolean = false
)