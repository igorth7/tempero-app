package com.igor.tempero.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TemperoColors = lightColorScheme(
    primary = Laranja,
    onPrimary = Color.White,
    primaryContainer = LaranjaClaro,
    onPrimaryContainer = Laranja,
    secondary = Marrom,
    secondaryContainer = LaranjaClaro,
    onSecondaryContainer = Laranja,
    surfaceContainer = Color.White,
    background = Creme,
    onBackground = Marrom,
    surface = Color.White,
    onSurface = Marrom,
    surfaceVariant = LaranjaClaro,
    onSurfaceVariant = CinzaTexto,
    outline = Linha,
    error = VermelhoErro
)

@Composable
fun TemperoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TemperoColors,
        typography = Typography,
        content = content
    )
}
