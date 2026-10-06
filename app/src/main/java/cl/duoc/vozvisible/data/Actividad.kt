package cl.duoc.vozvisible.data

enum class TipoActividad(val coleccion: String, val etiqueta: String) {
    ESCRITO("mensajes_escritos", "Mensaje escrito"),
    HABLADO("frases_habladas", "Frase reproducida"),
    UBICACION("ubicaciones", "Ubicación consultada")
}

data class RegistroActividad(
    val id: String = "",
    val correo: String,
    val tipo: TipoActividad,
    val texto: String,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val fecha: Long = System.currentTimeMillis()
)
