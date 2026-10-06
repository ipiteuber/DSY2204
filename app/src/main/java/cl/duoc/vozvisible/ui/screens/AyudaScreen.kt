package cl.duoc.vozvisible.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duoc.vozvisible.ui.theme.AmbarAcento
import cl.duoc.vozvisible.ui.theme.AzulPrincipal
import cl.duoc.vozvisible.ui.theme.GrisAzulado
import cl.duoc.vozvisible.ui.theme.VerdeAcento
import cl.duoc.vozvisible.ui.theme.VozVisibleTheme

private data class PasoAyuda(
    val numero: Int,
    val titulo: String,
    val detalle: String,
    val color: Color
)

private val pasos = listOf(
    PasoAyuda(
        1, "Escribir",
        "Toca Escribir en el menú, redacta tu mensaje y presiona Mostrar en pantalla grande. " +
            "El texto aparece en letra muy grande para que la otra persona lo lea sin esfuerzo.",
        AzulPrincipal
    ),
    PasoAyuda(
        2, "Hablar",
        "Toca Hablar, escribe lo que quieres decir y presiona Reproducir en voz alta. " +
            "El teléfono lo dice por ti, así que no necesitas usar tu voz.",
        AmbarAcento
    ),
    PasoAyuda(
        3, "Buscar dispositivo",
        "Toca Buscar dispositivo y luego Actualizar ubicación. La aplicación te muestra dónde " +
            "está el teléfono. La primera vez te va a pedir permiso de ubicación: acéptalo.",
        VerdeAcento
    ),
    PasoAyuda(
        4, "Tus frases quedan guardadas",
        "Cada mensaje que escribes o reproduces se guarda en tu cuenta. Puedes volver a verlos " +
            "en la misma pantalla y borrar los que ya no necesites.",
        GrisAzulado
    )
)

// Tutorial de uso, pensado para quien abre la aplicacion por primera vez.
@Composable
fun AyudaScreen(onVolver: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            CabeceraPantalla(titulo = "Ayuda", onVolver = onVolver)

            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Cómo usar VozVisible",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.semantics { heading() }
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Cuatro pasos para empezar. No necesitas conocimientos técnicos.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.height(20.dp))

                pasos.forEach { paso ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = paso.numero.toString(),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = paso.color,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.size(12.dp))
                            Column {
                                Text(
                                    text = paso.titulo,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = paso.color
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = paso.detalle,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "¿Necesitas más ayuda?",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Puedes volver a esta pantalla cuando quieras desde el menú principal.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AyudaScreenPreview() {
    VozVisibleTheme {
        AyudaScreen(onVolver = {})
    }
}
