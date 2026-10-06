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

data class EstadoEscribir(
    val texto: String = "",
    val enPantallaGrande: Boolean = false,
    val guardando: Boolean = false,
    val guardados: List<RegistroActividad> = emptyList(),
    val aviso: String = ""
)

class EscribirViewModel(
    private val actividad: RepositorioActividad = Repositorios.actividad
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoEscribir())
    val estado: StateFlow<EstadoEscribir> = _estado.asStateFlow()

    fun cargar(correo: String) {
        viewModelScope.launch {
            val lista = actividad.listar(correo, TipoActividad.ESCRITO)
            _estado.update { it.copy(guardados = lista) }
        }
    }

    fun cambiarTexto(valor: String) = _estado.update { it.copy(texto = valor, aviso = "") }

    fun confirmar(correo: String) {
        val texto = _estado.value.texto.trim()
        if (texto.isEmpty()) return
        _estado.update { it.copy(enPantallaGrande = true, guardando = true) }
        viewModelScope.launch {
            actividad.crear(
                RegistroActividad(correo = correo, tipo = TipoActividad.ESCRITO, texto = texto)
            )
            val lista = actividad.listar(correo, TipoActividad.ESCRITO)
            _estado.update {
                it.copy(guardando = false, guardados = lista, aviso = "Mensaje guardado.")
            }
        }
    }

    fun volverDesdePantallaGrande() = _estado.update { it.copy(enPantallaGrande = false) }

    fun borrar(correo: String, id: String) {
        viewModelScope.launch {
            actividad.borrar(TipoActividad.ESCRITO, id)
            val lista = actividad.listar(correo, TipoActividad.ESCRITO)
            _estado.update { it.copy(guardados = lista, aviso = "Mensaje eliminado.") }
        }
    }
}
