package cl.duoc.vozvisible.data.firebase

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

// Envuelve Firebase Authentication y convierte sus Task en funciones suspend.
class AutenticacionService(private val auth: FirebaseAuth = Firebase.auth) {

    // Tras crear la cuenta se espera el token de sesion: Firestore lo necesita para
    // que sus reglas reconozcan al usuario, y sin esa espera el primer escrito falla.
    suspend fun registrar(correo: String, clave: String): FirebaseUser? {
        val usuario = auth.createUserWithEmailAndPassword(correo.trim(), clave).await().user
        usuario?.getIdToken(true)?.await()
        return usuario
    }

    suspend fun iniciarSesion(correo: String, clave: String): FirebaseUser? {
        val usuario = auth.signInWithEmailAndPassword(correo.trim(), clave).await().user
        usuario?.getIdToken(true)?.await()
        return usuario
    }

    suspend fun enviarCorreoRecuperacion(correo: String) {
        auth.sendPasswordResetEmail(correo.trim()).await()
    }

    // Firebase exige una credencial reciente para borrar una cuenta: Si la sesion
    // lleva tiempo abierta, delete() falla con requires-recent-login. Por eso se
    // vuelve a autenticar con la clave antes de eliminar.
    suspend fun eliminarCuenta(correo: String, clave: String) {
        val usuario = auth.currentUser ?: error("No hay una sesión iniciada.")
        val credencial = EmailAuthProvider.getCredential(correo.trim(), clave)
        usuario.reauthenticate(credencial).await()
        usuario.delete().await()
    }

    fun cerrarSesion() = auth.signOut()

    fun usuarioActual(): FirebaseUser? = auth.currentUser
}
