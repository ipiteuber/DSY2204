package cl.duoc.vozvisible.data

import cl.duoc.vozvisible.data.memoria.CuentasEnMemoria
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CuentasEnMemoriaTest {

    private val nueva = Usuario(
        nombre = "Pablo Soto",
        correo = "pablo.soto@correo.cl",
        clave = "Pablo2026",
        nivel = NivelAudicion.MODERADA,
        modo = ModoComunicacion.AMBOS,
        apoyos = setOf("Avisos con vibración")
    )

    @Before
    fun limpiar() {
        CuentasEnMemoria.reiniciar()
    }

    @Test
    fun `parte con las cinco cuentas precargadas`() = runTest {
        assertEquals(5, CuentasEnMemoria.listado().size)
    }

    @Test
    fun `registrar una cuenta nueva devuelve exito y la agrega`() = runTest {
        val resultado = CuentasEnMemoria.registrar(nueva)

        assertTrue(resultado is ResultadoRegistro.Exito)
        assertEquals(6, CuentasEnMemoria.listado().size)
    }

    @Test
    fun `no deja registrar dos veces el mismo correo`() = runTest {
        CuentasEnMemoria.registrar(nueva)
        val repetido = CuentasEnMemoria.registrar(nueva.copy(nombre = "Otro Nombre"))

        assertTrue(repetido is ResultadoRegistro.Error)
        assertEquals(6, CuentasEnMemoria.listado().size)
    }

    @Test
    fun `el correo se compara sin distinguir mayusculas`() = runTest {
        CuentasEnMemoria.registrar(nueva)
        val repetido = CuentasEnMemoria.registrar(nueva.copy(correo = "PABLO.SOTO@CORREO.CL"))

        assertTrue(repetido is ResultadoRegistro.Error)
    }

    @Test
    fun `iniciar sesion con credenciales correctas devuelve el usuario`() = runTest {
        val usuario = CuentasEnMemoria.iniciarSesion("camila.rojas@correo.cl", "Camila#2026")

        assertNotNull(usuario)
        assertEquals("Camila Rojas", usuario?.nombre)
    }

    @Test
    fun `iniciar sesion con la clave equivocada devuelve null`() = runTest {
        assertNull(CuentasEnMemoria.iniciarSesion("camila.rojas@correo.cl", "otraClave1"))
    }

    @Test
    fun `iniciar sesion con un correo que no existe devuelve null`() = runTest {
        assertNull(CuentasEnMemoria.iniciarSesion("nadie@correo.cl", "Camila#2026"))
    }

    @Test
    fun `la recuperacion solo procede si el correo existe`() = runTest {
        assertTrue(CuentasEnMemoria.enviarRecuperacion("camila.rojas@correo.cl"))
        assertEquals(false, CuentasEnMemoria.enviarRecuperacion("nadie@correo.cl"))
    }

    @Test
    fun `iniciar sesion deja el correo como sesion activa y cerrar la limpia`() = runTest {
        CuentasEnMemoria.iniciarSesion("camila.rojas@correo.cl", "Camila#2026")
        assertEquals("camila.rojas@correo.cl", CuentasEnMemoria.correoActual())

        CuentasEnMemoria.cerrarSesion()
        assertNull(CuentasEnMemoria.correoActual())
    }

    @Test
    fun `guardar el perfil actualiza los datos del usuario`() = runTest {
        CuentasEnMemoria.registrar(nueva)
        CuentasEnMemoria.guardarPerfil(nueva.copy(nombre = "Pablo Soto Vera"))

        assertEquals("Pablo Soto Vera", CuentasEnMemoria.perfil("pablo.soto@correo.cl")?.nombre)
    }
}
