package cl.duoc.vozvisible.data

import androidx.compose.runtime.mutableStateListOf

data class Usuario(
    val nombre: String,
    val correo: String,
    val clave: String,
    val modoComunicacion: String
)

// Cuentas precargadas para poder probar el inicio de sesion sin registrar a nadie
val usuariosIniciales = arrayOf(
    Usuario("Camila Rojas", "camila.rojas@correo.cl", "Camila#2026", "Escribir"),
    Usuario("Matías Fuentes", "matias.fuentes@correo.cl", "Matias#2026", "Hablar"),
    Usuario("Valentina Soto", "valentina.soto@correo.cl", "Valen#2026", "Ambos"),
    Usuario("Ignacio Bravo", "ignacio.bravo@correo.cl", "Ignacio#2026", "Escribir"),
    Usuario("Josefa Miranda", "josefa.miranda@correo.cl", "Josefa#2026", "Ambos")
)

object RepositorioUsuarios {

    private val usuarios = mutableStateListOf(*usuariosIniciales)

    val listado: List<Usuario> get() = usuarios

    fun registrar(usuario: Usuario): String? {
        if (buscarPorCorreo(usuario.correo) != null) {
            return "Ese correo ya está registrado."
        }
        usuarios.add(usuario)
        return null
    }

    fun validar(correo: String, clave: String): Usuario? {
        val encontrado = buscarPorCorreo(correo)
        return if (encontrado != null && encontrado.clave == clave) encontrado else null
    }

    fun buscarPorCorreo(correo: String): Usuario? =
        usuarios.find { it.correo.equals(correo.trim(), ignoreCase = true) }
}
