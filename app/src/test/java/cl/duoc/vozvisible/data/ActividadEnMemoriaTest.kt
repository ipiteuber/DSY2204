package cl.duoc.vozvisible.data

import cl.duoc.vozvisible.data.memoria.ActividadEnMemoria
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ActividadEnMemoriaTest {

    private fun registro(correo: String, tipo: TipoActividad, texto: String) =
        RegistroActividad(correo = correo, tipo = tipo, texto = texto)

    // El repositorio en memoria es un objeto unico, hay que limpiarlo entre pruebas
    @Before
    fun limpiar() {
        ActividadEnMemoria.reiniciar()
    }

    @Test
    fun `crear devuelve el registro con un identificador asignado`() = runTest {
        val creado = ActividadEnMemoria.crear(registro("ana@correo.cl", TipoActividad.ESCRITO, "Hola"))

        assertTrue(creado.id.isNotBlank())
        assertEquals("Hola", creado.texto)
    }

    @Test
    fun `listar filtra por correo y por tipo`() = runTest {
        ActividadEnMemoria.crear(registro("ana@correo.cl", TipoActividad.ESCRITO, "Mensaje de Ana"))
        ActividadEnMemoria.crear(registro("ana@correo.cl", TipoActividad.HABLADO, "Frase de Ana"))
        ActividadEnMemoria.crear(registro("beto@correo.cl", TipoActividad.ESCRITO, "Mensaje de Beto"))

        val escritosDeAna = ActividadEnMemoria.listar("ana@correo.cl", TipoActividad.ESCRITO)

        assertEquals(1, escritosDeAna.size)
        assertEquals("Mensaje de Ana", escritosDeAna.first().texto)
    }

    @Test
    fun `actualizar cambia el texto del registro guardado`() = runTest {
        val creado = ActividadEnMemoria.crear(registro("caro@correo.cl", TipoActividad.ESCRITO, "Original"))
        ActividadEnMemoria.actualizar(creado.copy(texto = "Corregido"))

        val lista = ActividadEnMemoria.listar("caro@correo.cl", TipoActividad.ESCRITO)
        assertEquals("Corregido", lista.first { it.id == creado.id }.texto)
    }

    @Test
    fun `borrar saca el registro de la lista`() = runTest {
        val creado = ActividadEnMemoria.crear(registro("dani@correo.cl", TipoActividad.UBICACION, "-33.1, -70.1"))
        val antes = ActividadEnMemoria.listar("dani@correo.cl", TipoActividad.UBICACION).size

        ActividadEnMemoria.borrar(TipoActividad.UBICACION, creado.id)
        val despues = ActividadEnMemoria.listar("dani@correo.cl", TipoActividad.UBICACION).size

        assertEquals(antes - 1, despues)
    }
}
