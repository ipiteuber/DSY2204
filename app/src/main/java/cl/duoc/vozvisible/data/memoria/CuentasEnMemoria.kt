package cl.duoc.vozvisible.data.memoria

import cl.duoc.vozvisible.data.RepositorioCuentas
import cl.duoc.vozvisible.data.ResultadoRegistro
import cl.duoc.vozvisible.data.Usuario
import cl.duoc.vozvisible.data.esCorreoValido
import cl.duoc.vozvisible.data.usuariosIniciales

// Respaldo sin red: Permite probar la app completa sin credenciales de Firebase.
object CuentasEnMemoria : RepositorioCuentas {

    private val usuarios = mutableListOf(*usuariosIniciales)
    private var sesion: String? = null

    override suspend fun registrar(usuario: Usuario): ResultadoRegistro = when {
        !usuario.correo.esCorreoValido() ->
            ResultadoRegistro.Error("Escribe un correo válido, por ejemplo nombre@correo.cl.")

        buscar(usuario.correo) != null ->
            ResultadoRegistro.Error("Ese correo ya está registrado.")

        else -> {
            usuarios.add(usuario)
            sesion = usuario.correo
            ResultadoRegistro.Exito(usuario)
        }
    }

    override suspend fun iniciarSesion(correo: String, clave: String): Usuario? {
        val encontrado = buscar(correo)?.takeIf { it.clave == clave }
        sesion = encontrado?.correo
        return encontrado
    }

    override suspend fun enviarRecuperacion(correo: String): Boolean = buscar(correo) != null

    override suspend fun perfil(correo: String): Usuario? = buscar(correo)

    override suspend fun guardarPerfil(usuario: Usuario) {
        val indice = usuarios.indexOfFirst { it.correo.equals(usuario.correo, ignoreCase = true) }
        if (indice >= 0) usuarios[indice] = usuario else usuarios.add(usuario)
    }

    override suspend fun listado(): List<Usuario> = usuarios.toList()

    override fun cerrarSesion() {
        sesion = null
    }

    override fun correoActual(): String? = sesion

    fun reiniciar() {
        usuarios.clear()
        usuarios.addAll(usuariosIniciales)
        sesion = null
    }

    private fun buscar(correo: String): Usuario? =
        usuarios.find { it.correo.equals(correo.trim(), ignoreCase = true) }
}
