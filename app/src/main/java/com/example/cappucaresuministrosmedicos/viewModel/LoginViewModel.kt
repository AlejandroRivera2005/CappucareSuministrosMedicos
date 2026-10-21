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

data class LoginUiState(
    val correo: String = "",
    val contrasena: String = "",

    val errorCorreo: String? = null,
    val errorContrasena: String? = null,

    val cargando: Boolean = false,
    val errorGeneral: String? = null,
    val perfilIngresado: PerfilUsuario? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    init {
        viewModelScope.launch { usuarioRepository.sembrarEncargadoDemo() }
    }

    fun onCorreoChange(valor: String) = _uiState.update {
        it.copy(correo = valor, errorCorreo = Validadores.correo(valor), errorGeneral = null)
    }

    fun onContrasenaChange(valor: String) = _uiState.update {
        it.copy(
            contrasena = valor,
            errorContrasena = if (valor.isBlank()) "La contraseña es obligatoria" else null,
            errorGeneral = null
        )
    }

    fun iniciarSesion() {
        val actual = _uiState.value

        val errCorreo = Validadores.correo(actual.correo)
        val errContrasena = if (actual.contrasena.isBlank()) "La contraseña es obligatoria" else null

        if (errCorreo != null || errContrasena != null) {
            _uiState.update { it.copy(errorCorreo = errCorreo, errorContrasena = errContrasena) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, errorGeneral = null) }
            val resultado = usuarioRepository.iniciarSesion(
                correo = actual.correo.trim().lowercase(),
                contrasena = actual.contrasena
            )
            _uiState.update {
                resultado.fold(
                    onSuccess = { usuario -> it.copy(cargando = false, perfilIngresado = usuario.perfil) },
                    onFailure = { e -> it.copy(cargando = false, errorGeneral = e.message) }
                )
            }
        }
    }
}