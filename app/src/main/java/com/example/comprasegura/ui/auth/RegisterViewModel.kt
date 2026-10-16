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

data class RegisterEstado(
    val cargando: Boolean = false,
    val error: String? = null,
    val exito: Boolean = false,
)

class RegisterViewModel(app: Application) : AndroidViewModel(app) {

    private val sesion = SesionManager(app)
    private val repositorio = AuthRepositorio(RetrofitCliente.api(sesion), sesion)

    private val _estado = MutableStateFlow(RegisterEstado())
    val estado: StateFlow<RegisterEstado> = _estado

    fun registrar(
        nombre: String,
        apellido: String,
        correo: String,
        password: String,
        confirmar: String,
    ) {
        // Validaciones locales (alineadas con el backend)
        if (nombre.isBlank() || apellido.isBlank() || correo.isBlank() || password.isBlank() || confirmar.isBlank()) {
            _estado.value = RegisterEstado(error = "Completa todos los campos")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            _estado.value = RegisterEstado(error = "El correo no es válido")
            return
        }
        if (password.length < 8) {
            _estado.value = RegisterEstado(error = "La contraseña debe tener al menos 8 caracteres")
            return
        }
        if (!password.any { it.isLetter() }) {
            _estado.value = RegisterEstado(error = "La contraseña debe incluir al menos una letra")
            return
        }
        if (!password.any { it.isDigit() }) {
            _estado.value = RegisterEstado(error = "La contraseña debe incluir al menos un número")
            return
        }
        if (password != confirmar) {
            _estado.value = RegisterEstado(error = "Las contraseñas no coinciden")
            return
        }

        // Llamada al backend
        _estado.value = RegisterEstado(cargando = true)
        viewModelScope.launch {
            _estado.value = when (val r = repositorio.registrar(nombre, apellido, correo, password, confirmar)) {
                is Resultado.Exito -> RegisterEstado(exito = true)
                is Resultado.Error -> RegisterEstado(error = r.mensaje)
            }
        }
    }
}