package com.example.cappucaresuministrosmedicos.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cappucaresuministrosmedicos.viewModel.HistorialProductoViewModel

@Composable
fun HistorialProducto(viewModel: HistorialProductoViewModel = hiltViewModel()) {
    val movimientos by viewModel.movimientos.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Historial de movimientos", style = MaterialTheme.typography.titleMedium)

        if (movimientos.isEmpty()) {
            Text(
                "Este producto aún no tiene movimientos.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            val recientes = movimientos.take(10)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(horizontal = 14.dp)) {
                    recientes.forEachIndexed { indice, item ->
                        FilaMovimiento(item = item, mostrarProducto = false)
                        if (indice < recientes.lastIndex) HorizontalDivider()
                    }
                }
            }
        }
    }
}