package com.example.comprasegura.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.comprasegura.data.Resultado
import com.example.comprasegura.data.remote.RetrofitCliente
import com.example.comprasegura.data.repositorio.AuthRepositorio
import com.example.comprasegura.data.sesion.SesionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginEstado(
    val cargando: Boolean = false,
    val error: String? = null,
    val exito: Boolean = false,
)

class LoginViewModel(app: Application) : AndroidViewModel(app) {

    private val sesion = SesionManager(app)
    private val repositorio = AuthRepositorio(RetrofitCliente.api(sesion), sesion)

    private val _estado = MutableStateFlow(LoginEstado())
    val estado: StateFlow<LoginEstado> = _estado

    fun iniciarSesion(correo: String, password: String) {
        if (correo.isBlank() || password.isBlank()) {
            _estado.value = LoginEstado(error = "Escribe tu correo y contraseña")
            return
        }
        _estado.value = LoginEstado(cargando = true)
        viewModelScope.launch {
            _estado.value = when (val r = repositorio.login(correo, password)) {
                is Resultado.Exito -> LoginEstado(exito = true)
                is Resultado.Error -> LoginEstado(error = r.mensaje)
            }
        }
    }
}
