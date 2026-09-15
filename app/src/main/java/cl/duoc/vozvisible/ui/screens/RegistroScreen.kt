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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import cl.duoc.vozvisible.data.ModoComunicacion
import cl.duoc.vozvisible.data.NivelAudicion
import cl.duoc.vozvisible.data.RepositorioUsuarios
import cl.duoc.vozvisible.data.ResultadoRegistro
import cl.duoc.vozvisible.data.Usuario
import cl.duoc.vozvisible.data.apoyosDisponibles
import cl.duoc.vozvisible.data.esCorreoValido
import cl.duoc.vozvisible.ui.theme.VozVisibleTheme

// Registro de usuario. Guarda la cuenta nueva en el arreglo y lo muestra en la tabla.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(onRegistrado: (Usuario) -> Unit, onVolver: () -> Unit) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var clave by rememberSaveable { mutableStateOf("") }
    var claveRepetida by rememberSaveable { mutableStateOf("") }

    var nivelExpandido by rememberSaveable { mutableStateOf(false) }
    var nivel by rememberSaveable { mutableStateOf(NivelAudicion.LEVE) }
    var modo by rememberSaveable { mutableStateOf(ModoComunicacion.ESCRIBIR) }
    var aceptaTerminos by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf("") }

    val seleccionados = remember { mutableStateMapOf<String, Boolean>() }

    val correoInvalido = correo.isNotBlank() && !correo.esCorreoValido()
    val errorClave = claveRepetida.isNotEmpty() && claveRepetida != clave
    val formularioValido = nombre.isNotBlank() && correo.esCorreoValido() &&
            clave.isNotEmpty() && clave == claveRepetida && aceptaTerminos

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
                    text = "Cuéntanos cómo prefieres comunicarte para adaptar la aplicación a ti.",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre completo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = correo,
                    onValueChange = {
                        correo = it
                        error = ""
                    },
                    label = { Text("Correo electrónico") },
                    singleLine = true,
                    isError = error.isNotEmpty() || correoInvalido,
                    supportingText = {
                        when {
                            error.isNotEmpty() -> Text(error)
                            correoInvalido -> Text("Usa un formato como nombre@correo.cl.")
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = clave,
                    onValueChange = { clave = it },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = claveRepetida,
                    onValueChange = { claveRepetida = it },
                    label = { Text("Repetir contraseña") },
                    singleLine = true,
                    isError = errorClave,
                    supportingText = {
                        if (errorClave) Text("Las contraseñas no coinciden.")
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
                        value = nivel.etiqueta,
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
                                    nivel = opcion
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
                                    selected = modo == opcion,
                                    onClick = { modo = opcion },
                                    role = Role.RadioButton
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = modo == opcion, onClick = null)
                            Spacer(Modifier.size(12.dp))
                            Text(opcion.etiqueta, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "Apoyos que quieres activar",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.semantics { heading() }
                )
                apoyosDisponibles.forEach { apoyo ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .toggleable(
                                value = seleccionados[apoyo] == true,
                                onValueChange = { seleccionados[apoyo] = it },
                                role = Role.Checkbox
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = seleccionados[apoyo] == true, onCheckedChange = null)
                        Spacer(Modifier.size(12.dp))
                        Text(apoyo, style = MaterialTheme.typography.bodyLarge)
                    }
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .toggleable(
                            value = aceptaTerminos,
                            onValueChange = { aceptaTerminos = it },
                            role = Role.Checkbox
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = aceptaTerminos, onCheckedChange = null)
                    Spacer(Modifier.size(12.dp))
                    Text(
                        text = "Acepto los términos y condiciones",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        val nuevo = Usuario(
                            nombre = nombre.trim(),
                            correo = correo.trim(),
                            clave = clave,
                            nivel = nivel,
                            modo = modo,
                            apoyos = seleccionados.filterValues { it }.keys.toSet()
                        )
                        when (val resultado = RepositorioUsuarios.registrar(nuevo)) {
                            is ResultadoRegistro.Exito -> onRegistrado(resultado.usuario)
                            is ResultadoRegistro.Error -> error = resultado.mensaje
                        }
                    },
                    enabled = formularioValido,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Registrarme", style = MaterialTheme.typography.titleMedium)
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

                Spacer(Modifier.height(28.dp))

                Text(
                    text = "Cuentas registradas",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.semantics { heading() }
                )
                Spacer(Modifier.height(8.dp))
                TablaUsuarios()

                Spacer(Modifier.height(16.dp))
                ResumenRegistros()

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

// La contrasena no se muestra en la tabla a proposito
@Composable
private fun TablaUsuarios() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            FilaUsuario("Nombre", "Correo", "Modo", esEncabezado = true)
            HorizontalDivider()
            RepositorioUsuarios.listado.forEach { usuario ->
                FilaUsuario(usuario.nombre, usuario.correo, usuario.modo.etiqueta)
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun ResumenRegistros() {
    val datos = RepositorioUsuarios.estadisticas()
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Resumen de las cuentas",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.semantics { heading() }
            )
            Spacer(Modifier.height(8.dp))
            datos.nombresPorModo.forEach { (modo, nombres) ->
                Text(
                    text = "${modo.etiqueta}: ${nombres.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Niveles: " + datos.totalPorNivel.entries.joinToString(" · ") {
                    "${it.key.etiqueta} (${it.value})"
                },
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = if (datos.conApoyos.isEmpty()) {
                    "Todavía nadie activa apoyos."
                } else {
                    "Con apoyos activos: ${datos.conApoyos.joinToString(", ")}"
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// Compose no trae componente de tabla, las columnas se reparten con weight
@Composable
private fun FilaUsuario(
    nombre: String,
    correo: String,
    modo: String,
    esEncabezado: Boolean = false
) {
    val peso = if (esEncabezado) FontWeight.Bold else FontWeight.Normal
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Text(
            text = nombre,
            modifier = Modifier.weight(1.4f),
            fontWeight = peso,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = correo,
            modifier = Modifier.weight(2f),
            fontWeight = peso,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = modo,
            modifier = Modifier.weight(1f),
            fontWeight = peso,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegistroScreenPreview() {
    VozVisibleTheme {
        RegistroScreen(onRegistrado = {}, onVolver = {})
    }
}
