package cl.duoc.vozvisible.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.vozvisible.data.RegistroActividad
import cl.duoc.vozvisible.data.RepositorioActividad
import cl.duoc.vozvisible.data.Repositorios
import cl.duoc.vozvisible.data.TipoActividad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EstadoHablar(
    val texto: String = "",
    val motorListo: Boolean = false,
    val aviso: String = "",
    val historial: List<RegistroActividad> = emptyList()
) {
    val puedeHablar: Boolean get() = motorListo && texto.isNotBlank()
}

class HablarViewModel(
    private val actividad: RepositorioActividad = Repositorios.actividad
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoHablar())
    val estado: StateFlow<EstadoHablar> = _estado.asStateFlow()

    fun cargar(correo: String) {
        viewModelScope.launch {
            val lista = actividad.listar(correo, TipoActividad.HABLADO)
            _estado.update { it.copy(historial = lista) }
        }
    }

    fun cambiarTexto(valor: String) = _estado.update { it.copy(texto = valor, aviso = "") }

    fun motorListo(listo: Boolean) = _estado.update {
        it.copy(
            motorListo = listo,
            aviso = if (listo) it.aviso else "Este dispositivo no tiene voz en español instalada."
        )
    }

    fun registrarReproduccion(correo: String, texto: String) {
        if (texto.isBlank()) return
        viewModelScope.launch {
            actividad.crear(
                RegistroActividad(correo = correo, tipo = TipoActividad.HABLADO, texto = texto)
            )
            val lista = actividad.listar(correo, TipoActividad.HABLADO)
            _estado.update { it.copy(historial = lista, aviso = "Frase reproducida y guardada.") }
        }
    }

    fun borrar(correo: String, id: String) {
        viewModelScope.launch {
            actividad.borrar(TipoActividad.HABLADO, id)
            val lista = actividad.listar(correo, TipoActividad.HABLADO)
            _estado.update { it.copy(historial = lista, aviso = "Frase eliminada.") }
        }
    }
}
