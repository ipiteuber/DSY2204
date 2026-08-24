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
