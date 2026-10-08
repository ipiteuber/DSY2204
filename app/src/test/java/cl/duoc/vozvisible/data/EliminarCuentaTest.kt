package cl.duoc.vozvisible.data

import cl.duoc.vozvisible.data.memoria.ActividadEnMemoria
import cl.duoc.vozvisible.data.memoria.CuentasEnMemoria
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// Baja de cuenta: Verifica la validacion de la clave y el borrado en cascada
// del historial, que es lo que no se puede deshacer una vez ejecutado.
class EliminarCuentaTest {

    private val camila = usuariosIniciales.first()

    @Before
    fun limpiar() {
        CuentasEnMemoria.reiniciar()
        ActividadEnMemoria.reiniciar()
    }

    @Test
    fun `eliminar con la clave correcta saca la cuenta del listado`() = runTest {
        val resultado = CuentasEnMemoria.eliminarCuenta(camila.correo, camila.clave)

        assertTrue(resultado is ResultadoEliminacion.Exito)
        assertEquals(4, CuentasEnMemoria.listado().size)
        assertNull(CuentasEnMemoria.perfil(camila.correo))
    }

    @Test
    fun `eliminar con la clave equivocada no borra nada`() = runTest {
        val resultado = CuentasEnMemoria.eliminarCuenta(camila.correo, "claveMala1")

        assertTrue(resultado is ResultadoEliminacion.Error)
        assertEquals(
            "La contraseña no coincide con la cuenta.",
            (resultado as ResultadoEliminacion.Error).mensaje
        )
        assertEquals(5, CuentasEnMemoria.listado().size)
    }

    @Test
    fun `eliminar un correo inexistente devuelve error`() = runTest {
        val resultado = CuentasEnMemoria.eliminarCuenta("nadie@correo.cl", "Lo#Que#Sea1")

        assertTrue(resultado is ResultadoEliminacion.Error)
        assertEquals(5, CuentasEnMemoria.listado().size)
    }

    @Test
    fun `eliminar la cuenta borra tambien su historial`() = runTest {
        ActividadEnMemoria.crear(
            RegistroActividad(correo = camila.correo, tipo = TipoActividad.ESCRITO, texto = "Hola")
        )
        ActividadEnMemoria.crear(
            RegistroActividad(correo = camila.correo, tipo = TipoActividad.HABLADO, texto = "Gracias")
        )

        CuentasEnMemoria.eliminarCuenta(camila.correo, camila.clave)

        assertTrue(ActividadEnMemoria.listar(camila.correo, TipoActividad.ESCRITO).isEmpty())
        assertTrue(ActividadEnMemoria.listar(camila.correo, TipoActividad.HABLADO).isEmpty())
    }

    @Test
    fun `el historial de otra cuenta no se ve afectado`() = runTest {
        val otro = usuariosIniciales[1]
        ActividadEnMemoria.crear(
            RegistroActividad(correo = otro.correo, tipo = TipoActividad.ESCRITO, texto = "Mi mensaje")
        )

        CuentasEnMemoria.eliminarCuenta(camila.correo, camila.clave)

        assertEquals(1, ActividadEnMemoria.listar(otro.correo, TipoActividad.ESCRITO).size)
    }

    @Test
    fun `eliminar la cuenta en sesion deja la sesion cerrada`() = runTest {
        CuentasEnMemoria.iniciarSesion(camila.correo, camila.clave)

        CuentasEnMemoria.eliminarCuenta(camila.correo, camila.clave)

        assertNull(CuentasEnMemoria.correoActual())
    }
}
