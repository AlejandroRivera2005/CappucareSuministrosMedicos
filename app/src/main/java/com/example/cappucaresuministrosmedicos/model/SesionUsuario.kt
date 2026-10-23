package com.example.cappucaresuministrosmedicos.model

import com.example.cappucaresuministrosmedicos.model.local.entity.PerfilUsuario
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SesionUsuario @Inject constructor() {
    var nombre: String? = null
        private set
    var perfil: PerfilUsuario? = null
        private set

    fun iniciar(nombre: String, perfil: PerfilUsuario) {
        this.nombre = nombre
        this.perfil = perfil
    }

    fun cerrar() {
        nombre = null
        perfil = null
    }
}