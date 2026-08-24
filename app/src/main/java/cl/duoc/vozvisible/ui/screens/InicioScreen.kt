package cl.duoc.vozvisible.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duoc.vozvisible.data.FraseRapida
import cl.duoc.vozvisible.data.frasesRapidas
import cl.duoc.vozvisible.ui.theme.VozVisibleTheme

// Pantalla posterior al login. Muestra las frases rapidas en grilla.
@Composable
fun InicioScreen(nombreUsuario: String, onCerrarSesion: () -> Unit) {
    val ancho = LocalConfiguration.current.screenWidthDp
    val columnas = when {
        ancho >= 840 -> 3
        ancho >= 600 -> 2
        else -> 1
    }

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
                    Spacer(Modifier.height(4.dp))
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
                }
            }

            items(frasesRapidas.size) { indice ->
                TarjetaFrase(frasesRapidas[indice])
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
        InicioScreen(nombreUsuario = "Camila", onCerrarSesion = {})
    }
}

@Preview(showBackground = true, widthDp = 900, heightDp = 700)
@Composable
fun InicioScreenTabletPreview() {
    VozVisibleTheme {
        InicioScreen(nombreUsuario = "Camila", onCerrarSesion = {})
    }
}
