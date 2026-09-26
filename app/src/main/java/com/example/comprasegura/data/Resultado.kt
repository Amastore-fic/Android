package com.example.comprasegura.data

import com.google.gson.JsonParser
import retrofit2.HttpException

sealed interface Resultado<out T> {
    data class Exito<T>(val datos: T) : Resultado<T>
    data class Error(val mensaje: String, val codigo: Int? = null) : Resultado<Nothing>
}

// Ejecuta una llamada a la API y convierte las excepciones en Resultado.Error,
// usando el "message" que manda Laravel cuando existe.
suspend fun <T> llamarApi(bloque: suspend () -> T): Resultado<T> =
    try {
        Resultado.Exito(bloque())
    } catch (e: HttpException) {
        val mensaje = e.response()?.errorBody()?.string()
            ?.let { runCatching { JsonParser.parseString(it).asJsonObject.get("message")?.asString }.getOrNull() }
        Resultado.Error(mensaje ?: "Error ${e.code()}", e.code())
    } catch (e: Exception) {
        Resultado.Error(e.message ?: "Sin conexion con el servidor")
    }
