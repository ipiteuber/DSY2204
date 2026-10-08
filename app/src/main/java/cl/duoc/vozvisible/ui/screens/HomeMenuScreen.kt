package cl.duoc.vozvisible.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duoc.vozvisible.data.Repositorios
import cl.duoc.vozvisible.data.Usuario
import cl.duoc.vozvisible.data.resumenPerfil
import cl.duoc.vozvisible.data.usuariosIniciales
import cl.duoc.vozvisible.ui.theme.AzulClaro
import cl.duoc.vozvisible.ui.theme.AzulOscuro
import cl.duoc.vozvisible.ui.theme.DuraznoClaro
import cl.duoc.vozvisible.ui.theme.DuraznoTinta
import cl.duoc.vozvisible.ui.theme.LilaClaro
import cl.duoc.vozvisible.ui.theme.LilaTinta
import cl.duoc.vozvisible.ui.theme.MentaClaro
import cl.duoc.vozvisible.ui.theme.MentaTinta
import cl.duoc.vozvisible.ui.theme.VozVisibleTheme

private data class OpcionMenu(
    val titulo: String,
    val detalle: String,
    val icono: ImageVector,
    val fondo: Color,
    val tinta: Color,
    val accion: () -> Unit
)

// Menu principal: Saluda al usuario y abre cada funcion en tarjetas grandes.
@Composable
fun HomeMenuScreen(
    nombreUsuario: String,
    correoUsuario: String,
    onEscribir: () -> Unit,
    onHablar: () -> Unit,
    onBuscarDispositivo: () -> Unit,
    onAyuda: () -> Unit,
    onMiCuenta: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    var perfil by remember { mutableStateOf<Usuario?>(null) }

    LaunchedEffect(correoUsuario) {
        perfil = Repositorios.cuentas.perfil(correoUsuario)
    }

    // El saludo usa el nombre del perfil cuando existe: La ruta puede traer solo
    // el prefijo del correo si la cuenta se creo antes de guardar el perfil.
    val saludo = perfil?.nombre?.takeIf { it.isNotBlank() }
        ?: nombreUsuario.takeIf { it.isNotBlank() }
        ?: correoUsuario.substringBefore('@')

    val opciones = listOf(
        OpcionMenu(
            "Escribir", "Convierte el texto escrito en voz",
            Icons.Filled.Create, AzulClaro, AzulOscuro, onEscribir
        ),
        OpcionMenu(
            "Hablar", "Transcribe en pantalla lo que se dice",
            Icons.Filled.PlayArrow, DuraznoClaro, DuraznoTinta, onHablar
        ),
        OpcionMenu(
            "Buscar dispositivo", "Verifica la ubicación del teléfono",
            Icons.Filled.LocationOn, MentaClaro, MentaTinta, onBuscarDispositivo
        ),
        OpcionMenu(
            "Ayuda", "Tutorial de uso de cada función",
            Icons.Filled.Info, LilaClaro, LilaTinta, onAyuda
        ),
        OpcionMenu(
            "Mi cuenta", "Datos del perfil y baja de la cuenta",
            Icons.Filled.AccountCircle, AzulClaro, AzulOscuro, onMiCuenta
        )
    )

    Surface(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    Text(
                        text = "Hola, $saludo",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.semantics { heading() }
                    )
                    perfil?.let { datos ->
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = datos.resumenPerfil(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Selecciona una función",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.semantics { heading() }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            items(opciones) { opcion ->
                TarjetaMenu(opcion)
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = onCerrarSesion,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text("Cerrar sesión", style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TarjetaMenu(opcion: OpcionMenu) {
    Card(
        onClick = opcion.accion,
        colors = CardDefaults.cardColors(
            containerColor = opcion.fondo,
            contentColor = opcion.tinta
        ),
        modifier = Modifier
            .fillMaxWidth()
            // Altura minima en vez de fija: Si el texto necesita una linea mas,
            // la tarjeta crece en lugar de recortar el detalle.
            .defaultMinSize(minHeight = 172.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = opcion.icono,
                contentDescription = null,
                modifier = Modifier.size(42.dp)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = opcion.titulo,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = opcion.detalle,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 900)
@Composable
fun HomeMenuScreenPreview() {
    VozVisibleTheme {
        HomeMenuScreen(
            nombreUsuario = "Camila",
            correoUsuario = usuariosIniciales.first().correo,
            onEscribir = {},
            onHablar = {},
            onBuscarDispositivo = {},
            onAyuda = {},
            onMiCuenta = {},
            onCerrarSesion = {}
        )
    }
}
