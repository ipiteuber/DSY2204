package cl.duoc.vozvisible.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.vozvisible.data.ModoComunicacion
import cl.duoc.vozvisible.data.NivelAudicion
import cl.duoc.vozvisible.data.RepositorioCuentas
import cl.duoc.vozvisible.data.Repositorios
import cl.duoc.vozvisible.data.ResultadoRegistro
import cl.duoc.vozvisible.data.Usuario
import cl.duoc.vozvisible.data.esCorreoValido
import cl.duoc.vozvisible.data.validarClaves
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EstadoRegistro(
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val claveRepetida: String = "",
    val nivel: NivelAudicion = NivelAudicion.LEVE,
    val modo: ModoComunicacion = ModoComunicacion.ESCRIBIR,
    val apoyos: Set<String> = emptySet(),
    val aceptaTerminos: Boolean = false,
    val cargando: Boolean = false,
    val error: String = "",
    val registrado: Usuario? = null,
) {
    val correoInvalido: Boolean get() = correo.isNotBlank() && !correo.esCorreoValido()
    val errorClave: String? get() = if (claveRepetida.isEmpty()) null else validarClaves(clave, claveRepetida)
    val formularioValido: Boolean
        get() = nombre.isNotBlank() && correo.esCorreoValido() &&
                validarClaves(clave, claveRepetida) == null && aceptaTerminos && !cargando
}

class RegistroViewModel(
    private val repositorio: RepositorioCuentas = Repositorios.cuentas
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoRegistro())
    val estado: StateFlow<EstadoRegistro> = _estado.asStateFlow()

    fun cambiarNombre(valor: String) = _estado.update { it.copy(nombre = valor) }

    fun cambiarCorreo(valor: String) = _estado.update { it.copy(correo = valor, error = "") }

    fun cambiarClave(valor: String) = _estado.update { it.copy(clave = valor) }

    fun cambiarClaveRepetida(valor: String) = _estado.update { it.copy(claveRepetida = valor) }

    fun cambiarNivel(valor: NivelAudicion) = _estado.update { it.copy(nivel = valor) }

    fun cambiarModo(valor: ModoComunicacion) = _estado.update { it.copy(modo = valor) }

    fun cambiarTerminos(valor: Boolean) = _estado.update { it.copy(aceptaTerminos = valor) }

    fun alternarApoyo(apoyo: String) = _estado.update { actual ->
        val nuevos = if (apoyo in actual.apoyos) actual.apoyos - apoyo else actual.apoyos + apoyo
        actual.copy(apoyos = nuevos)
    }

    fun registrar() {
        val actual = _estado.value
        if (!actual.formularioValido) return
        _estado.update { it.copy(cargando = true, error = "") }
        viewModelScope.launch {
            val usuario = Usuario(
                nombre = actual.nombre.trim(),
                correo = actual.correo.trim(),
                clave = actual.clave,
                nivel = actual.nivel,
                modo = actual.modo,
                apoyos = actual.apoyos
            )
            when (val resultado = repositorio.registrar(usuario)) {
                is ResultadoRegistro.Exito ->
                    _estado.update { it.copy(cargando = false, registrado = resultado.usuario) }

                is ResultadoRegistro.Error ->
                    _estado.update { it.copy(cargando = false, error = resultado.mensaje) }
            }
        }
    }

    fun limpiarRegistrado() = _estado.update { it.copy(registrado = null) }
}
