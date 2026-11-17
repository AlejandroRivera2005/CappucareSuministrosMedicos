package com.example.cappucaresuministrosmedicos.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PokemonTypePill(
    clave: String,
    etiqueta: String,
    modifier: Modifier = Modifier
) {
    val fondo = colorDeTipo(clave)

    Text(
        text = etiqueta,
        modifier = modifier
            .background(color = fondo, shape = RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        color = colorLegible(fondo),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
    )
}

private fun colorLegible(fondo: Color): Color =
    if (contraste(Color.Black, fondo) >= contraste(Color.White, fondo)) Color.Black else Color.White

private fun contraste(a: Color, b: Color): Float {
    val claro = maxOf(a.luminance(), b.luminance())
    val oscuro = minOf(a.luminance(), b.luminance())
    return (claro + 0.05f) / (oscuro + 0.05f)
}

private fun colorDeTipo(tipo: String): Color = when (tipo.lowercase()) {
    "normal" -> Color(0xFFA8A77A)
    "fire" -> Color(0xFFEE8130)
    "water" -> Color(0xFF6390F0)
    "electric" -> Color(0xFFF7D02C)
    "grass" -> Color(0xFF7AC74C)
    "ice" -> Color(0xFF96D9D6)
    "fighting" -> Color(0xFFC22E28)
    "poison" -> Color(0xFFA33EA1)
    "ground" -> Color(0xFFE2BF65)
    "flying" -> Color(0xFFA98FF3)
    "psychic" -> Color(0xFFF95587)
    "bug" -> Color(0xFFA6B91A)
    "rock" -> Color(0xFFB6A136)
    "ghost" -> Color(0xFF735797)
    "dragon" -> Color(0xFF6F35FC)
    "dark" -> Color(0xFF705746)
    "steel" -> Color(0xFFB7B7CE)
    "fairy" -> Color(0xFFD685AD)
    else -> Color.Gray
}