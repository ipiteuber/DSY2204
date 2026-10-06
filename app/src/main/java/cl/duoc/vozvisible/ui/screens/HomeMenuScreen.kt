package cl.duoc.vozvisible.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
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
import cl.duoc.vozvisible.ui.theme.AmbarAcento
import cl.duoc.vozvisible.ui.theme.AzulPrincipal
import cl.duoc.vozvisible.ui.theme.GrisAzulado
import cl.duoc.vozvisible.ui.theme.VerdeAcento
import cl.duoc.vozvisible.ui.theme.VozVisibleTheme

private data class OpcionMenu(
    val titulo: String,
    val detalle: String,
    val icono: ImageVector,
    val color: Color,
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
    onCerrarSesion: () -> Unit
) {
    var perfil by remember { mutableStateOf<Usuario?>(null) }

    LaunchedEffect(correoUsuario) {
        perfil = Repositorios.cuentas.perfil(correoUsuario)
    }

    val opciones = listOf(
        OpcionMenu(
            "Escribir", "Muestra tu mensaje en pantalla grande",
            Icons.Filled.Create, AzulPrincipal, onEscribir
        ),
        OpcionMenu(
            "Hablar", "El teléfono dice en voz alta lo que escribes",
            Icons.Filled.PlayArrow, AmbarAcento, onHablar
        ),
        OpcionMenu(
            "Buscar dispositivo", "Muestra dónde está tu teléfono",
            Icons.Filled.LocationOn, VerdeAcento, onBuscarDispositivo
        ),
        OpcionMenu(
            "Ayuda", "Cómo se usa cada función",
            Icons.Filled.Info, GrisAzulado, onAyuda
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
                        text = "Hola, $nombreUsuario",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.semantics { heading() }
                    )
                    perfil?.let { datos ->
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = datos.resumenPerfil(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "¿Qué quieres hacer?",
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
            containerColor = opcion.color,
            contentColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = opcion.icono,
                contentDescription = null,
                modifier = Modifier.size(44.dp)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = opcion.titulo,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = opcion.detalle,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
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
            onCerrarSesion = {}
        )
    }
}
