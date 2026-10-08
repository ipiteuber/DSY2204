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
    secondary = MentaTinta,
    onSecondary = Color.White,
    secondaryContainer = MentaClaro,
    onSecondaryContainer = MentaTinta,
    tertiary = LilaTinta,
    onTertiary = Color.White,
    tertiaryContainer = LilaClaro,
    onTertiaryContainer = LilaTinta,
    background = FondoClaro,
    onBackground = TextoPrincipal,
    surface = Color.White,
    onSurface = TextoPrincipal,
    surfaceVariant = AzulClaro,
    onSurfaceVariant = TextoSecundario,
    outline = Borde,
    outlineVariant = Borde,
    error = RojoError,
    onError = Color.White,
    errorContainer = RosaClaro,
    onErrorContainer = RosaTinta
)

private val EsquemaOscuro = darkColorScheme(
    primary = Color(0xFFA8CDF2),
    onPrimary = AzulOscuro,
    primaryContainer = Color(0xFF14456B),
    onPrimaryContainer = AzulClaro,
    secondary = Color(0xFF9DD9BC),
    onSecondary = Color(0xFF07301F),
    secondaryContainer = Color(0xFF14503A),
    onSecondaryContainer = MentaClaro,
    tertiary = Color(0xFFC2B8EC),
    onTertiary = Color(0xFF241B4F),
    background = Color(0xFF0E1218),
    onBackground = Color(0xFFE6EAEF),
    surface = Color(0xFF161C24),
    onSurface = Color(0xFFE6EAEF),
    surfaceVariant = Color(0xFF25303C),
    onSurfaceVariant = Color(0xFFC6CED7),
    outline = Color(0xFF8B96A2),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF5B1F2E),
    onErrorContainer = RosaClaro
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
