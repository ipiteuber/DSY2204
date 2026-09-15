package cl.duoc.vozvisible.data

import androidx.compose.runtime.mutableStateListOf

enum class NivelAudicion(val etiqueta: String) {
    LEVE("Hipoacusia leve"),
    MODERADA("Hipoacusia moderada"),
    SEVERA("Hipoacusia severa"),
    TOTAL("Sordera total")
}

enum class ModoComunicacion(val etiqueta: String) {
    ESCRIBIR("Escribir"),
    HABLAR("Hablar"),
    AMBOS("Ambos")
}

data class Usuario(
    val nombre: String,
    val correo: String,
    val clave: String,
    val nivel: NivelAudicion,
    val modo: ModoComunicacion,
    val apoyos: Set<String> = emptySet()
)

sealed interface ResultadoRegistro {
    data class Exito(val usuario: Usuario) : ResultadoRegistro
    data class Error(val mensaje: String) : ResultadoRegistro
}

data class EstadisticasUsuarios(
    val nombresPorModo: Map<ModoComunicacion, List<String>>,
    val totalPorNivel: Map<NivelAudicion, Int>,
    val conApoyos: List<String>
)

val apoyosDisponibles = listOf(
    "Avisos con vibración",
    "Subtítulos en pantalla completa",
    "Frases rápidas guardadas"
)

// Cuentas precargadas para poder probar el inicio de sesion sin registrar a nadie
val usuariosIniciales = arrayOf(
    Usuario(
        "Camila Rojas", "camila.rojas@correo.cl", "Camila#2026",
        NivelAudicion.TOTAL, ModoComunicacion.ESCRIBIR,
        setOf("Avisos con vibración", "Frases rápidas guardadas")
    ),
    Usuario(
        "Matías Fuentes", "matias.fuentes@correo.cl", "Matias#2026",
        NivelAudicion.MODERADA, ModoComunicacion.HABLAR,
        setOf("Subtítulos en pantalla completa")
    ),
    Usuario(
        "Valentina Soto", "valentina.soto@correo.cl", "Valen#2026",
        NivelAudicion.SEVERA, ModoComunicacion.AMBOS,
        setOf("Avisos con vibración")
    ),
    Usuario(
        "Ignacio Bravo", "ignacio.bravo@correo.cl", "Ignacio#2026",
        NivelAudicion.LEVE, ModoComunicacion.ESCRIBIR
    ),
    Usuario(
        "Josefa Miranda", "josefa.miranda@correo.cl", "Josefa#2026",
        NivelAudicion.TOTAL, ModoComunicacion.AMBOS,
        setOf("Avisos con vibración", "Frases rápidas guardadas")
    )
)

object RepositorioUsuarios {

    private val usuarios = mutableStateListOf(*usuariosIniciales)

    val listado: List<Usuario> get() = usuarios

    fun registrar(usuario: Usuario): ResultadoRegistro = when {
        !usuario.correo.esCorreoValido() ->
            ResultadoRegistro.Error("Escribe un correo válido, por ejemplo nombre@correo.cl.")

        buscarPorCorreo(usuario.correo) != null ->
            ResultadoRegistro.Error("Ese correo ya está registrado.")

        else -> ResultadoRegistro.Exito(usuario).also { usuarios.add(usuario) }
    }

    fun validar(correo: String, clave: String): Usuario? {
        val encontrado = buscarPorCorreo(correo)
        return if (encontrado != null && encontrado.clave == clave) encontrado else null
    }

    fun buscarPorCorreo(correo: String): Usuario? =
        usuarios.find { it.correo.equals(correo.trim(), ignoreCase = true) }

    fun estadisticas(): EstadisticasUsuarios = usuarios.toList().let { lista ->
        EstadisticasUsuarios(
            nombresPorModo = lista.groupBy { it.modo }
                .mapValues { (_, grupo) -> grupo.map { usuario -> usuario.nombre }.sorted() },
            totalPorNivel = lista.groupingBy { it.nivel }.eachCount(),
            conApoyos = lista.filter { it.apoyos.isNotEmpty() }
                .sortedBy { it.nombre }
                .map { it.nombre }
        )
    }
}
