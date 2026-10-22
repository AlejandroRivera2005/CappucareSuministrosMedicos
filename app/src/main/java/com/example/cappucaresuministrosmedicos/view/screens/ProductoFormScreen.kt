package com.example.cappucaresuministrosmedicos.view.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cappucaresuministrosmedicos.view.components.CampoFormulario
import com.example.cappucaresuministrosmedicos.viewModel.ProductoFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoFormScreen(
    onVolver: () -> Unit,
    onGuardado: () -> Unit,
    viewModel: ProductoFormViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsState()

    LaunchedEffect(estado.guardado) {
        if (estado.guardado) onGuardado()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (estado.esEdicion) "Editar producto" else "Nuevo producto") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (estado.cargandoInicial) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CampoFormulario(
                valor = estado.id,
                onCambio = viewModel::onIdChange,
                etiqueta = "ID del producto",
                icono = Icons.Default.Info,
                error = estado.errorId,
                teclado = KeyboardType.Number,
                habilitado = !estado.esEdicion
            )
            CampoFormulario(
                valor = estado.nombre,
                onCambio = viewModel::onNombreChange,
                etiqueta = "Nombre",
                icono = Icons.Default.Edit,
                error = estado.errorNombre
            )
            CampoFormulario(
                valor = estado.descripcion,
                onCambio = viewModel::onDescripcionChange,
                etiqueta = "Descripción",
                icono = Icons.AutoMirrored.Filled.List,
                error = estado.errorDescripcion
            )
            CampoFormulario(
                valor = estado.categoria,
                onCambio = viewModel::onCategoriaChange,
                etiqueta = "Categoría",
                icono = Icons.Default.Menu,
                error = estado.errorCategoria
            )
            CampoFormulario(
                valor = estado.stockDisponible,
                onCambio = viewModel::onStockChange,
                etiqueta = "Stock disponible",
                icono = Icons.Default.ShoppingCart,
                error = estado.errorStock,
                teclado = KeyboardType.Number
            )
            CampoFormulario(
                valor = estado.unidadMedida,
                onCambio = viewModel::onUnidadChange,
                etiqueta = "Unidad de medida (ej: cajas, unidades)",
                icono = Icons.Default.Settings,
                error = estado.errorUnidad
            )
            CampoFormulario(
                valor = estado.stockMinimo,
                onCambio = viewModel::onStockMinimoChange,
                etiqueta = "Stock mínimo",
                icono = Icons.Default.Warning,
                error = estado.errorStockMinimo,
                teclado = KeyboardType.Number
            )
            CampoFormulario(
                valor = estado.lugarAlmacenamiento,
                onCambio = viewModel::onLugarChange,
                etiqueta = "Lugar de almacenamiento",
                icono = Icons.Default.LocationOn,
                error = estado.errorLugar
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Producto activo", style = MaterialTheme.typography.labelLarge)
                    Text(
                        "Los productos inactivos se muestran como tales en la lista.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(checked = estado.activo, onCheckedChange = viewModel::onActivoChange)
            }

            AnimatedVisibility(visible = estado.errorGeneral != null) {
                Text(
                    text = estado.errorGeneral.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = viewModel::guardar,
                enabled = !estado.guardando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (estado.guardando) {
                    CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text(if (estado.esEdicion) "Guardar cambios" else "Crear producto")
                }
            }
        }
    }
}