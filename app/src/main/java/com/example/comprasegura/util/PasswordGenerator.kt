package com.example.comprasegura.util

// Genera contraseñas seguras que cumplen las reglas del backend:
// minimo 8 caracteres, al menos una letra y al menos un numero.
object PasswordGenerator {

    private const val LONGITUD_DEFAULT = 12
    private const val MAYUSCULAS = "ABCDEFGHJKLMNPQRSTUVWXYZ"   // sin I, O
    private const val MINUSCULAS = "abcdefghijkmnpqrstuvwxyz"   // sin l, o
    private const val DIGITOS = "23456789"                       // sin 0, 1
    private val TODOS = MAYUSCULAS + MINUSCULAS + DIGITOS

    fun generar(longitud: Int = LONGITUD_DEFAULT): String {
        val chars = mutableListOf<Char>()

        // Garantiza al menos una letra (mayuscula y minuscula) y un numero
        chars.add(MAYUSCULAS.random())
        chars.add(MINUSCULAS.random())
        chars.add(DIGITOS.random())

        // Rellena el resto con caracteres aleatorios
        repeat(longitud - chars.size) {
            chars.add(TODOS.random())
        }

        // Mezcla para que no queden los garantizados al inicio
        return chars.shuffled().joinToString("")
    }
}