package com.example.cappucaresuministrosmedicos.view.screens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cappucaresuministrosmedicos.model.local.entity.ProductoEntity
import com.example.cappucaresuministrosmedicos.view.components.EtiquetaEstado
import com.example.cappucaresuministrosmedicos.viewModel.AlertasViewModel
import com.example.cappucaresuministrosmedicos.viewModel.EstadoStock
import com.example.cappucaresuministrosmedicos.viewModel.estadoStock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertasScreen(
    onVerDetalle: (Long) -> Unit,
    viewModel: AlertasViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val cantidad = estado.productos.size

    Scaffold(
        topBar = { TopAppBar(title = { Text("Alertas") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = when (cantidad) {
                    0 -> "Todo en orden: ningún producto bajo el mínimo"
                    1 -> "1 producto bajo el nivel mínimo"
                    else -> "$cantidad productos bajo el nivel mínimo"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(estado.productos, key = { it.id }) { producto ->
                    TarjetaAlerta(producto = producto, onClick = { onVerDetalle(producto.id) })
                }
            }

            Button(
                onClick = {
                    val intento = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "Lista de reposición")
                        putExtra(Intent.EXTRA_TEXT, viewModel.textoReposicion())
                    }
                    context.startActivity(
                        Intent.createChooser(intento, "Compartir lista de reposición")
                    )
                },
                enabled = cantidad > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Generar lista de reposición")
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun TarjetaAlerta(producto: ProductoEntity, onClick: () -> Unit) {
    val agotado = producto.estadoStock() == EstadoStock.AGOTADO
    val avance = if (producto.stockMinimo > 0)
        (producto.stockDisponible.toFloat() / producto.stockMinimo).coerceIn(0f, 1f)
    else 0f

    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
                if (agotado) {
                    EtiquetaEstado(
                        "Agotado",
                        MaterialTheme.colorScheme.errorContainer,
                        MaterialTheme.colorScheme.onErrorContainer
                    )
                } else {
                    EtiquetaEstado(
                        "Bajo mínimo",
                        MaterialTheme.colorScheme.tertiaryContainer,
                        MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
            LinearProgressIndicator(
                progress = { avance },
                modifier = Modifier.fillMaxWidth(),
                color = if (agotado) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.tertiary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Disponible: ${producto.stockDisponible}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Mínimo: ${producto.stockMinimo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}