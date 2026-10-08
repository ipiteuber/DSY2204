package cl.duoc.vozvisible.data.memoria

import cl.duoc.vozvisible.data.RegistroActividad
import cl.duoc.vozvisible.data.RepositorioActividad
import cl.duoc.vozvisible.data.TipoActividad

object ActividadEnMemoria : RepositorioActividad {

    private val registros = mutableListOf<RegistroActividad>()
    private var siguienteId = 1

    override suspend fun crear(registro: RegistroActividad): RegistroActividad {
        val guardado = registro.copy(id = "mem-${siguienteId++}")
        registros.add(guardado)
        return guardado
    }

    override suspend fun listar(correo: String, tipo: TipoActividad): List<RegistroActividad> =
        registros.filter { it.tipo == tipo && it.correo.equals(correo, ignoreCase = true) }
            .sortedByDescending { it.fecha }

    override suspend fun actualizar(registro: RegistroActividad) {
        val indice = registros.indexOfFirst { it.id == registro.id }
        if (indice >= 0) registros[indice] = registro
    }

    override suspend fun borrar(tipo: TipoActividad, id: String) {
        registros.removeAll { it.tipo == tipo && it.id == id }
    }

    override suspend fun borrarTodo(correo: String) {
        registros.removeAll { it.correo.equals(correo, ignoreCase = true) }
    }

    fun reiniciar() {
        registros.clear()
        siguienteId = 1
    }
}
