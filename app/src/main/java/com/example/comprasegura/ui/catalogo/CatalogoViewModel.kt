package com.example.comprasegura.ui.catalogo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.comprasegura.data.Resultado
import com.example.comprasegura.data.remote.RetrofitCliente
import com.example.comprasegura.data.remote.dto.ProductoDto
import com.example.comprasegura.data.repositorio.AuthRepositorio
import com.example.comprasegura.data.repositorio.CatalogoRepositorio
import com.example.comprasegura.data.sesion.SesionManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CatalogoEstado(
    val cargando: Boolean = false,
    val productos: List<ProductoDto> = emptyList(),
    val error: String? = null,
)

class CatalogoViewModel(app: Application) : AndroidViewModel(app) {

    private val sesion = SesionManager(app)
    private val api = RetrofitCliente.api(sesion)
    private val catalogo = CatalogoRepositorio(api)
    private val auth = AuthRepositorio(api, sesion)

    private val _estado = MutableStateFlow(CatalogoEstado())
    val estado: StateFlow<CatalogoEstado> = _estado

    private var busqueda: Job? = null

    init {
        buscar(null)
    }

    // Espera un momento antes de consultar para no llamar a la API en cada tecla
    fun buscar(texto: String?, idCategoria: Long? = null) {
        busqueda?.cancel()
        busqueda = viewModelScope.launch {
            if (!texto.isNullOrEmpty()) delay(350)
            _estado.value = _estado.value.copy(cargando = true, error = null)
            _estado.value = when (val r = catalogo.buscar(texto, idCategoria)) {
                is Resultado.Exito -> CatalogoEstado(productos = r.datos.data)
                is Resultado.Error -> _estado.value.copy(cargando = false, error = r.mensaje)
            }
        }
    }

    fun cerrarSesion(alTerminar: () -> Unit) {
        viewModelScope.launch {
            auth.logout()
            alTerminar()
        }
    }
}
