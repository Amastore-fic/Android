package com.example.comprasegura.data.remote

import com.example.comprasegura.data.remote.dto.AgregarAlCarritoPeticion
import com.example.comprasegura.data.remote.dto.CalificarPeticion
import com.example.comprasegura.data.remote.dto.CambiarCantidadPeticion
import com.example.comprasegura.data.remote.dto.CarritoDto
import com.example.comprasegura.data.remote.dto.CategoriaDto
import com.example.comprasegura.data.remote.dto.CrearPedidoPeticion
import com.example.comprasegura.data.remote.dto.DireccionDto
import com.example.comprasegura.data.remote.dto.LoginPeticion
import com.example.comprasegura.data.remote.dto.LoginRespuesta
import com.example.comprasegura.data.remote.dto.MensajeRespuesta
import com.example.comprasegura.data.remote.dto.Pagina
import com.example.comprasegura.data.remote.dto.PedidoDto
import com.example.comprasegura.data.remote.dto.ProductoDto
import com.example.comprasegura.data.remote.dto.RegistroPeticion
import com.example.comprasegura.data.remote.dto.UsuarioDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

// Endpoints de /api/v1 que usa el cliente (comprador). La app Android no cubre
// las funciones de vendedor ni de administrador; esas viven en el panel web.
interface ApiService {

    // ---- Autenticacion ----
    @POST("auth/login")
    suspend fun login(@Body peticion: LoginPeticion): LoginRespuesta

    @POST("auth/registro")
    suspend fun registrar(@Body peticion: RegistroPeticion): LoginRespuesta

    @POST("auth/logout")
    suspend fun logout(): MensajeRespuesta

    @GET("auth/perfil")
    suspend fun perfil(): UsuarioDto

    // ---- Catalogo ----
    @GET("productos")
    suspend fun productos(
        @Query("buscar") buscar: String? = null,
        @Query("categoria") categoria: Long? = null,
        @Query("page") pagina: Int = 1,
    ): Pagina<ProductoDto>

    @GET("productos/{id}")
    suspend fun producto(@Path("id") id: Long): ProductoDto

    @GET("categorias")
    suspend fun categorias(): List<CategoriaDto>

    // ---- Carrito ----
    @GET("carrito")
    suspend fun carrito(): CarritoDto

    @POST("carrito/items")
    suspend fun agregarAlCarrito(@Body peticion: AgregarAlCarritoPeticion): CarritoDto

    @PUT("carrito/items/{id}")
    suspend fun cambiarCantidad(@Path("id") idItem: Long, @Body peticion: CambiarCantidadPeticion): CarritoDto

    @DELETE("carrito/items/{id}")
    suspend fun eliminarDelCarrito(@Path("id") idItem: Long): CarritoDto

    // ---- Direcciones ----
    @GET("direcciones")
    suspend fun direcciones(): List<DireccionDto>

    @POST("direcciones")
    suspend fun registrarDireccion(@Body direccion: DireccionDto): DireccionDto

    // ---- Pedidos y pago ----
    @POST("pedidos")
    suspend fun crearPedido(@Body peticion: CrearPedidoPeticion): PedidoDto

    @POST("pedidos/{id}/pago")
    suspend fun pagarPedido(@Path("id") idPedido: Long): PedidoDto

    @GET("pedidos")
    suspend fun pedidos(): List<PedidoDto>

    @GET("pedidos/{id}")
    suspend fun pedido(@Path("id") idPedido: Long): PedidoDto

    // ---- Calificaciones ----
    @POST("productos/{id}/calificaciones")
    suspend fun calificar(@Path("id") idProducto: Long, @Body peticion: CalificarPeticion): MensajeRespuesta
}