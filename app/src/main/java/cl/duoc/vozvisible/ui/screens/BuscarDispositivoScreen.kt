package cl.duoc.vozvisible.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.vozvisible.data.formatearCoordenadas
import cl.duoc.vozvisible.ui.theme.MentaTinta
import cl.duoc.vozvisible.ui.viewmodel.BuscarDispositivoViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

// Muestra la ultima ubicacion conocida del dispositivo y guarda cada consulta.
@SuppressLint("MissingPermission")
@Composable
fun BuscarDispositivoScreen(
    correoUsuario: String,
    onVolver: () -> Unit,
    vm: BuscarDispositivoViewModel = viewModel()
) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    val contexto = LocalContext.current

    LaunchedEffect(correoUsuario) { vm.cargar(correoUsuario) }

    fun pedirUltimaUbicacion() {
        val cliente = LocationServices.getFusedLocationProviderClient(contexto)
        // lastLocation devuelve null si el telefono no tomo una posicion hace poco,
        // asi que en ese caso se pide una lectura nueva en vez de dar por fallida la busqueda.
        cliente.lastLocation
            .addOnSuccessListener { ubicacion ->
                if (ubicacion != null) {
                    vm.ubicacionEncontrada(correoUsuario, ubicacion.latitude, ubicacion.longitude)
                } else {
                    cliente.getCurrentLocation(
                        Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                        CancellationTokenSource().token
                    )
                        .addOnSuccessListener { nueva ->
                            if (nueva == null) vm.sinUbicacion()
                            else vm.ubicacionEncontrada(correoUsuario, nueva.latitude, nueva.longitude)
                        }
                        .addOnFailureListener { vm.sinUbicacion() }
                }
            }
            .addOnFailureListener { vm.sinUbicacion() }
    }

    val permiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) pedirUltimaUbicacion() else vm.permisoNegado()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            CabeceraPantalla(titulo = "Buscar dispositivo", onVolver = onVolver)

            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Última ubicación conocida",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.semantics { heading() }
                )
                Spacer(Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        if (estado.tieneUbicacion) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = null,
                                    tint = MentaTinta,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(Modifier.size(12.dp))
                                Text(
                                    text = formatearCoordenadas(estado.latitud!!, estado.longitud!!),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        } else {
                            Text(
                                text = "Aún no hay consultas de ubicación.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                if (estado.mensaje.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Column(modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }) {
                        TarjetaAviso(mensaje = estado.mensaje, esError = estado.permisoNegado)
                    }
                }

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        vm.iniciarBusqueda()
                        val concedido = ContextCompat.checkSelfPermission(
                            contexto, Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                        if (concedido) pedirUltimaUbicacion()
                        else permiso.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    },
                    enabled = !estado.buscando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (estado.buscando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Actualizar ubicación", style = MaterialTheme.typography.titleMedium)
                    }
                }

                if (estado.historial.isNotEmpty()) {
                    Spacer(Modifier.height(28.dp))
                    Text(
                        text = "Consultas anteriores",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.semantics { heading() }
                    )
                    Spacer(Modifier.height(8.dp))
                    estado.historial.forEach { registro ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = registro.texto,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = { vm.borrar(correoUsuario, registro.id) }) {
                                Text("Borrar")
                            }
                        }
                        HorizontalDivider()
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
