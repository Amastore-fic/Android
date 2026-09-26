package com.example.comprasegura.data.remote.dto

// Los nombres de campo respetan el snake_case que devuelve la API.

data class LoginPeticion(
    val correo: String,
    val password: String,
    val dispositivo: String = "android",
)

data class LoginRespuesta(
    val token: String,
    val tipo: String,
    val usuario: UsuarioDto,
)

data class UsuarioDto(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val rol: String,
    val activo: Boolean,
)

data class MensajeRespuesta(
    val message: String?,
)
