package com.example.comprasegura.util

import java.text.NumberFormat
import java.util.Locale

private val moneda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-MX"))

// La API manda los montos como texto ("649.00"); aqui solo se formatean.
fun formatearMoneda(monto: String): String =
    monto.toBigDecimalOrNull()?.let { moneda.format(it) } ?: monto
