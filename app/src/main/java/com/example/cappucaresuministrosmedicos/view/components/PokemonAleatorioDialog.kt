package com.example.cappucaresuministrosmedicos.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cappucaresuministrosmedicos.viewModel.PokemonAleatorioUiState

@Composable
fun PokemonAleatorioDialog(
    estado: PokemonAleatorioUiState,
    onOtro: () -> Unit,
    onCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Pokémon aleatorio") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val pokemon = estado.pokemon
                when {
                    estado.cargando -> CircularProgressIndicator(modifier = Modifier.padding(24.dp))

                    estado.error != null -> Text(
                        text = estado.error,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )

                    pokemon != null -> {
                        PokemonImage(
                            url = pokemon.imagenUrl,
                            name = pokemon.nombre,
                            size = 180.dp
                        )
                        Text(
                            text = "#${pokemon.id} ${pokemon.nombre}",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            pokemon.tipos.forEach { tipo ->
                                PokemonTypePill(clave = tipo.clave, etiqueta = tipo.etiqueta)
                            }
                        }
                        Text(
                            text = "Peso: ${pokemon.peso}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Altura: ${pokemon.altura}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onOtro, enabled = !estado.cargando) {
                Text("Otro")
            }
        },
        dismissButton = {
            TextButton(onClick = onCerrar) {
                Text("Cerrar")
            }
        }
    )
}