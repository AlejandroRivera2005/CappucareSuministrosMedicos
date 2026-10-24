package com.example.cappucaresuministrosmedicos.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cappucaresuministrosmedicos.model.UsuarioRepository
import com.example.cappucaresuministrosmedicos.model.local.entity.PerfilUsuario
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UsuarioPendiente(
    val id: Long,
    val nombre: String,
    val correo: String,
    val perfil: String
)

data class UsuariosUiState(
    val pendientes: List<UsuarioPendiente> = emptyList(),
    val cargando: Boolean = true
)

@HiltViewModel
class UsuariosViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    val uiState: StateFlow<UsuariosUiState> = usuarioRepository.observarPendientes()
        .map { lista ->
            UsuariosUiState(
                pendientes = lista.map {
                    UsuarioPendiente(
                        id = it.id,
                        nombre = it.nombre,
                        correo = it.correo,
                        perfil = if (it.perfil == PerfilUsuario.ENCARGADO) "Encargado" else "Autorizado"
                    )
                },
                cargando = false
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UsuariosUiState())

    fun aprobar(id: Long) {
        viewModelScope.launch { usuarioRepository.aprobar(id) }
    }

    fun rechazar(id: Long) {
        viewModelScope.launch { usuarioRepository.rechazar(id) }
    }
}