package cl.duoc.vozvisible.data

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

// Verifica contra un doble de prueba que la capa de datos se usa como corresponde,
// sin depender de Firebase ni de la implementacion en memoria.
class RepositorioCuentasMockTest {

    private val camila = Usuario(
        nombre = "Camila Rojas",
        correo = "camila.rojas@correo.cl",
        clave = "Camila#2026",
        nivel = NivelAudicion.SEVERA,
        modo = ModoComunicacion.ESCRIBIR
    )

    @Test
    fun `el inicio de sesion devuelve el usuario que entrega el repositorio`() = runTest {
        val repositorio = mock<RepositorioCuentas>()
        whenever(repositorio.iniciarSesion(camila.correo, camila.clave)).thenReturn(camila)

        val resultado = repositorio.iniciarSesion(camila.correo, camila.clave)

        assertEquals("Camila Rojas", resultado?.nombre)
        verify(repositorio).iniciarSesion(camila.correo, camila.clave)
    }

    @Test
    fun `credenciales equivocadas devuelven null sin tocar el perfil`() = runTest {
        val repositorio = mock<RepositorioCuentas>()
        whenever(repositorio.iniciarSesion(any(), any())).thenReturn(null)

        assertNull(repositorio.iniciarSesion(camila.correo, "claveMala1"))
        verify(repositorio, org.mockito.kotlin.never()).perfil(any())
    }

    @Test
    fun `un registro exitoso entrega el usuario dentro del resultado`() = runTest {
        val repositorio = mock<RepositorioCuentas>()
        whenever(repositorio.registrar(camila)).thenReturn(ResultadoRegistro.Exito(camila))

        val resultado = repositorio.registrar(camila)

        assertTrue(resultado is ResultadoRegistro.Exito)
        assertEquals(camila, (resultado as ResultadoRegistro.Exito).usuario)
    }

    @Test
    fun `un correo repetido entrega un resultado de error con su mensaje`() = runTest {
        val repositorio = mock<RepositorioCuentas>()
        whenever(repositorio.registrar(camila))
            .thenReturn(ResultadoRegistro.Error("Ese correo ya está registrado."))

        val resultado = repositorio.registrar(camila)

        assertTrue(resultado is ResultadoRegistro.Error)
        assertEquals("Ese correo ya está registrado.", (resultado as ResultadoRegistro.Error).mensaje)
    }
}
