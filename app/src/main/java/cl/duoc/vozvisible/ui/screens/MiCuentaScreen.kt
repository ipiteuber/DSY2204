package cl.duoc.vozvisible.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.vozvisible.data.Usuario
import cl.duoc.vozvisible.data.usuariosIniciales
import cl.duoc.vozvisible.ui.theme.RosaClaro
import cl.duoc.vozvisible.ui.theme.RosaTinta
import cl.duoc.vozvisible.ui.theme.VozVisibleTheme
import cl.duoc.vozvisible.ui.viewmodel.MiCuentaViewModel

// Mi cuenta: Consulta de los datos del perfil y baja definitiva de la cuenta.
@Composable
fun MiCuentaScreen(
    correoUsuario: String,
    onVolver: () -> Unit,
    onCuentaEliminada: () -> Unit,
    modelo: MiCuentaViewModel = viewModel()
) {
    val estado by modelo.estado.collectAsStateWithLifecycle()

    LaunchedEffect(correoUsuario) { modelo.cargar(correoUsuario) }

    LaunchedEffect(estado.cuentaEliminada) {
        if (estado.cuentaEliminada) onCuentaEliminada()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            CabeceraPantalla(titulo = "Mi cuenta", onVolver = onVolver)
            Spacer(Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Datos registrados",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.semantics { heading() }
                    )
                    Spacer(Modifier.height(12.dp))
                    DatoCuenta("Nombre", estado.perfil?.nombre ?: "Sin registrar")
                    DatoCuenta("Correo", estado.perfil?.correo ?: correoUsuario)
                    DatoCuenta("Nivel de audición", estado.perfil?.nivel?.etiqueta ?: "Sin registrar")
                    DatoCuenta("Modo preferido", estado.perfil?.modo?.etiqueta ?: "Sin registrar")
                    DatoCuenta(
                        "Apoyos activos",
                        estado.perfil?.apoyos?.takeIf { it.isNotEmpty() }
                            ?.joinToString(", ") ?: "Ninguno"
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = RosaClaro,
                    contentColor = RosaTinta
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Eliminar la cuenta",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.semantics { heading() }
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Se eliminan el perfil, los mensajes guardados y las frases " +
                            "reproducidas. La operación no se puede deshacer.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = modelo::pedirConfirmacion,
                        enabled = !estado.procesando,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RosaTinta,
                            contentColor = MaterialTheme.colorScheme.onError
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text(
                            text = "Eliminar mi cuenta",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            if (estado.mensaje.isNotBlank() && !estado.confirmando) {
                Spacer(Modifier.height(16.dp))
                TarjetaAviso(estado.mensaje, esError = estado.esError)
            }

            Spacer(Modifier.height(24.dp))
            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Volver al menú", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(16.dp))
        }
    }

    if (estado.confirmando) {
        AlertDialog(
            onDismissRequest = modelo::cancelarConfirmacion,
            title = { Text("Confirmar la eliminación") },
            text = {
                Column {
                    Text(
                        text = "Escribe tu contraseña para confirmar la baja definitiva " +
                            "de la cuenta.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = estado.clave,
                        onValueChange = modelo::cambiarClave,
                        label = { Text("Contraseña") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = estado.esError,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (estado.mensaje.isNotBlank()) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = estado.mensaje,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { modelo.eliminar(correoUsuario) },
                    enabled = !estado.procesando
                ) {
                    Text(if (estado.procesando) "Eliminando…" else "Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = modelo::cancelarConfirmacion) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun DatoCuenta(etiqueta: String, valor: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(text = etiqueta, style = MaterialTheme.typography.labelLarge)
        Text(text = valor, style = MaterialTheme.typography.bodyLarge)
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
}

@Preview(showBackground = true, widthDp = 400, heightDp = 900)
@Composable
fun MiCuentaScreenPreview() {
    VozVisibleTheme {
        val demo: Usuario = usuariosIniciales.first()
        MiCuentaScreen(
            correoUsuario = demo.correo,
            onVolver = {},
            onCuentaEliminada = {}
        )
    }
}
