package cl.duoc.vozvisible.data

private val formatoCorreo = Regex("^[\\w.+-]+@[\\w-]+\\.[a-z]{2,}$", RegexOption.IGNORE_CASE)

fun String.esCorreoValido(): Boolean = formatoCorreo.matches(trim())

fun Usuario.resumenPerfil(): String {
    val apoyo = when (apoyos.size) {
        0 -> "sin apoyos activos"
        1 -> "1 apoyo activo"
        else -> "${apoyos.size} apoyos activos"
    }
    return "${nivel.etiqueta} · prefiere ${modo.etiqueta.lowercase()} · $apoyo"
}

// Deja el filtro fuera cuando la condicion no se cumple, para encadenar sin ifs
fun <T> List<T>.filtrarSi(condicion: Boolean, predicado: (T) -> Boolean): List<T> =
    if (condicion) filter(predicado) else this
