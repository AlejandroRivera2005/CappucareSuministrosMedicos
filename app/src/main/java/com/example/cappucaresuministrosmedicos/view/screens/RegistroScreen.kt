package com.example.cappucaresuministrosmedicos.view.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cappucaresuministrosmedicos.model.local.entity.PerfilUsuario
import com.example.cappucaresuministrosmedicos.view.components.CampoFormulario
import com.example.cappucaresuministrosmedicos.viewModel.RegistroViewModel

@Composable
fun RegistroScreen(
    onRegistroExitoso: () -> Unit,
    onVolver: () -> Unit,
    viewModel: RegistroViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsState()

    // Al registrarse, se avisa que la cuenta queda pendiente de aprobación
    if (estado.registroExitoso) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Cuenta creada") },
            text = {
                Text("Un encargado debe aprobar tu cuenta antes de que puedas iniciar sesión.")
            },
            confirmButton = {
                TextButton(onClick = onRegistroExitoso) { Text("Entendido") }
            }
        )
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
                .padding(horizontal = 20.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Tu cuenta deberá ser aprobada por un encargado antes de ingresar.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))

            CampoFormulario(
                valor = estado.nombre,
                onCambio = viewModel::onNombreChange,
                etiqueta = "Nombre completo",
                icono = Icons.Default.Person,
                error = estado.errorNombre
            )
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
            CampoFormulario(
                valor = estado.confirmacion,
                onCambio = viewModel::onConfirmacionChange,
                etiqueta = "Confirmar contraseña",
                icono = Icons.Default.Lock,
                error = estado.errorConfirmacion,
                teclado = KeyboardType.Password,
                esContrasena = true
            )

            Text("Perfil", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PerfilUsuario.entries.forEach { perfil ->
                    FilterChip(
                        selected = estado.perfil == perfil,
                        onClick = { viewModel.onPerfilChange(perfil) },
                        label = {
                            Text(if (perfil == PerfilUsuario.ENCARGADO) "Encargado" else "Autorizado")
                        }
                    )
                }
            }

            AnimatedVisibility(visible = estado.errorGeneral != null) {
                Text(
                    text = estado.errorGeneral.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = viewModel::registrar,
                enabled = !estado.cargando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (estado.cargando) {
                    CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text("Registrarme")
                }
            }

            TextButton(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
                Text("Ya tengo cuenta, iniciar sesión")
            }
        }
    }
}