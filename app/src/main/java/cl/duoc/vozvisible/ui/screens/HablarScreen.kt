package cl.duoc.vozvisible.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.vozvisible.data.frasesRapidas
import cl.duoc.vozvisible.data.usuariosIniciales
import cl.duoc.vozvisible.ui.theme.VozVisibleTheme
import cl.duoc.vozvisible.ui.viewmodel.HablarViewModel
import java.util.Locale

// Lee en voz alta el texto escrito con el motor TextToSpeech del sistema.
@Composable
fun HablarScreen(
    correoUsuario: String,
    onVolver: () -> Unit,
    modelo: HablarViewModel = viewModel()
) {
    val estado by modelo.estado.collectAsStateWithLifecycle()
    val contexto = LocalContext.current
    val motor = remember { mutableStateOf<TextToSpeech?>(null) }

    LaunchedEffect(correoUsuario) { modelo.cargar(correoUsuario) }

    // El motor TTS es un recurso del sistema: Se libera al salir de la pantalla.
    DisposableEffect(contexto) {
        var instancia: TextToSpeech? = null
        instancia = TextToSpeech(contexto) { resultado ->
            val idioma = instancia?.setLanguage(Locale("es", "CL")) ?: TextToSpeech.LANG_NOT_SUPPORTED
            modelo.motorListo(resultado == TextToSpeech.SUCCESS && idioma >= TextToSpeech.LANG_AVAILABLE)
        }
        motor.value = instancia
        onDispose {
            instancia.stop()
            instancia.shutdown()
            motor.value = null
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            CabeceraPantalla(titulo = "Hablar", onVolver = onVolver)

            Spacer(Modifier.height(8.dp))
            Text(
                text = "Escribe una frase y el teléfono la dirá en voz alta por ti.",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = estado.texto,
                onValueChange = modelo::cambiarTexto,
                label = { Text("Frase para decir en voz alta") },
                textStyle = MaterialTheme.typography.titleLarge,
                minLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Frases rápidas",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.semantics { heading() }
            )
            Spacer(Modifier.height(8.dp))
            frasesRapidas.take(4).forEach { frase ->
                AssistChip(
                    onClick = { modelo.cambiarTexto(frase.texto) },
                    label = { Text(frase.texto) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .padding(vertical = 4.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val frase = estado.texto.trim()
                    motor.value?.speak(frase, TextToSpeech.QUEUE_FLUSH, null, null)
                    modelo.registrarReproduccion(correoUsuario, frase)
                },
                enabled = estado.puedeHablar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Decir en voz alta", style = MaterialTheme.typography.titleMedium)
            }

            if (estado.aviso.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                TarjetaAviso(estado.aviso, esError = !estado.motorListo)
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Frases reproducidas",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.semantics { heading() }
            )
            Spacer(Modifier.height(8.dp))

            if (estado.historial.isEmpty()) {
                Text(
                    text = "Todavía no has reproducido frases.",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        estado.historial.forEach { registro ->
                            FilaRegistro(
                                registro = registro,
                                onBorrar = { modelo.borrar(correoUsuario, registro.id) }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
fun HablarScreenPreview() {
    VozVisibleTheme {
        HablarScreen(correoUsuario = usuariosIniciales.first().correo, onVolver = {})
    }
}
