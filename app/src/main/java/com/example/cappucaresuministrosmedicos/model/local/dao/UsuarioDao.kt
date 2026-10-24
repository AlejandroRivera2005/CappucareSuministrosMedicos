package com.example.cappucaresuministrosmedicos.model.local.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.cappucaresuministrosmedicos.model.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface UsuarioDao {
    @Insert
    suspend fun insertar(usuario: UsuarioEntity): Long

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun obtenerPorCorreo(correo: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE aprobado = 0 ORDER BY id")
    fun observarPendientes(): Flow<List<UsuarioEntity>>

    @Query("UPDATE usuarios SET aprobado = 1 WHERE id = :id")
    suspend fun aprobar(id: Long)

    @Query("DELETE FROM usuarios WHERE id = :id")
    suspend fun eliminar(id: Long)
}