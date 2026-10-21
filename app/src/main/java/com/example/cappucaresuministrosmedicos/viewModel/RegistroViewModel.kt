package com.example.cappucaresuministrosmedicos.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cappucaresuministrosmedicos.model.UsuarioRepository
import com.example.cappucaresuministrosmedicos.model.local.entity.PerfilUsuario
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegistroUiState(
    val nombre: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val confirmacion: String = "",
    val perfil: PerfilUsuario = PerfilUsuario.AUTORIZADO,

    val errorNombre: String? = null,
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,
    val errorConfirmacion: String? = null,

    val cargando: Boolean = false,
    val errorGeneral: String? = null,
    val registroExitoso: Boolean = false
)

@HiltViewModel
class RegistroViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    fun onNombreChange(valor: String) = _uiState.update {
        it.copy(nombre = valor, errorNombre = Validadores.nombre(valor), errorGeneral = null)
    }

    fun onCorreoChange(valor: String) = _uiState.update {
        it.copy(correo = valor, errorCorreo = Validadores.correo(valor), errorGeneral = null)
    }

    fun onContrasenaChange(valor: String) = _uiState.update {
        it.copy(
            contrasena = valor,
            errorContrasena = Validadores.contrasena(valor),

            errorConfirmacion = if (it.confirmacion.isEmpty()) it.errorConfirmacion
            else Validadores.confirmarContrasena(valor, it.confirmacion),
            errorGeneral = null
        )
    }

    fun onConfirmacionChange(valor: String) = _uiState.update {
        it.copy(
            confirmacion = valor,
            errorConfirmacion = Validadores.confirmarContrasena(it.contrasena, valor),
            errorGeneral = null
        )
    }

    fun onPerfilChange(perfil: PerfilUsuario) = _uiState.update { it.copy(perfil = perfil) }

    fun registrar() {
        val actual = _uiState.value


        val errNombre = Validadores.nombre(actual.nombre)
        val errCorreo = Validadores.correo(actual.correo)
        val errContrasena = Validadores.contrasena(actual.contrasena)
        val errConfirmacion = Validadores.confirmarContrasena(actual.contrasena, actual.confirmacion)

        if (listOf(errNombre, errCorreo, errContrasena, errConfirmacion).any { it != null }) {
            _uiState.update {
                it.copy(
                    errorNombre = errNombre,
                    errorCorreo = errCorreo,
                    errorContrasena = errContrasena,
                    errorConfirmacion = errConfirmacion
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, errorGeneral = null) }
            val resultado = usuarioRepository.registrar(
                nombre = actual.nombre.trim(),
                correo = actual.correo.trim().lowercase(),
                contrasena = actual.contrasena,
                perfil = actual.perfil
            )
            _uiState.update {
                resultado.fold(
                    onSuccess = { _ -> it.copy(cargando = false, registroExitoso = true) },
                    onFailure = { e -> it.copy(cargando = false, errorGeneral = e.message) }
                )
            }
        }
    }
}