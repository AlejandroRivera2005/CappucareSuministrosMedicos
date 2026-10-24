package com.example.cappucaresuministrosmedicos.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cappucaresuministrosmedicos.model.local.entity.ProductoEntity
import com.example.cappucaresuministrosmedicos.view.components.EtiquetaEstado
import com.example.cappucaresuministrosmedicos.viewModel.EstadoStock
import com.example.cappucaresuministrosmedicos.viewModel.ProductoListaViewModel
import com.example.cappucaresuministrosmedicos.viewModel.estadoStock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoListaScreen(
    onVolver: () -> Unit,
    onNuevo: () -> Unit,
    onVerDetalle: (Long) -> Unit,
    puedeCrear: Boolean,
    viewModel: ProductoListaViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsState()
    val busqueda by viewModel.busqueda.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Productos") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            // Solo el encargado puede crear productos
            if (puedeCrear) {
                FloatingActionButton(onClick = onNuevo) {
                    Icon(Icons.Default.Add, contentDescription = "Nuevo producto")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = busqueda,
                onValueChange = viewModel::onBusquedaChange,
                label = { Text("Buscar por nombre, categoría o ID") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            when {
                estado.cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                estado.productos.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = when {
                            estado.totalProductos > 0 -> "Ningún producto coincide con la búsqueda."
                            puedeCrear -> "Aún no hay productos. Toca + para crear el primero."
                            else -> "Aún no hay productos."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                else -> LazyColumn(
                    contentPadding = PaddingValues(bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(estado.productos, key = { it.id }) { producto ->
                        ProductoCard(producto = producto, onClick = { onVerDetalle(producto.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductoCard(producto: ProductoEntity, onClick: () -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = onClick)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
                Text(
                    "ID ${producto.id} · ${producto.categoria}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Stock: ${producto.stockDisponible} ${producto.unidadMedida}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                when (producto.estadoStock()) {
                    EstadoStock.AGOTADO -> EtiquetaEstado(
                        texto = "Agotado",
                        fondo = MaterialTheme.colorScheme.errorContainer,
                        contenido = MaterialTheme.colorScheme.onErrorContainer
                    )
                    EstadoStock.BAJO_MINIMO -> EtiquetaEstado(
                        texto = "Bajo mínimo",
                        fondo = MaterialTheme.colorScheme.tertiaryContainer,
                        contenido = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    EstadoStock.DISPONIBLE -> Unit
                }
                if (!producto.activo) {
                    EtiquetaEstado(
                        texto = "Inactivo",
                        fondo = MaterialTheme.colorScheme.surfaceVariant,
                        contenido = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}