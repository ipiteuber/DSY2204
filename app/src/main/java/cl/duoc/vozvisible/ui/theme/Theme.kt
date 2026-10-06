package cl.duoc.vozvisible.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaClaro = lightColorScheme(
    primary = AzulPrincipal,
    onPrimary = Color.White,
    primaryContainer = AzulClaro,
    onPrimaryContainer = AzulOscuro,
    secondary = AmbarAcento,
    onSecondary = Color.White,
    secondaryContainer = AmbarClaro,
    onSecondaryContainer = Color(0xFF3A2200),
    background = FondoClaro,
    onBackground = TextoPrincipal,
    surface = Color.White,
    onSurface = TextoPrincipal,
    surfaceVariant = AzulClaro,
    onSurfaceVariant = TextoSecundario,
    outline = Color(0xFF6B7683),
    error = RojoError,
    onError = Color.White
)

private val EsquemaOscuro = darkColorScheme(
    primary = Color(0xFF9CC6F0),
    onPrimary = AzulOscuro,
    primaryContainer = Color(0xFF11486F),
    onPrimaryContainer = AzulClaro,
    secondary = Color(0xFFF2C078),
    onSecondary = Color(0xFF432700),
    background = Color(0xFF0E1218),
    onBackground = Color(0xFFE4E8ED),
    surface = Color(0xFF161C24),
    onSurface = Color(0xFFE4E8ED),
    surfaceVariant = Color(0xFF25303C),
    onSurfaceVariant = Color(0xFFC3CBD4),
    outline = Color(0xFF8B96A2),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410)
)

// Sin dynamicColor: Los colores dinamicos del sistema pueden bajar el contraste
// y aca el contraste es un requisito, no una preferencia estetica.
@Composable
fun VozVisibleTheme(
    temaOscuro: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (temaOscuro) EsquemaOscuro else EsquemaClaro,
        typography = Tipografia,
        content = content
    )
}
