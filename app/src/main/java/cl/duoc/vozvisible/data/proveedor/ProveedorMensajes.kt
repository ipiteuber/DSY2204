package cl.duoc.vozvisible.data.proveedor

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import cl.duoc.vozvisible.data.RegistroActividad
import cl.duoc.vozvisible.data.Repositorios
import cl.duoc.vozvisible.data.TipoActividad
import kotlinx.coroutines.runBlocking

// Expone los registros guardados a otras aplicaciones del dispositivo mediante el
// mecanismo estandar de Android. El acceso queda restringido por un permiso de
// nivel de firma declarado en el manifiesto.
class ProveedorMensajes : ContentProvider() {

    companion object {
        const val AUTORIDAD = "cl.duoc.vozvisible.proveedor"

        const val COLUMNA_ID = "_id"
        const val COLUMNA_CORREO = "correo"
        const val COLUMNA_TIPO = "tipo"
        const val COLUMNA_TEXTO = "texto"
        const val COLUMNA_FECHA = "fecha"

        private val COLUMNAS = arrayOf(
            COLUMNA_ID, COLUMNA_CORREO, COLUMNA_TIPO, COLUMNA_TEXTO, COLUMNA_FECHA
        )

        private const val RUTA_MENSAJES = 1
        private const val RUTA_MENSAJE_UNICO = 2

        val URI_MENSAJES: Uri = Uri.parse("content://$AUTORIDAD/mensajes")
    }

    private val rutas = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(AUTORIDAD, "mensajes", RUTA_MENSAJES)
        addURI(AUTORIDAD, "mensajes/*", RUTA_MENSAJE_UNICO)
    }

    override fun onCreate(): Boolean = true

    // selection se usa para el correo del usuario y selectionArgs para el tipo de registro,
    // de modo que la consulta no exponga los datos de otras cuentas.
    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        val cursor = MatrixCursor(projection ?: COLUMNAS)
        val correo = selection ?: Repositorios.cuentas.correoActual() ?: return cursor
        val tipo = tipoDesde(selectionArgs?.firstOrNull())

        val registros = runBlocking {
            when (rutas.match(uri)) {
                RUTA_MENSAJES -> Repositorios.actividad.listar(correo, tipo)
                RUTA_MENSAJE_UNICO -> Repositorios.actividad.listar(correo, tipo)
                    .filter { it.id == uri.lastPathSegment }
                else -> emptyList()
            }
        }
        registros.forEach { cursor.addRow(filaDe(it)) }
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val datos = values ?: return null
        val correo = datos.getAsString(COLUMNA_CORREO) ?: return null
        val texto = datos.getAsString(COLUMNA_TEXTO) ?: return null
        val tipo = tipoDesde(datos.getAsString(COLUMNA_TIPO))

        val creado = runBlocking {
            Repositorios.actividad.crear(
                RegistroActividad(correo = correo, tipo = tipo, texto = texto)
            )
        }
        context?.contentResolver?.notifyChange(URI_MENSAJES, null)
        return Uri.withAppendedPath(URI_MENSAJES, creado.id)
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        val datos = values ?: return 0
        val id = uri.lastPathSegment ?: return 0
        val correo = datos.getAsString(COLUMNA_CORREO) ?: return 0
        val texto = datos.getAsString(COLUMNA_TEXTO) ?: return 0
        val tipo = tipoDesde(datos.getAsString(COLUMNA_TIPO))

        runBlocking {
            Repositorios.actividad.actualizar(
                RegistroActividad(id = id, correo = correo, tipo = tipo, texto = texto)
            )
        }
        context?.contentResolver?.notifyChange(URI_MENSAJES, null)
        return 1
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val id = uri.lastPathSegment ?: return 0
        val tipo = tipoDesde(selectionArgs?.firstOrNull())

        runBlocking { Repositorios.actividad.borrar(tipo, id) }
        context?.contentResolver?.notifyChange(URI_MENSAJES, null)
        return 1
    }

    override fun getType(uri: Uri): String = when (rutas.match(uri)) {
        RUTA_MENSAJES -> "vnd.android.cursor.dir/vnd.$AUTORIDAD.mensajes"
        else -> "vnd.android.cursor.item/vnd.$AUTORIDAD.mensajes"
    }

    private fun filaDe(registro: RegistroActividad): Array<Any> = arrayOf(
        registro.id,
        registro.correo,
        registro.tipo.name,
        registro.texto,
        registro.fecha
    )

    // Ante un tipo ausente o desconocido se asume el de los mensajes escritos
    private fun tipoDesde(valor: String?): TipoActividad =
        TipoActividad.entries.firstOrNull { it.name == valor } ?: TipoActividad.ESCRITO
}
