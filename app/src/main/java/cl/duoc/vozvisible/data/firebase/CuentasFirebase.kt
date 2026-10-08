package cl.duoc.vozvisible.data.firebase

import cl.duoc.vozvisible.data.ModoComunicacion
import cl.duoc.vozvisible.data.NivelAudicion
import cl.duoc.vozvisible.data.RepositorioCuentas
import cl.duoc.vozvisible.data.ResultadoEliminacion
import cl.duoc.vozvisible.data.ResultadoRegistro
import cl.duoc.vozvisible.data.Usuario
import cl.duoc.vozvisible.data.esCorreoValido

// Authentication guarda la credencial y Firestore el resto del perfil.
class CuentasFirebase(
    private val autenticacion: AutenticacionService,
    private val firestore: FirestoreService
) : RepositorioCuentas {

    override suspend fun registrar(usuario: Usuario): ResultadoRegistro {
        if (!usuario.correo.esCorreoValido()) {
            return ResultadoRegistro.Error("Escribe un correo válido, por ejemplo nombre@correo.cl.")
        }
        return runCatching {
            autenticacion.registrar(usuario.correo, usuario.clave)
            firestore.guardarPerfil(usuario)
        }.fold(
            onSuccess = { ResultadoRegistro.Exito(usuario) },
            onFailure = { ResultadoRegistro.Error(it.message ?: "No fue posible crear la cuenta.") }
        )
    }

    override suspend fun iniciarSesion(correo: String, clave: String): Usuario? =
        runCatching {
            val cuenta = autenticacion.iniciarSesion(correo, clave) ?: return null
            firestore.leerPerfil(cuenta.email ?: correo)
                ?: Usuario(
                    nombre = cuenta.displayName ?: correo.substringBefore('@'),
                    correo = cuenta.email ?: correo,
                    clave = "",
                    nivel = NivelAudicion.LEVE,
                    modo = ModoComunicacion.ESCRIBIR
                )
        }.getOrNull()

    override suspend fun enviarRecuperacion(correo: String): Boolean =
        runCatching { autenticacion.enviarCorreoRecuperacion(correo) }.isSuccess

    override suspend fun perfil(correo: String): Usuario? =
        runCatching { firestore.leerPerfil(correo) }.getOrNull()

    override suspend fun guardarPerfil(usuario: Usuario) {
        runCatching { firestore.guardarPerfil(usuario) }
    }

    override suspend fun listado(): List<Usuario> =
        runCatching { firestore.listarPerfiles() }.getOrDefault(emptyList())

    // Orden deliberado: Primero la actividad y el perfil, al final la credencial.
    // Si se borrara primero la credencial, las reglas de Firestore rechazarian
    // los borrados siguientes y quedarian documentos huerfanos.
    override suspend fun eliminarCuenta(correo: String, clave: String): ResultadoEliminacion =
        runCatching {
            firestore.borrarActividadDe(correo)
            firestore.borrarPerfil(correo)
            autenticacion.eliminarCuenta(correo, clave)
        }.fold(
            onSuccess = { ResultadoEliminacion.Exito },
            onFailure = {
                ResultadoEliminacion.Error(
                    it.message ?: "No pudimos eliminar la cuenta. Revisa la contraseña."
                )
            }
        )

    override fun cerrarSesion() {
        autenticacion.cerrarSesion()
    }

    override fun correoActual(): String? = autenticacion.usuarioActual()?.email
}
