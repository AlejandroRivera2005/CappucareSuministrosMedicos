package com.example.cappucaresuministrosmedicos.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cappucaresuministrosmedicos.viewModel.ItemHistorial

@Composable
fun FilaMovimiento(item: ItemHistorial, mostrarProducto: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (mostrarProducto) item.productoNombre else item.motivo,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = if (mostrarProducto)
                    "${item.motivo} · ${item.fecha} · ${item.responsable}"
                else
                    "${item.fecha} · ${item.responsable}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = if (item.esIngreso) "+${item.cantidad}" else "−${item.cantidad}",
            style = MaterialTheme.typography.titleMedium,
            color = if (item.esIngreso) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.error
        )
    }
}