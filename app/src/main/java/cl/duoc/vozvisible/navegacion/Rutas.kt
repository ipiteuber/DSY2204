package cl.duoc.vozvisible.navegacion

import kotlinx.serialization.Serializable

// Rutas con tipo en vez de strings. Navigation 2.8 las serializa solo,
// asi que un error de destino o de argumento lo caza el compilador.
@Serializable
object RutaLogin

@Serializable
object RutaRegistro

@Serializable
object RutaRecuperar

@Serializable
data class RutaInicio(val nombre: String)
