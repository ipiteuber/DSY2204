package cl.duoc.vozvisible.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.width
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.vozvisible.data.RegistroActividad
import cl.duoc.vozvisible.data.comoFechaCorta
import cl.duoc.vozvisible.data.frasesRapidas
import cl.duoc.vozvisible.data.usuariosIniciales
import cl.duoc.vozvisible.ui.theme.VozVisibleTheme
import cl.duoc.vozvisible.ui.viewmodel.EscribirViewModel

// Escribe un mensaje, lo muestra en pantalla completa para el interlocutor y lo guarda.
@Composable
fun EscribirScreen(
    correoUsuario: String,
    onVolver: () -> Unit,
    modelo: EscribirViewModel = viewModel()
) {
    val estado by modelo.estado.collectAsStateWithLifecycle()

    LaunchedEffect(correoUsuario) { modelo.cargar(correoUsuario) }

    if (estado.enPantallaGrande) {
        MensajeEnGrande(
            texto = estado.texto,
            onVolver = modelo::volverDesdePantallaGrande
        )
        return
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            CabeceraPantalla(titulo = "Escribir", onVolver = onVolver)

            Spacer(Modifier.height(8.dp))
            Text(
                text = "Escribe lo que quieres decir. Al confirmar se muestra en letras grandes para que la otra persona lo lea.",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = estado.texto,
                onValueChange = modelo::cambiarTexto,
                label = { Text("Tu mensaje") },
                textStyle = MaterialTheme.typography.titleLarge,
                minLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 140.dp)
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
                onClick = { modelo.confirmar(correoUsuario) },
                enabled = estado.texto.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Mostrar en grande", style = MaterialTheme.typography.titleMedium)
            }

            if (estado.aviso.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                TarjetaAviso(estado.aviso)
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Mensajes guardados",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.semantics { heading() }
            )
            Spacer(Modifier.height(8.dp))

            if (estado.guardados.isEmpty()) {
                Text(
                    text = "Todavía no has guardado mensajes.",
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
                        EncabezadoTabla()
                        HorizontalDivider()
                        estado.guardados.forEach { registro ->
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

// Pantalla completa con tipografia enorme: La lee el interlocutor, no la usuaria.
@Composable
private fun MensajeEnGrande(texto: String, onVolver: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = texto,
                    fontSize = 56.sp,
                    lineHeight = 66.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                )
            }
            Button(
                onClick = onVolver,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Volver a escribir", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

// Encabezado de la tabla de mensajes. Las columnas se reparten con weight,
// porque Compose no trae un componente de tabla.
@Composable
private fun EncabezadoTabla() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 10.dp)
    ) {
        Text(
            text = "Fecha",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Mensaje",
            modifier = Modifier.weight(2.6f),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.width(72.dp))
    }
}

@Composable
fun FilaRegistro(registro: RegistroActividad, onBorrar: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = registro.fecha.comoFechaCorta(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.size(2.dp))
            Text(text = registro.texto, style = MaterialTheme.typography.bodyLarge)
        }
        IconButton(onClick = onBorrar, modifier = Modifier.size(48.dp)) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Eliminar \"${registro.texto.take(30)}\""
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
fun EscribirScreenPreview() {
    VozVisibleTheme {
        EscribirScreen(correoUsuario = usuariosIniciales.first().correo, onVolver = {})
    }
}
