package com.example.cappucaresuministrosmedicos.model.local.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.cappucaresuministrosmedicos.model.local.entity.UsuarioEntity

@Dao
interface UsuarioDao {
    @Insert
    suspend fun insertar(usuario: UsuarioEntity): Long

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun obtenerPorCorreo(correo: String): UsuarioEntity?
}