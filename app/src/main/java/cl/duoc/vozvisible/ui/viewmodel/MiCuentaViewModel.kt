package cl.duoc.vozvisible.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.vozvisible.data.RepositorioCuentas
import cl.duoc.vozvisible.data.Repositorios
import cl.duoc.vozvisible.data.ResultadoEliminacion
import cl.duoc.vozvisible.data.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EstadoMiCuenta(
    val perfil: Usuario? = null,
    val clave: String = "",
    val confirmando: Boolean = false,
    val procesando: Boolean = false,
    val mensaje: String = "",
    val esError: Boolean = false,
    val cuentaEliminada: Boolean = false
)

class MiCuentaViewModel(
    private val cuentas: RepositorioCuentas = Repositorios.cuentas
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoMiCuenta())
    val estado: StateFlow<EstadoMiCuenta> = _estado.asStateFlow()

    fun cargar(correo: String) {
        viewModelScope.launch {
            _estado.update { it.copy(perfil = cuentas.perfil(correo)) }
        }
    }

    fun cambiarClave(valor: String) = _estado.update { it.copy(clave = valor, mensaje = "") }

    fun pedirConfirmacion() = _estado.update { it.copy(confirmando = true, mensaje = "") }

    fun cancelarConfirmacion() =
        _estado.update { it.copy(confirmando = false, clave = "", mensaje = "") }

    fun eliminar(correo: String) {
        val clave = _estado.value.clave
        if (clave.isBlank()) {
            _estado.update {
                it.copy(mensaje = "Escribe tu contraseña para confirmar.", esError = true)
            }
            return
        }
        _estado.update { it.copy(procesando = true, mensaje = "") }
        viewModelScope.launch {
            when (val resultado = cuentas.eliminarCuenta(correo, clave)) {
                is ResultadoEliminacion.Exito -> {
                    cuentas.cerrarSesion()
                    _estado.update {
                        it.copy(procesando = false, confirmando = false, cuentaEliminada = true)
                    }
                }

                is ResultadoEliminacion.Error -> _estado.update {
                    it.copy(procesando = false, esError = true, mensaje = resultado.mensaje)
                }
            }
        }
    }
}
