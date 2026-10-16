package com.example.comprasegura.ui.producto

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.comprasegura.databinding.ActivityProductoBinding
import com.example.comprasegura.util.formatearMoneda
import kotlinx.coroutines.launch

class ProductoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductoBinding
    private val viewModel: ProductoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom)
            insets
        }

        binding.botonVolver.setOnClickListener { finish() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.estado.collect { estado ->
                    binding.progreso.visibility = if (estado.cargando) View.VISIBLE else View.GONE
                    binding.textoError.visibility = if (estado.error != null) View.VISIBLE else View.GONE
                    binding.textoError.text = estado.error

                    estado.producto?.let { p ->
                        binding.titulo.text = p.titulo
                        binding.precio.text = formatearMoneda(p.precio)
                        binding.descripcion.text = p.descripcion

                        // Existencias + categoría
                        val categoriaNombre = p.categoria?.nombre ?: "Sin categoría"
                        binding.existencias.text = "${p.existencias} disponibles · $categoriaNombre"

                        // Vendedor
                        val vendedor = p.vendedor
                        if (vendedor != null) {
                            binding.vendedorNombre.text =
                                "${vendedor.nombre_comercial} · ${vendedor.codigo_vendedor}"
                            binding.vendedorSucursal.text = vendedor.sucursal ?: ""
                            binding.vendedorSucursal.visibility =
                                if (vendedor.sucursal.isNullOrBlank()) View.GONE else View.VISIBLE
                        } else {
                            binding.vendedorNombre.text = "Sin vendedor"
                            binding.vendedorSucursal.visibility = View.GONE
                        }

                        // Calificaciones
                        val count = p.calificaciones_count ?: 0
                        val avg = p.calificaciones_avg_puntuacion
                        binding.calificaciones.text = if (count > 0 && avg != null) {
                            "★ $avg ($count calificaciones)"
                        } else {
                            "Sin calificaciones"
                        }

                        // Botón carrito: habilitado solo si hay stock (aún no funcional)
                        binding.botonAgregarCarrito.isEnabled = p.existencias > 0
                    }
                }
            }
        }
    }
}