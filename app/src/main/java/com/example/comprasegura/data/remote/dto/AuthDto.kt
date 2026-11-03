package com.example.comprasegura.data.remote.dto

data class LoginPeticion(
    val correo: String,
    val password: String,
    val dispositivo: String = "android",
)

data class RegistroPeticion(
    val nombre: String,
    val apellido: String,
    val correo: String,
    val password: String,
    val password_confirmation: String,
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
    val telefono: String? = null,
    val vendedor: VendedorPerfilDto? = null,
)

data class VendedorPerfilDto(
    val id: Long? = null,
    val nombre_comercial: String? = null,
    val codigo_vendedor: String? = null,
    val sucursal: String? = null,
    val descripcion: String? = null,
)

data class ActualizarPerfilPeticion(
    val nombre: String,
    val apellido: String,
    val correo: String,
    val telefono: String? = null,
    val nombre_comercial: String? = null,
    val sucursal: String? = null,
    val descripcion: String? = null,
)

data class CambiarPasswordPeticion(
    val password_actual: String,
    val password: String,
    val password_confirmation: String,
)

data class MensajeRespuesta(
    val message: String?,
)