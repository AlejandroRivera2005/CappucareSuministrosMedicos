package com.example.cappucaresuministrosmedicos.viewModel

import androidx.lifecycle.ViewModel
import com.example.cappucaresuministrosmedicos.model.SesionUsuario
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SesionViewModel @Inject constructor(
    private val sesion: SesionUsuario
) : ViewModel() {
    val haySesion: Boolean get() = sesion.nombre != null
    fun cerrar() = sesion.cerrar()
}