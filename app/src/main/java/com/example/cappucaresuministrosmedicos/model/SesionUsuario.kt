package com.example.cappucaresuministrosmedicos.model

import com.example.cappucaresuministrosmedicos.model.local.entity.PerfilUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SesionUsuario @Inject constructor() {

    private val _perfil = MutableStateFlow<PerfilUsuario?>(null)


    val perfilActual: StateFlow<PerfilUsuario?> = _perfil.asStateFlow()

    val perfil: PerfilUsuario? get() = _perfil.value

    var nombre: String? = null
        private set

    fun iniciar(nombre: String, perfil: PerfilUsuario) {
        this.nombre = nombre
        _perfil.value = perfil
    }

    fun cerrar() {
        nombre = null
        _perfil.value = null
    }
}