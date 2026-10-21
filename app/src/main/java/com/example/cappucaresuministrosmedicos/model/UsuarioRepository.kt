package com.example.cappucaresuministrosmedicos.model

import com.example.cappucaresuministrosmedicos.model.local.dao.UsuarioDao
import com.example.cappucaresuministrosmedicos.model.local.entity.PerfilUsuario
import com.example.cappucaresuministrosmedicos.model.local.entity.UsuarioEntity
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsuarioRepository @Inject constructor(
    private val usuarioDao: UsuarioDao
) {

    suspend fun registrar(
        nombre: String,
        correo: String,
        contrasena: String,
        perfil: PerfilUsuario
    ): Result<Unit> {
        if (usuarioDao.obtenerPorCorreo(correo) != null) {
            return Result.failure(Exception("Ese correo ya está registrado"))
        }
        usuarioDao.insertar(
            UsuarioEntity(
                nombre = nombre,
                correo = correo,
                perfil = perfil,
                contrasenaHash = hashear(contrasena),
                aprobado = false
            )
        )
        return Result.success(Unit)
    }

    suspend fun iniciarSesion(correo: String, contrasena: String): Result<UsuarioEntity> {
        val usuario = usuarioDao.obtenerPorCorreo(correo)
        if (usuario == null || usuario.contrasenaHash != hashear(contrasena)) {
            return Result.failure(Exception("Correo o contraseña incorrectos"))
        }
        if (!usuario.aprobado) {
            return Result.failure(Exception("Tu cuenta aún no fue aprobada por un encargado"))
        }
        return Result.success(usuario)
    }
    suspend fun sembrarEncargadoDemo() {
        val correo = "encargado@cappucare.cl"
        if (usuarioDao.obtenerPorCorreo(correo) == null) {
            usuarioDao.insertar(
                UsuarioEntity(
                    nombre = "Encargado Demo",
                    correo = correo,
                    perfil = PerfilUsuario.ENCARGADO,
                    contrasenaHash = hashear("Cappucare123"),
                    aprobado = true
                )
            )
        }
    }

    private fun hashear(texto: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(texto.toByteArray())
            .joinToString("") { "%02x".format(it) }
}
