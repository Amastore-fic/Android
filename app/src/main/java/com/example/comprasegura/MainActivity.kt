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

// Pantalla de entrada: catalogo. Si no hay sesion, manda al login.
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CatalogoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!SesionManager(this).haySesion) {
            irALogin()
            return
        }

        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom)
            insets
        }

        val adapter = ProductoAdapter { producto ->
            // TODO: abrir ficha del producto (ui/producto) con vendedor, existencias y calificaciones
            Toast.makeText(this, producto.titulo, Toast.LENGTH_SHORT).show()
        }
        binding.listaProductos.adapter = adapter

        binding.campoBuscar.doAfterTextChanged { viewModel.buscar(it?.toString()) }
        binding.botonSalir.setOnClickListener { viewModel.cerrarSesion { irALogin() } }

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

    private fun irALogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
