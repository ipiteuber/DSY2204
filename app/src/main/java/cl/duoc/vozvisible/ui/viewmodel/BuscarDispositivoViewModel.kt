package cl.duoc.vozvisible.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.vozvisible.data.RegistroActividad
import cl.duoc.vozvisible.data.RepositorioActividad
import cl.duoc.vozvisible.data.Repositorios
import cl.duoc.vozvisible.data.TipoActividad
import cl.duoc.vozvisible.data.formatearCoordenadas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EstadoBuscarDispositivo(
    val buscando: Boolean = false,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val mensaje: String = "",
    val permisoNegado: Boolean = false,
    val historial: List<RegistroActividad> = emptyList()
) {
    val tieneUbicacion: Boolean get() = latitud != null && longitud != null
}

class BuscarDispositivoViewModel(
    private val actividad: RepositorioActividad = Repositorios.actividad
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoBuscarDispositivo())
    val estado: StateFlow<EstadoBuscarDispositivo> = _estado.asStateFlow()

    fun cargar(correo: String) {
        viewModelScope.launch {
            val lista = actividad.listar(correo, TipoActividad.UBICACION)
            _estado.update { it.copy(historial = lista) }
        }
    }

    fun iniciarBusqueda() = _estado.update {
        it.copy(buscando = true, mensaje = "", permisoNegado = false)
    }

    fun permisoNegado() = _estado.update {
        it.copy(
            buscando = false,
            permisoNegado = true,
            mensaje = "Sin el permiso de ubicación no podemos mostrar dónde está el dispositivo. " +
                    "Puedes activarlo en los ajustes del teléfono."
        )
    }

    fun sinUbicacion() = _estado.update {
        it.copy(
            buscando = false,
            mensaje = "Todavía no hay una ubicación guardada. Activa el GPS y vuelve a intentarlo."
        )
    }

    fun ubicacionEncontrada(correo: String, latitud: Double, longitud: Double) {
        val texto = formatearCoordenadas(latitud, longitud)
        _estado.update {
            it.copy(buscando = false, latitud = latitud, longitud = longitud, mensaje = "")
        }
        viewModelScope.launch {
            actividad.crear(
                RegistroActividad(
                    correo = correo,
                    tipo = TipoActividad.UBICACION,
                    texto = texto,
                    latitud = latitud,
                    longitud = longitud
                )
            )
            val lista = actividad.listar(correo, TipoActividad.UBICACION)
            _estado.update { it.copy(historial = lista) }
        }
    }

    fun borrar(correo: String, id: String) {
        viewModelScope.launch {
            actividad.borrar(TipoActividad.UBICACION, id)
            val lista = actividad.listar(correo, TipoActividad.UBICACION)
            _estado.update { it.copy(historial = lista) }
        }
    }
}
