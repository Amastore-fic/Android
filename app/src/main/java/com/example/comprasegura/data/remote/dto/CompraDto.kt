package com.example.comprasegura.data.remote.dto

// DTOs del flujo de compra. Se completan conforme la API implemente cada endpoint.

data class AgregarAlCarritoPeticion(
    val id_producto: Long,
    val cantidad: Int,
)

data class CambiarCantidadPeticion(
    val cantidad: Int,
)

data class CarritoItemDto(
    val id: Long,
    val cantidad: Int,
    val producto: ProductoDto,
)

data class CarritoDto(
    val id: Long,
    val items: List<CarritoItemDto>,
    val total: String,
)

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
