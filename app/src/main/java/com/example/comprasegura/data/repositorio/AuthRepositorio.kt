package com.example.comprasegura.data.repositorio

import com.example.comprasegura.data.Resultado
import com.example.comprasegura.data.llamarApi
import com.example.comprasegura.data.remote.ApiService
import com.example.comprasegura.data.remote.dto.LoginPeticion
import com.example.comprasegura.data.remote.dto.UsuarioDto
import com.example.comprasegura.data.sesion.SesionManager

class AuthRepositorio(
    private val api: ApiService,
    private val sesion: SesionManager,
) {

    suspend fun login(correo: String, password: String): Resultado<UsuarioDto> {
        val resultado = llamarApi { api.login(LoginPeticion(correo.trim(), password)) }
        return when (resultado) {
            is Resultado.Error -> resultado
            is Resultado.Exito -> {
                val usuario = resultado.datos.usuario
                // La app movil es solo para compradores
                if (usuario.rol != "cliente") {
                    Resultado.Error("Esta app es para clientes. Vendedores y administradores usan el panel web.")
                } else {
                    sesion.guardar(resultado.datos.token, usuario.nombre, usuario.rol)
                    Resultado.Exito(usuario)
                }
            }
        }
    }

    suspend fun logout() {
        llamarApi { api.logout() }
        sesion.cerrar()
    }
}
