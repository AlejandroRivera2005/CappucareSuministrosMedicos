package com.example.cappucaresuministrosmedicos.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cappucaresuministrosmedicos.model.local.entity.TipoMovimiento
import com.example.cappucaresuministrosmedicos.view.components.EtiquetaEstado
import com.example.cappucaresuministrosmedicos.viewModel.ProductoDetalleViewModel
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoDetalleScreen(
    onVolver: () -> Unit,
    onEditar: () -> Unit,
    onEliminado: () -> Unit,
    onRegistrarMovimiento: (TipoMovimiento) -> Unit,
    viewModel: ProductoDetalleViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    var confirmarEliminar by remember { mutableStateOf(false) }

    LaunchedEffect(estado.eliminado) {
        if (estado.eliminado) onEliminado()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del producto") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (estado.producto != null) {
                        IconButton(onClick = onEditar) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                        IconButton(onClick = { confirmarEliminar = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        val producto = estado.producto

        when {
            estado.cargando -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            producto == null -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "El producto no existe o fue eliminado.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(producto.nombre, style = MaterialTheme.typography.headlineMedium)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (producto.activo) {
                        EtiquetaEstado(
                            "Activo",
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else {
                        EtiquetaEstado(
                            "Inactivo",
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (producto.stockDisponible <= producto.stockMinimo) {
                        EtiquetaEstado(
                            "Stock bajo",
                            MaterialTheme.colorScheme.errorContainer,
                            MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        FilaDetalle("ID", producto.id.toString())
                        FilaDetalle("Descripción", producto.descripcion)
                        FilaDetalle("Categoría", producto.categoria)
                        FilaDetalle("Lugar de almacenamiento", producto.lugarAlmacenamiento)
                    }
                }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        FilaDetalle(
                            "Stock disponible",
                            "${producto.stockDisponible} ${producto.unidadMedida}"
                        )
                        FilaDetalle(
                            "Stock mínimo",
                            "${producto.stockMinimo} ${producto.unidadMedida}"
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onRegistrarMovimiento(TipoMovimiento.INGRESO) },
                        enabled = producto.activo,
                        modifier = Modifier.weight(1f)
                    ) { Text("Ingreso") }
                    OutlinedButton(
                        onClick = { onRegistrarMovimiento(TipoMovimiento.SALIDA) },
                        enabled = producto.activo,
                        modifier = Modifier.weight(1f)
                    ) { Text("Salida / venta") }
                }
                if (estado.errorGeneral != null) {
                    Text(
                        text = estado.errorGeneral.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    if (confirmarEliminar) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text("Eliminar producto") },
            text = {
                Text(
                    "Se eliminará el producto y también todo su historial de movimientos. " +
                            "Esta acción no se puede deshacer."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmarEliminar = false
                        viewModel.eliminar()
                    },
                    enabled = !estado.eliminando
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun FilaDetalle(etiqueta: String, valor: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            etiqueta,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(valor, style = MaterialTheme.typography.bodyLarge)
    }
}