package cl.duoc.vozvisible.data.firebase

import cl.duoc.vozvisible.data.ModoComunicacion
import cl.duoc.vozvisible.data.NivelAudicion
import cl.duoc.vozvisible.data.RegistroActividad
import cl.duoc.vozvisible.data.TipoActividad
import cl.duoc.vozvisible.data.Usuario
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

private const val PERFILES = "perfiles"

// CRUD sobre Firestore: El perfil del usuario y los registros de cada pantalla.
class FirestoreService(private val db: FirebaseFirestore = Firebase.firestore) {

    suspend fun guardarPerfil(usuario: Usuario) {
        val datos = mapOf(
            "nombre" to usuario.nombre,
            "correo" to usuario.correo.lowercase(),
            "nivel" to usuario.nivel.name,
            "modo" to usuario.modo.name,
            "apoyos" to usuario.apoyos.toList()
        )
        db.collection(PERFILES).document(usuario.correo.lowercase()).set(datos).await()
    }

    suspend fun leerPerfil(correo: String): Usuario? {
        val documento = db.collection(PERFILES).document(correo.lowercase()).get().await()
        if (!documento.exists()) return null
        return Usuario(
            nombre = documento.getString("nombre").orEmpty(),
            correo = documento.getString("correo") ?: correo,
            clave = "",
            nivel = documento.enumOEsperado("nivel", NivelAudicion.LEVE),
            modo = documento.enumOEsperado("modo", ModoComunicacion.ESCRIBIR),
            apoyos = (documento.get("apoyos") as? List<*>)
                ?.mapNotNull { it as? String }?.toSet().orEmpty()
        )
    }

    suspend fun listarPerfiles(): List<Usuario> =
        db.collection(PERFILES).get().await().documents.mapNotNull { documento ->
            val correo = documento.getString("correo") ?: return@mapNotNull null
            Usuario(
                nombre = documento.getString("nombre").orEmpty(),
                correo = correo,
                clave = "",
                nivel = documento.enumOEsperado("nivel", NivelAudicion.LEVE),
                modo = documento.enumOEsperado("modo", ModoComunicacion.ESCRIBIR),
                apoyos = (documento.get("apoyos") as? List<*>)
                    ?.mapNotNull { it as? String }?.toSet().orEmpty()
            )
        }

    suspend fun borrarPerfil(correo: String) {
        db.collection(PERFILES).document(correo.lowercase()).delete().await()
    }

    suspend fun crearRegistro(registro: RegistroActividad): RegistroActividad {
        val referencia = db.collection(registro.tipo.coleccion)
            .add(registro.comoMapa()).await()
        return registro.copy(id = referencia.id)
    }

    suspend fun listarRegistros(correo: String, tipo: TipoActividad): List<RegistroActividad> =
        db.collection(tipo.coleccion)
            .whereEqualTo("correo", correo.lowercase())
            .get().await().documents
            .map { it.comoRegistro(tipo) }
            .sortedByDescending { it.fecha }

    suspend fun actualizarRegistro(registro: RegistroActividad) {
        db.collection(registro.tipo.coleccion).document(registro.id)
            .set(registro.comoMapa()).await()
    }

    suspend fun borrarRegistro(tipo: TipoActividad, id: String) {
        db.collection(tipo.coleccion).document(id).delete().await()
    }

    // Firestore no borra en cascada: Hay que recorrer cada coleccion y borrar
    // los documentos del usuario uno por uno antes de dar de baja la cuenta.
    suspend fun borrarActividadDe(correo: String) {
        TipoActividad.entries.forEach { tipo ->
            db.collection(tipo.coleccion)
                .whereEqualTo("correo", correo.lowercase())
                .get().await().documents
                .forEach { it.reference.delete().await() }
        }
    }
}

private fun RegistroActividad.comoMapa(): Map<String, Any?> = mapOf(
    "correo" to correo.lowercase(),
    "texto" to texto,
    "latitud" to latitud,
    "longitud" to longitud,
    "fecha" to fecha
)

private fun DocumentSnapshot.comoRegistro(tipo: TipoActividad) = RegistroActividad(
    id = id,
    correo = getString("correo").orEmpty(),
    tipo = tipo,
    texto = getString("texto").orEmpty(),
    latitud = getDouble("latitud"),
    longitud = getDouble("longitud"),
    fecha = getLong("fecha") ?: 0L
)

private inline fun <reified T : Enum<T>> DocumentSnapshot.enumOEsperado(campo: String, porDefecto: T): T =
    getString(campo)?.let { guardado -> enumValues<T>().find { it.name == guardado } } ?: porDefecto
