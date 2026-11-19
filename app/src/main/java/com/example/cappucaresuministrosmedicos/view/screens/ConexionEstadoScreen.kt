package com.example.cappucaresuministrosmedicos.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ConexionEstadoScreen(
    sinInternet: Boolean,
    comprobando: Boolean = false,
    onReintentar: () -> Unit = {},
    onVolver: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (sinInternet) Icons.Default.WifiOff else Icons.Default.CloudOff,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = if (sinInternet) "Sin conexión a Internet" else "Servidores en mantenimiento",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = if (sinInternet) {
                "Cappucare necesita una conexión a Internet para funcionar. Comprueba tu conexión e inténtalo nuevamente."
            } else {
                "No es posible comunicarse con el servidor de Cappucare. Inténtalo nuevamente en unos minutos."
            },
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        if (comprobando) CircularProgressIndicator()
        else Button(onClick = onReintentar) { Text("Reintentar") }
        if (onVolver != null) {
            Spacer(Modifier.height(8.dp))
            Button(onClick = onVolver) { Text("Volver al inicio") }
        }
    }
}