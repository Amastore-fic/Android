package com.example.comprasegura.data.remote.dto

// Respuesta paginada de Laravel ({ data: [...], current_page, last_page, ... })
data class Pagina<T>(
    val data: List<T>,
    val current_page: Int,
    val last_page: Int,
    val total: Int,
)

data class CategoriaDto(
    val id: Long,
    val nombre: String,
    val slug: String?,
)

data class VendedorPublicoDto(
    val id: Long,
    val nombre_comercial: String,
    val codigo_vendedor: String,
    val sucursal: String?,
    val descripcion: String?,
)

data class ProductoDto(
    val id: Long,
    val titulo: String,
    val descripcion: String,
    // La API manda montos como texto ("649.00"); el cliente solo los muestra
    val precio: String,
    val existencias: Int,
    val imagen_url: String?,
    val categoria: CategoriaDto?,
    val vendedor: VendedorPublicoDto?,
    val calificaciones_avg_puntuacion: String?,
    val calificaciones_count: Int?,
)
