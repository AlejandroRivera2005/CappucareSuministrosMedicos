package com.example.cappucaresuministrosmedicos.view.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cappucaresuministrosmedicos.model.local.entity.PerfilUsuario
import com.example.cappucaresuministrosmedicos.view.components.CampoFormulario
import com.example.cappucaresuministrosmedicos.viewModel.LoginViewModel

@Composable
fun LoginScreen(
    onLoginExitoso: (PerfilUsuario) -> Unit,
    onIrARegistro: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsState()


    LaunchedEffect(estado.perfilIngresado) {
        estado.perfilIngresado?.let(onLoginExitoso)
    }

    val entrada = remember { MutableTransitionState(false).apply { targetState = true } }

    AnimatedVisibility(
        visibleState = entrada,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 8 })
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 48.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Cappucare", style = MaterialTheme.typography.headlineLarge)
            Text(
                "Control inteligente y oportuno de existencias",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))

            CampoFormulario(
                valor = estado.correo,
                onCambio = viewModel::onCorreoChange,
                etiqueta = "Correo",
                icono = Icons.Default.Email,
                error = estado.errorCorreo,
                teclado = KeyboardType.Email
            )
            CampoFormulario(
                valor = estado.contrasena,
                onCambio = viewModel::onContrasenaChange,
                etiqueta = "Contraseña",
                icono = Icons.Default.Lock,
                error = estado.errorContrasena,
                teclado = KeyboardType.Password,
                esContrasena = true
            )


            AnimatedVisibility(visible = estado.errorGeneral != null) {
                Text(
                    text = estado.errorGeneral.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = viewModel::iniciarSesion,
                enabled = !estado.cargando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (estado.cargando) {
                    CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text("Iniciar sesión")
                }
            }

            TextButton(onClick = onIrARegistro, modifier = Modifier.fillMaxWidth()) {
                Text("¿No tienes cuenta? Regístrate")
            }
        }
    }
}