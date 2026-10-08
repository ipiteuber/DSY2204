package cl.duoc.vozvisible.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.vozvisible.data.RepositorioCuentas
import cl.duoc.vozvisible.data.Repositorios
import cl.duoc.vozvisible.data.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EstadoLogin(
    val correo: String = "",
    val clave: String = "",
    val recordarme: Boolean = false,
    val cargando: Boolean = false,
    val error: String = "",
    val autenticado: Usuario? = null
) {
    val puedeIngresar: Boolean get() = correo.isNotBlank() && clave.isNotBlank() && !cargando
}

class LoginViewModel(
    private val cuentas: RepositorioCuentas = Repositorios.cuentas
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoLogin())
    val estado: StateFlow<EstadoLogin> = _estado.asStateFlow()

    fun cambiarCorreo(valor: String) = _estado.update { it.copy(correo = valor, error = "") }

    fun cambiarClave(valor: String) = _estado.update { it.copy(clave = valor, error = "") }

    fun cambiarRecordarme(valor: Boolean) = _estado.update { it.copy(recordarme = valor) }

    fun ingresar() {
        val actual = _estado.value
        if (!actual.puedeIngresar) return
        _estado.update { it.copy(cargando = true, error = "") }
        viewModelScope.launch {
            val usuario = cuentas.iniciarSesion(actual.correo, actual.clave)
            _estado.update {
                if (usuario == null) {
                    it.copy(cargando = false, error = "El correo o la contraseña no corresponden a una cuenta registrada.")
                } else {
                    it.copy(cargando = false, autenticado = usuario)
                }
            }
        }
    }

    fun limpiarAutenticado() = _estado.update { it.copy(autenticado = null) }
}
