package com.example.comprasegura.ui.producto

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.comprasegura.data.Resultado
import com.example.comprasegura.data.remote.RetrofitCliente
import com.example.comprasegura.data.remote.dto.ProductoDto
import com.example.comprasegura.data.repositorio.CatalogoRepositorio
import com.example.comprasegura.data.sesion.SesionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProductoEstado(
    val cargando: Boolean = false,
    val producto: ProductoDto? = null,
    val error: String? = null,
)

class ProductoViewModel(app: Application, savedState: SavedStateHandle) : AndroidViewModel(app) {

    private val productoId: Long = savedState.get<Long>("producto_id") ?: 0L
    private val sesion = SesionManager(app)
    private val repositorio = CatalogoRepositorio(RetrofitCliente.api(sesion))

    private val _estado = MutableStateFlow(ProductoEstado())
    val estado: StateFlow<ProductoEstado> = _estado

    init {
        cargar()
    }

    fun cargar() {
        if (productoId <= 0) {
            _estado.value = ProductoEstado(error = "Producto no válido")
            return
        }
        _estado.value = ProductoEstado(cargando = true)
        viewModelScope.launch {
            _estado.value = when (val r = repositorio.producto(productoId)) {
                is Resultado.Exito -> ProductoEstado(producto = r.datos)
                is Resultado.Error -> ProductoEstado(error = r.mensaje)
            }
        }
    }
}