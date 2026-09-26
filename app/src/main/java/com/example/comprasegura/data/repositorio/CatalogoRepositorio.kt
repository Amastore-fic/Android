package com.example.comprasegura.data.repositorio

import com.example.comprasegura.data.Resultado
import com.example.comprasegura.data.llamarApi
import com.example.comprasegura.data.remote.ApiService
import com.example.comprasegura.data.remote.dto.CategoriaDto
import com.example.comprasegura.data.remote.dto.Pagina
import com.example.comprasegura.data.remote.dto.ProductoDto

class CatalogoRepositorio(private val api: ApiService) {

    suspend fun buscar(texto: String?, idCategoria: Long?, pagina: Int = 1): Resultado<Pagina<ProductoDto>> =
        llamarApi { api.productos(texto?.takeIf { it.isNotBlank() }, idCategoria, pagina) }

    suspend fun producto(id: Long): Resultado<ProductoDto> =
        llamarApi { api.producto(id) }

    suspend fun categorias(): Resultado<List<CategoriaDto>> =
        llamarApi { api.categorias() }
}
