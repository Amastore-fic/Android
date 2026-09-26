package com.example.comprasegura

import com.example.comprasegura.util.formatearMoneda
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FormatoTest {

    @Test
    fun formateaMontosQueMandaLaApi() {
        val texto = formatearMoneda("649.00")
        assertTrue(texto, texto.contains("649.00"))
    }

    @Test
    fun devuelveElTextoOriginalSiNoEsNumero() {
        assertEquals("abc", formatearMoneda("abc"))
    }
}
