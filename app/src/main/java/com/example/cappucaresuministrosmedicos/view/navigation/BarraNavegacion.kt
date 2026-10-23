package com.example.cappucaresuministrosmedicos.view.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector


private data class Destino(val ruta: String, val etiqueta: String, val icono: ImageVector)

@Composable
fun BarraNavegacion(
    rutaActual: String?,
    alertas: Int,
    onNavegar: (String) -> Unit
) {
    val destinos = listOf(
        Destino(Rutas.home, "Inicio", Icons.Default.Home),
        Destino(Rutas.productos, "Inventario", Icons.Default.Inventory2),
        Destino(Rutas.escaner, "Escanear", Icons.Default.QrCodeScanner),
        Destino(Rutas.alertas, "Alertas", Icons.Default.Notifications)
    )

    NavigationBar {
        destinos.forEach { destino ->
            NavigationBarItem(
                selected = rutaActual == destino.ruta,
                onClick = { onNavegar(destino.ruta) },
                icon = {
                    if (destino.ruta == Rutas.alertas && alertas > 0) {
                        // Contador rojo con la cantidad de productos en alerta
                        BadgedBox(badge = { Badge { Text("$alertas") } }) {
                            Icon(destino.icono, contentDescription = null)
                        }
                    } else {
                        Icon(destino.icono, contentDescription = null)
                    }
                },
                label = { Text(destino.etiqueta) }
            )
        }
    }
}