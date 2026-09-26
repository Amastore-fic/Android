package com.example.comprasegura.data.sesion

import android.content.Context
import androidx.core.content.edit

// Guarda el token Bearer y los datos basicos del usuario en SharedPreferences.
class SesionManager(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("comprasegura_sesion", Context.MODE_PRIVATE)

    val token: String?
        get() = prefs.getString(CLAVE_TOKEN, null)

    val nombreUsuario: String?
        get() = prefs.getString(CLAVE_NOMBRE, null)

    val haySesion: Boolean
        get() = token != null

    fun guardar(token: String, nombre: String, rol: String) {
        prefs.edit {
            putString(CLAVE_TOKEN, token)
            putString(CLAVE_NOMBRE, nombre)
            putString(CLAVE_ROL, rol)
        }
    }

    fun cerrar() {
        prefs.edit { clear() }
    }

    private companion object {
        const val CLAVE_TOKEN = "token"
        const val CLAVE_NOMBRE = "nombre"
        const val CLAVE_ROL = "rol"
    }
}
