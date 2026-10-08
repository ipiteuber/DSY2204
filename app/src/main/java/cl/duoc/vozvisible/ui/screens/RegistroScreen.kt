package cl.duoc.vozvisible.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.vozvisible.data.EstadisticasUsuarios
import cl.duoc.vozvisible.data.ModoComunicacion
import cl.duoc.vozvisible.data.NivelAudicion
import cl.duoc.vozvisible.data.Usuario
import cl.duoc.vozvisible.data.apoyosDisponibles
import cl.duoc.vozvisible.data.estadisticas
import cl.duoc.vozvisible.ui.theme.VozVisibleTheme
import cl.duoc.vozvisible.ui.viewmodel.RegistroViewModel

// Registro de usuario. Crea la cuenta en el repositorio activo y la muestra en la tabla.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onRegistrado: (Usuario) -> Unit,
    onVolver: () -> Unit,
    modelo: RegistroViewModel = viewModel()
) {
    val estado by modelo.estado.collectAsStateWithLifecycle()
    var nivelExpandido by rememberSaveable { mutableStateOf(false) }


    LaunchedEffect(estado.registrado) {
        estado.registrado?.let { usuario ->
            modelo.limpiarRegistrado()
            onRegistrado(usuario)
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(modifier = Modifier.widthIn(max = 520.dp)) {

                Text(
                    text = "Crear cuenta",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.semantics { heading() }
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Indica tu forma preferida de comunicación para adaptar la aplicación.",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = estado.nombre,
                    onValueChange = modelo::cambiarNombre,
                    label = { Text("Nombre completo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = estado.correo,
                    onValueChange = modelo::cambiarCorreo,
                    label = { Text("Correo electrónico") },
                    singleLine = true,
                    isError = estado.error.isNotEmpty() || estado.correoInvalido,
                    supportingText = {
                        when {
                            estado.error.isNotEmpty() -> Text(estado.error)
                            estado.correoInvalido -> Text("Usa un formato como nombre@correo.cl.")
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = estado.clave,
                    onValueChange = modelo::cambiarClave,
                    label = { Text("Contraseña") },
                    singleLine = true,
                    supportingText = { Text("Al menos 8 caracteres, con letras y números.") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = estado.claveRepetida,
                    onValueChange = modelo::cambiarClaveRepetida,
                    label = { Text("Repetir contraseña") },
                    singleLine = true,
                    isError = estado.errorClave != null,
                    supportingText = {
                        estado.errorClave?.let { Text(it) }
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "Nivel de audición",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.semantics { heading() }
                )
                Spacer(Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = nivelExpandido,
                    onExpandedChange = { nivelExpandido = !nivelExpandido }
                ) {
                    OutlinedTextField(
                        value = estado.nivel.etiqueta,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Selecciona una opción") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = nivelExpandido)
                        },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = nivelExpandido,
                        onDismissRequest = { nivelExpandido = false }
                    ) {
                        NivelAudicion.entries.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion.etiqueta) },
                                onClick = {
                                    modelo.cambiarNivel(opcion)
                                    nivelExpandido = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "¿Cómo prefieres comunicarte?",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.semantics { heading() }
                )
                // selectableGroup le dice a TalkBack que es una seleccion unica
                Column(Modifier.selectableGroup()) {
                    ModoComunicacion.entries.forEach { opcion ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .selectable(
                                    selected = estado.modo == opcion,
                                    onClick = { modelo.cambiarModo(opcion) },
                                    role = Role.RadioButton
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = estado.modo == opcion, onClick = null)
                            Spacer(Modifier.size(12.dp))
                            Text(opcion.etiqueta, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "Apoyos por activar",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.semantics { heading() }
                )
                apoyosDisponibles.forEach { apoyo ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .toggleable(
                                value = apoyo in estado.apoyos,
                                onValueChange = { modelo.alternarApoyo(apoyo) },
                                role = Role.Checkbox
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = apoyo in estado.apoyos, onCheckedChange = null)
                        Spacer(Modifier.size(12.dp))
                        Text(apoyo, style = MaterialTheme.typography.bodyLarge)
                    }
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .toggleable(
                            value = estado.aceptaTerminos,
                            onValueChange = modelo::cambiarTerminos,
                            role = Role.Checkbox
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = estado.aceptaTerminos, onCheckedChange = null)
                    Spacer(Modifier.size(12.dp))
                    Text(
                        text = "Acepto los términos y condiciones",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = modelo::registrar,
                    enabled = estado.formularioValido,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = if (estado.cargando) "Creando cuenta…" else "Registrarme",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(Modifier.height(8.dp))

                TextButton(
                    onClick = onVolver,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Ya tengo cuenta, volver al inicio")
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}




@Preview(showBackground = true, heightDp = 1400)
@Composable
fun RegistroScreenPreview() {
    VozVisibleTheme {
        RegistroScreen(onRegistrado = {}, onVolver = {})
    }
}
