package cl.duoc.vozvisible.data

import android.content.Context
import android.util.Log
import cl.duoc.vozvisible.data.firebase.ActividadFirestore
import cl.duoc.vozvisible.data.firebase.AutenticacionService
import cl.duoc.vozvisible.data.firebase.CuentasFirebase
import cl.duoc.vozvisible.data.firebase.FirestoreService
import cl.duoc.vozvisible.data.memoria.ActividadEnMemoria
import cl.duoc.vozvisible.data.memoria.CuentasEnMemoria
import com.google.firebase.FirebaseApp

interface RepositorioCuentas {
    suspend fun registrar(usuario: Usuario): ResultadoRegistro
    suspend fun iniciarSesion(correo: String, clave: String): Usuario?
    suspend fun enviarRecuperacion(correo: String): Boolean
    suspend fun perfil(correo: String): Usuario?
    suspend fun guardarPerfil(usuario: Usuario)
    suspend fun listado(): List<Usuario>
    suspend fun eliminarCuenta(correo: String, clave: String): ResultadoEliminacion
    fun cerrarSesion()
    fun correoActual(): String?
}

interface RepositorioActividad {
    suspend fun crear(registro: RegistroActividad): RegistroActividad
    suspend fun listar(correo: String, tipo: TipoActividad): List<RegistroActividad>
    suspend fun actualizar(registro: RegistroActividad)
    suspend fun borrar(tipo: TipoActividad, id: String)
    suspend fun borrarTodo(correo: String)
}

// Elige la implementacion al arrancar: Firebase si hay credenciales de verdad,
// memoria si el google-services.json sigue siendo el de reemplazo.
object Repositorios {

    private const val PROYECTO_DEMO = "vozvisible-demo"
    private const val ETIQUETA = "VozVisible"

    private var cuentasActual: RepositorioCuentas = CuentasEnMemoria
    private var actividadActual: RepositorioActividad = ActividadEnMemoria

    var usaFirebase: Boolean = false
        private set

    val cuentas: RepositorioCuentas get() = cuentasActual
    val actividad: RepositorioActividad get() = actividadActual

    fun inicializar(contexto: Context) {
        val proyecto = proyectoActivo(contexto)
        if (proyecto == null || proyecto == PROYECTO_DEMO) {
            Log.i(ETIQUETA, "Firebase sin configurar (proyecto: $proyecto). Se usan los repositorios en memoria.")
            return
        }
        runCatching {
            val firestore = FirestoreService()
            cuentasActual = CuentasFirebase(AutenticacionService(), firestore)
            actividadActual = ActividadFirestore(firestore)
            usaFirebase = true
        }.onSuccess {
            Log.i(ETIQUETA, "Firebase activo en el proyecto $proyecto.")
        }.onFailure {
            Log.e(ETIQUETA, "No se pudo inicializar Firebase, se continua en memoria.", it)
        }
    }

    // initializeApp devuelve null cuando Firebase ya fue inicializado por su propio
    // ContentProvider al arrancar, asi que hay que leer la instancia ya creada.
    private fun proyectoActivo(contexto: Context): String? =
        FirebaseApp.getApps(contexto).firstOrNull()?.options?.projectId
}
