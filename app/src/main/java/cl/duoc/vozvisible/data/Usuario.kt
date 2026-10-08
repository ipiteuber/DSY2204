package cl.duoc.vozvisible.data

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

sealed interface ResultadoEliminacion {
    data object Exito : ResultadoEliminacion
    data class Error(val mensaje: String) : ResultadoEliminacion
}

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
