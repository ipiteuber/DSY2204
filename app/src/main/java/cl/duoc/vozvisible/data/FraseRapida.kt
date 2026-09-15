package cl.duoc.vozvisible.data

data class FraseRapida(
    val texto: String,
    val categoria: String
)

val frasesRapidas = arrayOf(
    FraseRapida("Hola, soy sordo. ¿Puede escribirme?", "Presentación"),
    FraseRapida("¿Me puede repetir más despacio?", "Presentación"),
    FraseRapida("Necesito ayuda, por favor.", "Urgencia"),
    FraseRapida("¿Dónde queda la salida?", "Orientación"),
    FraseRapida("Quiero pagar con tarjeta.", "Trámites"),
    FraseRapida("Tengo hora médica a las 10.", "Trámites")
)

val categoriasFrases: List<String> = frasesRapidas.map { it.categoria }.distinct().sorted()

fun frasesPorCategoria(): Map<String, List<FraseRapida>> = frasesRapidas.groupBy { it.categoria }

fun buscarFrases(texto: String): List<FraseRapida> {
    val consulta = texto.trim()
    if (consulta.isBlank()) return frasesRapidas.toList()
    return frasesRapidas.filter {
        it.texto.contains(consulta, ignoreCase = true) ||
                it.categoria.contains(consulta, ignoreCase = true)
    }
}
