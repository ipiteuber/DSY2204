package cl.duoc.vozvisible.data.firebase

import cl.duoc.vozvisible.data.RegistroActividad
import cl.duoc.vozvisible.data.RepositorioActividad
import cl.duoc.vozvisible.data.TipoActividad

class ActividadFirestore(private val firestore: FirestoreService) : RepositorioActividad {

    override suspend fun crear(registro: RegistroActividad): RegistroActividad =
        runCatching { firestore.crearRegistro(registro) }.getOrDefault(registro)

    override suspend fun listar(correo: String, tipo: TipoActividad): List<RegistroActividad> =
        runCatching { firestore.listarRegistros(correo, tipo) }.getOrDefault(emptyList())

    override suspend fun actualizar(registro: RegistroActividad) {
        runCatching { firestore.actualizarRegistro(registro) }
    }

    override suspend fun borrar(tipo: TipoActividad, id: String) {
        runCatching { firestore.borrarRegistro(tipo, id) }
    }

    override suspend fun borrarTodo(correo: String) {
        runCatching { firestore.borrarActividadDe(correo) }
    }
}
