package com.example.cappucaresuministrosmedicos.model


import com.example.cappucaresuministrosmedicos.model.local.dao.UsuarioDao
import com.example.cappucaresuministrosmedicos.model.local.entity.PerfilUsuario
import com.example.cappucaresuministrosmedicos.model.local.entity.UsuarioEntity
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import java.security.MessageDigest

class UsuarioRepositoryTest : StringSpec({
    lateinit var dao: UsuarioDao
    lateinit var repository: UsuarioRepository

    beforeTest {
        dao = mockk()
        repository = UsuarioRepository(dao)
    }

    fun hash(value: String) = MessageDigest.getInstance("SHA-256")
        .digest(value.toByteArray()).joinToString("") { "%02x".format(it) }

    "observarPendientes devuelve el flujo del DAO" {
        val usuario = UsuarioEntity(
            id = 1, nombre = "Ana", correo = "ana@test.cl",
            perfil = PerfilUsuario.AUTORIZADO, contrasenaHash = hash("clave123")
        )
        every { dao.observarPendientes() } returns flowOf(listOf(usuario))
        repository.observarPendientes().first() shouldBe listOf(usuario)
    }

    "registrar crea usuario pendiente con contraseña SHA-256" {
        coEvery { dao.obtenerPorCorreo("ana@example.com") } returns null
        coEvery { dao.insertar(any()) } returns 1L
        runTest {
            repository.registrar("Ana", "ana@example.com", "clave123", PerfilUsuario.AUTORIZADO)
                .isSuccess shouldBe true
        }
        coVerify {
            dao.insertar(match {
                it.nombre == "Ana" && it.correo == "ana@example.com" &&
                        it.perfil == PerfilUsuario.AUTORIZADO && !it.aprobado &&
                        it.contrasenaHash == hash("clave123")
            })
        }
    }

    "registrar rechaza correos duplicados sin insertar" {
        coEvery { dao.obtenerPorCorreo("ana@example.com") } returns mockk<UsuarioEntity>()
        runTest {
            val result = repository.registrar("Ana", "ana@example.com", "clave123", PerfilUsuario.AUTORIZADO)
            result.isFailure shouldBe true
            result.exceptionOrNull()?.message shouldBe "Ese correo ya está registrado"
        }
        coVerify(exactly = 0) { dao.insertar(any()) }
    }

    "login exitoso devuelve usuario aprobado" {
        val user = UsuarioEntity(
            id = 2, nombre = "Ana", correo = "ana@example.com",
            perfil = PerfilUsuario.ENCARGADO, contrasenaHash = hash("clave123"), aprobado = true
        )
        coEvery { dao.obtenerPorCorreo(user.correo) } returns user
        runTest {
            repository.iniciarSesion(user.correo, "clave123").getOrNull() shouldBe user
        }
    }

    "login rechaza contraseña incorrecta" {
        val user = UsuarioEntity(
            id = 2, nombre = "Ana", correo = "ana@example.com",
            perfil = PerfilUsuario.ENCARGADO, contrasenaHash = hash("otra"), aprobado = true
        )
        coEvery { dao.obtenerPorCorreo(user.correo) } returns user
        runTest {
            val result = repository.iniciarSesion(user.correo, "clave123")
            result.isFailure shouldBe true
            result.exceptionOrNull()?.message shouldBe "Correo o contraseña incorrectos"
        }
    }

    "login rechaza cuentas pendientes" {
        val user = UsuarioEntity(
            id = 2, nombre = "Ana", correo = "ana@example.com",
            perfil = PerfilUsuario.AUTORIZADO, contrasenaHash = hash("clave123"), aprobado = false
        )
        coEvery { dao.obtenerPorCorreo(user.correo) } returns user
        runTest {
            val result = repository.iniciarSesion(user.correo, "clave123")
            result.isFailure shouldBe true
            result.exceptionOrNull()?.message shouldBe "Tu cuenta aún no fue aprobada por un encargado"
        }
    }

    "sembrar encargado demo inserta solo cuando no existe" {
        coEvery { dao.obtenerPorCorreo("encargado@cappucare.cl") } returns null
        coEvery { dao.insertar(any()) } returns 3L
        runTest { repository.sembrarEncargadoDemo() }
        coVerify {
            dao.insertar(match {
                it.nombre == "Encargado Demo" &&
                        it.correo == "encargado@cappucare.cl" &&
                        it.perfil == PerfilUsuario.ENCARGADO && it.aprobado &&
                        it.contrasenaHash == hash("Cappucare123")
            })
        }
    }

    "sembrar encargado demo no duplica un registro existente" {
        coEvery { dao.obtenerPorCorreo("encargado@cappucare.cl") } returns mockk<UsuarioEntity>()
        runTest { repository.sembrarEncargadoDemo() }
        coVerify(exactly = 0) { dao.insertar(any()) }
    }

    "aprobar y rechazar delegan las operaciones" {
        coEvery { dao.aprobar(4L) } returns Unit
        coEvery { dao.eliminar(5L) } returns Unit
        runTest {
            repository.aprobar(4L)
            repository.rechazar(5L)
        }
        coVerify { dao.aprobar(4L) }
        coVerify { dao.eliminar(5L) }
    }
})