package com.example.comprasegura.data.remote

import com.example.comprasegura.BuildConfig
import com.example.comprasegura.data.sesion.SesionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitCliente {

    @Volatile
    private var instancia: ApiService? = null

    fun api(sesion: SesionManager): ApiService =
        instancia ?: synchronized(this) {
            instancia ?: crear(sesion).also { instancia = it }
        }

    private fun crear(sesion: SesionManager): ApiService {
        val http = OkHttpClient.Builder()
            // Agrega Accept: application/json y el token Bearer si hay sesion
            .addInterceptor { cadena ->
                val peticion = cadena.request().newBuilder()
                    .header("Accept", "application/json")
                    .apply { sesion.token?.let { header("Authorization", "Bearer $it") } }
                    .build()
                cadena.proceed(peticion)
            }
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
                }
            )
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(http)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
