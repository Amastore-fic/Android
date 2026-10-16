package com.example.comprasegura

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.comprasegura.data.sesion.SesionManager
import com.example.comprasegura.databinding.ActivityMainBinding
import com.example.comprasegura.ui.auth.LoginActivity
import com.example.comprasegura.ui.catalogo.CatalogoViewModel
import com.example.comprasegura.ui.catalogo.ProductoAdapter
import kotlinx.coroutines.launch
import com.example.comprasegura.ui.producto.ProductoActivity

// Pantalla de entrada: catalogo publico. El login solo es obligatorio para comprar.
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CatalogoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom)
            insets
        }

        val adapter = ProductoAdapter { producto ->
            val intent = Intent(this, ProductoActivity::class.java)
            intent.putExtra("producto_id", producto.id)
            startActivity(intent)
        }
        binding.listaProductos.adapter = adapter

        binding.campoBuscar.doAfterTextChanged { viewModel.buscar(it?.toString()) }

        // Se llama aquí para configurar el botón la primera vez
        actualizarBotonSesion()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.estado.collect { estado ->
                    binding.progreso.visibility = if (estado.cargando) View.VISIBLE else View.GONE
                    adapter.submitList(estado.productos)
                    binding.textoMensaje.text = estado.error ?: getString(R.string.catalogo_vacio)
                    binding.textoMensaje.visibility =
                        if (!estado.cargando && (estado.error != null || estado.productos.isEmpty())) View.VISIBLE
                        else View.GONE
                }
            }
        }
    }

    // Se ejecuta cada vez que la pantalla vuelve a estar visible
    override fun onResume() {
        super.onResume()
        actualizarBotonSesion()
    }

    // Función que ajusta el texto y la acción del botón según la sesión
    private fun actualizarBotonSesion() {
        if (SesionManager(this).haySesion) {
            binding.botonSalir.text = "Cerrar Sesión"
            binding.botonSalir.setOnClickListener {
                viewModel.cerrarSesion { goToLogin() }
            }
        } else {
            binding.botonSalir.text = "Iniciar Sesión"
            binding.botonSalir.setOnClickListener { goToLogin() }
        }
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}