package cl.duoc.vozvisible.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.vozvisible.data.RepositorioCuentas
import cl.duoc.vozvisible.data.Repositorios
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EstadoRecuperar(
    val correo: String = "",
    val metodo: String = "Enviar al correo electrónico",
    val cargando: Boolean = false,
    val mensaje: String = "",
    val esError: Boolean = false
)

class RecuperarViewModel(
    private val cuentas: RepositorioCuentas = Repositorios.cuentas
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoRecuperar())
    val estado: StateFlow<EstadoRecuperar> = _estado.asStateFlow()

    fun cambiarCorreo(valor: String) = _estado.update { it.copy(correo = valor, mensaje = "") }

    fun cambiarMetodo(valor: String) = _estado.update { it.copy(metodo = valor) }

    fun enviar() {
        val correo = _estado.value.correo
        if (correo.isBlank()) return
        _estado.update { it.copy(cargando = true, mensaje = "") }
        viewModelScope.launch {
            val enviado = cuentas.enviarRecuperacion(correo)
            _estado.update {
                it.copy(
                    cargando = false,
                    esError = !enviado,
                    mensaje = if (enviado) {
                        "Listo. Revisa tu bandeja de entrada en unos minutos."
                    } else {
                        "No encontramos una cuenta con ese correo."
                    }
                )
            }
        }
    }
}
