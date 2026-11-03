package com.example.comprasegura.data

import com.google.gson.JsonParser
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.io.IOException

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
    } catch (e: SocketTimeoutException) {
        Resultado.Error("El servidor tardó demasiado en responder. Intenta nuevamente.")
    } catch (e: UnknownHostException) {
        Resultado.Error("No se pudo encontrar el servidor. Revisa la dirección de la API y tu conexión.")
    } catch (e: IOException) {
        Resultado.Error("No se pudo conectar con el servidor. Revisa tu conexión.")
    } catch (e: Exception) {
        Resultado.Error(e.message ?: "Ocurrió un error inesperado.")
    }
