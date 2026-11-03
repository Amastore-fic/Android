package com.example.comprasegura.data.remote.dto

data class AgregarAlCarritoPeticion(
    val id_producto: Long,
    val cantidad: Int = 1
)

data class CambiarCantidadPeticion(
    val cantidad: Int
)

data class CarritoDto(
    val id: Long,
    val items: List<CarritoItemDto> = emptyList(),
    val total: String = "0.00"
)

data class CarritoItemDto(
    val id: Long,
    val id_producto: Long,
    val cantidad: Int,
    val producto: ProductoDto? = null,
    val subtotal: String? = null
)