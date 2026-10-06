package cl.duoc.vozvisible.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val formatoCorreo = Regex("^[\\w.+-]+@[\\w-]+\\.[a-z]{2,}$", RegexOption.IGNORE_CASE)

private val formatoHora = SimpleDateFormat("dd/MM HH:mm", Locale("es", "CL"))

fun String.esCorreoValido(): Boolean = formatoCorreo.matches(trim())

fun String.esClaveSegura(): Boolean =
    length >= 8 && any { it.isDigit() } && any { it.isLetter() }

fun validarClaves(clave: String, repetida: String): String? = when {
    clave.isBlank() -> "Escribe una contraseña."
    !clave.esClaveSegura() -> "La contraseña necesita 8 caracteres, con letras y números."
    clave != repetida -> "Las dos contraseñas no son iguales."
    else -> null
}

fun Usuario.resumenPerfil(): String {
    val apoyo = when (apoyos.size) {
        0 -> "sin apoyos activos"
        1 -> "1 apoyo activo"
        else -> "${apoyos.size} apoyos activos"
    }
    return "${nivel.etiqueta} · prefiere ${modo.etiqueta.lowercase()} · $apoyo"
}

fun formatearCoordenadas(latitud: Double, longitud: Double): String =
    String.format(Locale.US, "%.5f, %.5f", latitud, longitud)

fun Long.comoFechaCorta(): String = formatoHora.format(Date(this))

fun RegistroActividad.resumen(): String = when (tipo) {
    TipoActividad.UBICACION -> "${fecha.comoFechaCorta()} · $texto"
    else -> "${fecha.comoFechaCorta()} · ${texto.take(60)}"
}

fun List<Usuario>.estadisticas(): EstadisticasUsuarios = EstadisticasUsuarios(
    nombresPorModo = groupBy { it.modo }
        .mapValues { (_, grupo) -> grupo.map { usuario -> usuario.nombre }.sorted() },
    totalPorNivel = groupingBy { it.nivel }.eachCount(),
    conApoyos = filter { it.apoyos.isNotEmpty() }.sortedBy { it.nombre }.map { it.nombre }
)

// Deja el filtro fuera cuando la condicion no se cumple, para encadenar sin ifs
fun <T> List<T>.filtrarSi(condicion: Boolean, predicado: (T) -> Boolean): List<T> =
    if (condicion) filter(predicado) else this
