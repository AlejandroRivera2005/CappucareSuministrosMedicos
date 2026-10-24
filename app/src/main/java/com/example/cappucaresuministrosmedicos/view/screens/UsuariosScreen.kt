package com.example.cappucaresuministrosmedicos.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cappucaresuministrosmedicos.viewModel.UsuarioPendiente
import com.example.cappucaresuministrosmedicos.viewModel.UsuariosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosScreen(
    onVolver: () -> Unit,
    viewModel: UsuariosViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    var porRechazar by remember { mutableStateOf<UsuarioPendiente?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Usuarios pendientes") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!estado.cargando && estado.pendientes.isEmpty()) {
                Text(
                    "No hay solicitudes pendientes.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(estado.pendientes, key = { it.id }) { usuario ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(usuario.nombre, style = MaterialTheme.typography.titleMedium)
                            Text(
                                usuario.correo,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "Perfil solicitado: ${usuario.perfil}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { porRechazar = usuario },
                                    modifier = Modifier.weight(1f)
                                ) { Text("Rechazar") }
                                Button(
                                    onClick = { viewModel.aprobar(usuario.id) },
                                    modifier = Modifier.weight(1f)
                                ) { Text("Aprobar") }
                            }
                        }
                    }
                }
            }
        }
    }

    porRechazar?.let { usuario ->
        AlertDialog(
            onDismissRequest = { porRechazar = null },
            title = { Text("Rechazar solicitud") },
            text = { Text("Se eliminará la solicitud de ${usuario.nombre}.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.rechazar(usuario.id)
                    porRechazar = null
                }) { Text("Rechazar") }
            },
            dismissButton = {
                TextButton(onClick = { porRechazar = null }) { Text("Cancelar") }
            }
        )
    }
}