package com.example.cappucaresuministrosmedicos.view.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun EtiquetaEstado(
    texto: String,
    fondo: Color,
    contenido: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = fondo,
        contentColor = contenido,
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}