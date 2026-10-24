package com.example.cappucaresuministrosmedicos.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cappucaresuministrosmedicos.model.SesionUsuario
import com.example.cappucaresuministrosmedicos.model.UsuarioRepository
import com.example.cappucaresuministrosmedicos.model.local.entity.PerfilUsuario
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val nombre: String = "",
    val esEncargado: Boolean = false,
    val pendientes: Int = 0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sesion: SesionUsuario,
    usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val nombre = sesion.nombre.orEmpty()
    private val esEncargado = sesion.perfil == PerfilUsuario.ENCARGADO

    val uiState: StateFlow<HomeUiState> = usuarioRepository.observarPendientes()
        .map { HomeUiState(nombre, esEncargado, if (esEncargado) it.size else 0) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            HomeUiState(nombre, esEncargado, 0)
        )

    fun cerrarSesion() = sesion.cerrar()
}

