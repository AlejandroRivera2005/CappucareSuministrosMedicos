package com.example.cappucaresuministrosmedicos.view.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cappucaresuministrosmedicos.model.local.entity.MotivoMovimiento
import com.example.cappucaresuministrosmedicos.model.local.entity.TipoMovimiento
import com.example.cappucaresuministrosmedicos.viewModel.MovimientoViewModel

@Composable
fun MovimientoScreen(
    onVolver: () -> Unit,
    onGuardado: () -> Unit,
    viewModel: MovimientoViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsState()

    LaunchedEffect(estado.guardado) {
        if (estado.guardado) onGuardado()
    }

    val context = LocalContext.current
    val lanzadorPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            lanzadorPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(onClick = onVolver) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            Text("Volver")
        }
        Text("Registrar movimiento", style = MaterialTheme.typography.headlineMedium)

        val producto = estado.producto
        if (producto == null) {
            CircularProgressIndicator()
            return@Column
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp)) {
                Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
                Text(
                    "Disponible: ${producto.stockDisponible} ${producto.unidadMedida}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Text("Tipo de movimiento", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TipoMovimiento.entries.forEach { tipo ->
                FilterChip(
                    selected = estado.tipo == tipo,
                    onClick = { viewModel.onTipoChange(tipo) },
                    label = { Text(if (tipo == TipoMovimiento.INGRESO) "Ingreso" else "Salida") }
                )
            }
        }

        Text("Cantidad", style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalIconButton(onClick = { viewModel.onCantidadDelta(-1) }) {
                Text("−", style = MaterialTheme.typography.titleLarge)
            }
            Text("${estado.cantidad}", style = MaterialTheme.typography.headlineLarge)
            FilledTonalIconButton(onClick = { viewModel.onCantidadDelta(1) }) {
                Text("+", style = MaterialTheme.typography.titleLarge)
            }
        }
        AnimatedVisibility(visible = estado.errorCantidad != null) {
            Text(
                estado.errorCantidad.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Text("Motivo", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            estado.motivos.forEach { motivo ->
                FilterChip(
                    selected = estado.motivo == motivo,
                    onClick = { viewModel.onMotivoChange(motivo) },
                    label = { Text(etiquetaMotivo(motivo)) }
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Fecha: ${estado.fecha}", style = MaterialTheme.typography.bodyMedium)
                Text("Responsable: ${estado.responsable}", style = MaterialTheme.typography.bodyMedium)
            }
        }

        val resultante = estado.stockResultante
        if (resultante != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (estado.quedaBajoMinimo)
                        MaterialTheme.colorScheme.errorContainer
                    else
                        MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    text = "Stock resultante: ${producto.stockDisponible} → $resultante " +
                            producto.unidadMedida +
                            if (estado.quedaBajoMinimo) " · bajo mínimo" else "",
                    modifier = Modifier.padding(14.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        AnimatedVisibility(visible = estado.errorGeneral != null) {
            Text(
                estado.errorGeneral.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = viewModel::registrar,
            enabled = !estado.cargando,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Guardar movimiento")
        }
    }
}

private fun etiquetaMotivo(motivo: MotivoMovimiento) = when (motivo) {
    MotivoMovimiento.RECEPCION -> "Recepción"
    MotivoMovimiento.VENTA -> "Venta"
    MotivoMovimiento.MERMA -> "Merma"
    MotivoMovimiento.AJUSTE -> "Ajuste"
}