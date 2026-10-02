package com.example.comprasegura.util

import android.content.Context
import android.content.Intent
import com.example.comprasegura.ui.auth.LoginActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

// Muestra un popup cuando el usuario intenta hacer algo que requiere sesion.
object SessionDialog {

    fun show(
        context: Context,
        message: String = "Necesitas iniciar sesión para continuar",
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle("Inicia sesión")
            .setMessage(message)
            .setPositiveButton("Iniciar sesión") { _, _ ->
                context.startActivity(Intent(context, LoginActivity::class.java))
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}