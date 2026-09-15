package cl.duoc.vozvisible.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duoc.vozvisible.data.FraseRapida
import cl.duoc.vozvisible.data.RepositorioUsuarios
import cl.duoc.vozvisible.data.buscarFrases
import cl.duoc.vozvisible.data.categoriasFrases
import cl.duoc.vozvisible.data.filtrarSi
import cl.duoc.vozvisible.data.frasesPorCategoria
import cl.duoc.vozvisible.data.resumenPerfil
import cl.duoc.vozvisible.data.usuariosIniciales
import cl.duoc.vozvisible.ui.theme.VozVisibleTheme

// Pantalla posterior al login. Muestra las frases rapidas en grilla.
@Composable
fun InicioScreen(nombreUsuario: String, correoUsuario: String, onCerrarSesion: () -> Unit) {
    val ancho = LocalConfiguration.current.screenWidthDp
    val columnas = when {
        ancho >= 840 -> 3
        ancho >= 600 -> 2
        else -> 1
    }

    var busqueda by rememberSaveable { mutableStateOf("") }
    var categoria by rememberSaveable { mutableStateOf<String?>(null) }

    val porCategoria = remember { frasesPorCategoria() }
    val usuario = RepositorioUsuarios.buscarPorCorreo(correoUsuario)
    val frases = buscarFrases(busqueda).filtrarSi(categoria != null) { it.categoria == categoria }

    Surface(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columnas),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    Text(
                        text = "Hola, $nombreUsuario",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.semantics { heading() }
                    )
                    usuario?.let { perfil ->
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = perfil.resumenPerfil(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Toca una frase para mostrarla en pantalla grande y reproducirla en voz alta.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(20.dp))
                    AccesosDirectos()
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = "Frases rápidas",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.semantics { heading() }
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = busqueda,
                        onValueChange = { busqueda = it },
                        label = { Text("Buscar una frase") },
                        leadingIcon = {
                            Icon(Icons.Filled.Search, contentDescription = null)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        FilterChip(
                            selected = categoria == null,
                            onClick = { categoria = null },
                            label = { Text("Todas (${porCategoria.values.sumOf { it.size }})") }
                        )
                        categoriasFrases.forEach { nombre ->
                            FilterChip(
                                selected = categoria == nombre,
                                onClick = {
                                    categoria = if (categoria == nombre) null else nombre
                                },
                                label = { Text("$nombre (${porCategoria[nombre]?.size ?: 0})") }
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }

            items(frases.size) { indice ->
                TarjetaFrase(frases[indice])
            }

            if (frases.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "No hay frases que coincidan con esa búsqueda.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = onCerrarSesion,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text("Cerrar sesión", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun AccesosDirectos() {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        AccesoDirecto(
            titulo = "Escribir",
            detalle = "Redacta y el teléfono lo dice por ti",
            icono = Icons.Filled.Create,
            modifier = Modifier.weight(1f)
        )
        AccesoDirecto(
            titulo = "Escuchar",
            detalle = "Transcribe lo que te están diciendo",
            icono = Icons.Filled.PlayArrow,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AccesoDirecto(
    titulo: String,
    detalle: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier.height(140.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(titulo, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(detalle, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun TarjetaFrase(frase: FraseRapida) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = frase.categoria.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(6.dp))
            Text(frase.texto, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun InicioScreenPreview() {
    VozVisibleTheme {
        InicioScreen(
            nombreUsuario = "Camila",
            correoUsuario = usuariosIniciales.first().correo,
            onCerrarSesion = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 900, heightDp = 700)
@Composable
fun InicioScreenTabletPreview() {
    VozVisibleTheme {
        InicioScreen(
            nombreUsuario = "Camila",
            correoUsuario = usuariosIniciales.first().correo,
            onCerrarSesion = {}
        )
    }
}
