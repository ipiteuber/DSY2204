package cl.duoc.vozvisible.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExtensionesTest {

    private fun usuario(
        nombre: String,
        modo: ModoComunicacion = ModoComunicacion.ESCRIBIR,
        nivel: NivelAudicion = NivelAudicion.LEVE,
        apoyos: Set<String> = emptySet()
    ) = Usuario(nombre, "${nombre.lowercase().replace(" ", ".")}@correo.cl", "Clave2026", nivel, modo, apoyos)

    @Test
    fun `acepta correos con formato valido`() {
        assertTrue("camila.rojas@correo.cl".esCorreoValido())
        assertTrue("  matias+test@duoc.cl  ".esCorreoValido())
    }

    @Test
    fun `rechaza correos mal formados`() {
        assertFalse("camila.rojas".esCorreoValido())
        assertFalse("camila@correo".esCorreoValido())
        assertFalse("@correo.cl".esCorreoValido())
        assertFalse("".esCorreoValido())
    }

    @Test
    fun `una clave segura necesita largo, letras y numeros`() {
        assertTrue("Camila2026".esClaveSegura())
        assertFalse("corta1".esClaveSegura())
        assertFalse("solamenteletras".esClaveSegura())
        assertFalse("20262026".esClaveSegura())
    }

    @Test
    fun `validarClaves devuelve null cuando todo esta correcto`() {
        assertNull(validarClaves("Camila2026", "Camila2026"))
    }

    @Test
    fun `validarClaves detecta la clave vacia`() {
        assertEquals("Escribe una contraseña.", validarClaves("", ""))
    }

    @Test
    fun `validarClaves detecta claves distintas`() {
        assertEquals("Las dos contraseñas no son iguales.", validarClaves("Camila2026", "Camila2025"))
    }

    @Test
    fun `el resumen del perfil usa singular y plural segun los apoyos`() {
        val sinApoyos = usuario("Ana")
        assertTrue(sinApoyos.resumenPerfil().endsWith("sin apoyos activos"))

        val unApoyo = usuario("Beto", apoyos = setOf("Avisos con vibración"))
        assertTrue(unApoyo.resumenPerfil().endsWith("1 apoyo activo"))

        val dosApoyos = usuario("Caro", apoyos = setOf("Avisos con vibración", "Frases rápidas guardadas"))
        assertTrue(dosApoyos.resumenPerfil().endsWith("2 apoyos activos"))
    }

    @Test
    fun `las coordenadas se formatean con cinco decimales`() {
        assertEquals("-33.44890, -70.66930", formatearCoordenadas(-33.4489, -70.6693))
    }

    @Test
    fun `las estadisticas agrupan por modo y cuentan por nivel`() {
        val lista = listOf(
            usuario("Ana", ModoComunicacion.ESCRIBIR, NivelAudicion.LEVE),
            usuario("Beto", ModoComunicacion.ESCRIBIR, NivelAudicion.SEVERA),
            usuario("Caro", ModoComunicacion.HABLAR, NivelAudicion.LEVE)
        )
        val datos = lista.estadisticas()

        assertEquals(listOf("Ana", "Beto"), datos.nombresPorModo[ModoComunicacion.ESCRIBIR])
        assertEquals(listOf("Caro"), datos.nombresPorModo[ModoComunicacion.HABLAR])
        assertEquals(2, datos.totalPorNivel[NivelAudicion.LEVE])
        assertEquals(1, datos.totalPorNivel[NivelAudicion.SEVERA])
    }

    @Test
    fun `las estadisticas listan ordenados a los usuarios con apoyos`() {
        val lista = listOf(
            usuario("Zoe", apoyos = setOf("Avisos con vibración")),
            usuario("Ana"),
            usuario("Beto", apoyos = setOf("Frases rápidas guardadas"))
        )
        assertEquals(listOf("Beto", "Zoe"), lista.estadisticas().conApoyos)
    }

    @Test
    fun `filtrarSi aplica el filtro solo cuando la condicion se cumple`() {
        val numeros = listOf(1, 2, 3, 4, 5, 6)
        assertEquals(listOf(2, 4, 6), numeros.filtrarSi(true) { it % 2 == 0 })
        assertEquals(numeros, numeros.filtrarSi(false) { it % 2 == 0 })
    }

    @Test
    fun `el resumen de una ubicacion incluye el texto de las coordenadas`() {
        val registro = RegistroActividad(
            correo = "ana@correo.cl",
            tipo = TipoActividad.UBICACION,
            texto = "-33.44890, -70.66930",
            fecha = 0L
        )
        assertTrue(registro.resumen().contains("-33.44890, -70.66930"))
    }

    @Test
    fun `el resumen de un mensaje escrito recorta los textos largos`() {
        val largo = "a".repeat(120)
        val registro = RegistroActividad(
            correo = "ana@correo.cl",
            tipo = TipoActividad.ESCRITO,
            texto = largo,
            fecha = 0L
        )
        assertTrue(registro.resumen().count { it == 'a' } == 60)
    }
}
