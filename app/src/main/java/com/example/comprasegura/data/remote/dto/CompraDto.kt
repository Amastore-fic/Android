package com.example.comprasegura.data.remote.dto

data class DireccionDto(
    val id: Long? = null,
    val destinatario: String,
    val calle: String,
    val numero_exterior: String,
    val numero_interior: String? = null,
    val colonia: String,
    val ciudad: String,
    val estado: String,
    val codigo_postal: String,
    val telefono: String,
    val referencias: String? = null,
)

data class CrearPedidoPeticion(
    val id_direccion: Long,
)

data class PedidoDetalleDto(
    val id: Long,
    val id_producto: Long,
    val titulo_producto: String,
    val cantidad: Int,
    val precio_unitario: String,
    val subtotal: String,
)

data class PedidoDto(
    val id: Long,
    val folio: String,
    val total: String,
    val estado: String,
    val created_at: String,
    val detalles: List<PedidoDetalleDto>?,
)

data class CalificarPeticion(
    val puntuacion: Int,
    val comentario: String?,
)