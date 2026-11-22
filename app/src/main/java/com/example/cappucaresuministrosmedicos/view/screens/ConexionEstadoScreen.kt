package com.example.cappucaresuministrosmedicos.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ConexionEstadoScreen(
    sinInternet: Boolean,
    comprobando: Boolean = false,
    onReintentar: () -> Unit = {},
    onVolver: (() -> Unit)? = null
) {
    val colores = MaterialTheme.colorScheme

    val titulo = if (sinInternet) {
        "Sin conexión a Internet"
    } else {
        "Servidores en mantenimiento"
    }

    val descripcion = if (sinInternet) {
        "Cappucare necesita una conexión a Internet para funcionar. Comprueba tu conexión e inténtalo nuevamente."
    } else {
        "No es posible comunicarse con el servidor de Cappucare. Inténtalo nuevamente en unos minutos."
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colores.background,
        contentColor = colores.onBackground
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 420.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = colores.primaryContainer,
                    contentColor = colores.onPrimaryContainer,
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Box(
                        modifier = Modifier.padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (sinInternet) {
                                Icons.Default.WifiOff
                            } else {
                                Icons.Default.CloudOff
                            },
                            contentDescription = null,
                            tint = colores.onPrimaryContainer,
                            modifier = Modifier
                                .height(56.dp)
                                .fillMaxWidth(0.3f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = titulo,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = colores.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colores.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (comprobando) {
                    CircularProgressIndicator(
                        color = colores.primary,
                        trackColor = colores.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Comprobando conexión...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colores.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Button(
                        onClick = onReintentar,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Reintentar")
                    }
                }

                if (onVolver != null) {
                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = onVolver,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Volver al inicio")
                    }
                }
            }
        }
    }
}
