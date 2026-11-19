package com.example.cappucaresuministrosmedicos.model

import com.example.cappucaresuministrosmedicos.model.local.entity.PerfilUsuario
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first

class SesionUsuarioTest : StringSpec({
    "inicia sesión guardando nombre y perfil" {
        val sesion = SesionUsuario()
        sesion.iniciar("Carla", PerfilUsuario.ENCARGADO)
        sesion.nombre shouldBe "Carla"
        sesion.perfil shouldBe PerfilUsuario.ENCARGADO
        sesion.perfilActual.first() shouldBe PerfilUsuario.ENCARGADO
    }

    "cerrar limpia nombre y perfil" {
        val sesion = SesionUsuario()
        sesion.iniciar("Carla", PerfilUsuario.AUTORIZADO)
        sesion.cerrar()
        sesion.nombre shouldBe null
        sesion.perfil shouldBe null
        sesion.perfilActual.first() shouldBe null
    }

    "la sesión parte sin usuario" {
        val sesion = SesionUsuario()
        sesion.nombre shouldBe null
        sesion.perfil shouldBe null
    }
})
