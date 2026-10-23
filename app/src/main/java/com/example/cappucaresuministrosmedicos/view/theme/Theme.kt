package com.example.cappucaresuministrosmedicos.view.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Verde700,
    onPrimary = Color.White,
    primaryContainer = VerdeClaro,
    onPrimaryContainer = Verde800,

    secondary = VerdeGrisaceo,
    onSecondary = Color.White,
    secondaryContainer = VerdeSuperficie,
    onSecondaryContainer = Tinta,

    // El "terciario" se usa como color de aviso (bajo mínimo)
    tertiary = AvisoIntenso,
    onTertiary = Color.White,
    tertiaryContainer = AvisoFondo,
    onTertiaryContainer = AvisoTexto,

    error = ErrorIntenso,
    onError = Color.White,
    errorContainer = ErrorFondo,
    onErrorContainer = ErrorTexto,

    background = VerdeFondo,
    onBackground = Tinta,
    surface = VerdeFondo,
    onSurface = Tinta,
    surfaceVariant = VerdeSuperficie,
    onSurfaceVariant = VerdeGrisaceo,
    outline = Color(0xFF8AA19B),
    outlineVariant = VerdeBordeSuave,

    // Tarjetas, diálogos y barra inferior en blanco, como en el mockup
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color.White,
    surfaceContainer = Color.White,
    surfaceContainerHigh = Color.White,
    surfaceContainerHighest = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Turquesa,
    onPrimary = OscuroFondo,
    primaryContainer = Verde800,
    onPrimaryContainer = VerdeClaro,

    secondary = OscuroTextoSecundario,
    onSecondary = OscuroFondo,
    secondaryContainer = OscuroSuperficieAlta,
    onSecondaryContainer = OscuroTexto,

    tertiary = Color(0xFFFFB95C),
    onTertiary = Color(0xFF3B2000),
    tertiaryContainer = Color(0xFF5C3500),
    onTertiaryContainer = AvisoFondo,

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = ErrorTexto,
    onErrorContainer = ErrorFondo,

    background = OscuroFondo,
    onBackground = OscuroTexto,
    surface = OscuroFondo,
    onSurface = OscuroTexto,
    surfaceVariant = OscuroSuperficieAlta,
    onSurfaceVariant = OscuroTextoSecundario,
    outline = Color(0xFF6E8A84),
    outlineVariant = OscuroBorde,

    surfaceContainerLowest = Color(0xFF0B1A17),
    surfaceContainerLow = Color(0xFF152C28),
    surfaceContainer = OscuroSuperficie,
    surfaceContainerHigh = Color(0xFF1F3B36),
    surfaceContainerHighest = OscuroSuperficie
)

@Composable
fun CappucareSuministrosMedicosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
