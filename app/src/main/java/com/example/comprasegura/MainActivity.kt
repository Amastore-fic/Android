package com.example.comprasegura

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
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
import androidx.recyclerview.widget.GridLayoutManager
import com.example.comprasegura.databinding.ActivityMainBinding
import com.example.comprasegura.ui.catalogo.CatalogoViewModel
import com.example.comprasegura.ui.catalogo.ProductoAdapter
import com.example.comprasegura.ui.perfil.PerfilActivity
import com.example.comprasegura.ui.producto.ProductoActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.comprasegura.data.remote.RetrofitCliente
import com.example.comprasegura.data.sesion.SesionManager
import com.example.comprasegura.data.remote.dto.CategoriaDto

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CatalogoViewModel by viewModels()
    private var categoriaSeleccionada: Long? = null
    private val api by lazy { RetrofitCliente.api(SesionManager(this)) }

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

        configurarProductos()
        configurarNavegacionInferior()
        configurarBuscador()
        configurarCategorias()
        observarCatalogo()

        if (intent.getBooleanExtra("abrir_categorias", false)) {
            abrirCategorias()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getBooleanExtra("abrir_categorias", false)) {
            abrirCategorias()
        }
    }

    override fun onResume() {
        super.onResume()
        seleccionarNavegacion("inicio")
    }

    private fun abrirCategorias() {
        seleccionarNavegacion("categorias")
        binding.scrollCategorias.smoothScrollTo(0, 0)
        binding.contenidoHome.smoothScrollTo(0, 0)
    }

    private fun configurarProductos() {
        val adapter = ProductoAdapter { producto ->
            val intent = Intent(this, ProductoActivity::class.java)
            intent.putExtra("producto_id", producto.id)
            startActivity(intent)
        }

        binding.listaProductos.layoutManager = GridLayoutManager(this, 2)
        binding.listaProductos.adapter = adapter
        binding.listaProductos.isNestedScrollingEnabled = false
    }

    private fun configurarBuscador() {
        binding.campoBuscar.doAfterTextChanged { texto ->
            viewModel.buscar(texto?.toString(), categoriaSeleccionada)
        }
    }

    private fun configurarCategorias() {
        lifecycleScope.launch {
            try {
                val categorias = withContext(Dispatchers.IO) { api.categorias() }
                mostrarCategorias(categorias)
            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "No se pudieron cargar las categorías: ${e.message ?: "error de conexión"}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun mostrarCategorias(categorias: List<CategoriaDto>) {
        val contenedor = binding.contenedorChipsCategorias
        contenedor.removeAllViews()

        agregarChipCategoria("Todas", null, contenedor)
        categorias.forEach { categoria ->
            agregarChipCategoria(categoria.nombre, categoria.id, contenedor)
        }
        actualizarEstiloCategorias()
    }

    private fun agregarChipCategoria(
        nombre: String,
        id: Long?,
        contenedor: android.widget.LinearLayout
    ) {
        val chip = TextView(this).apply {
            text = nombre
            textSize = 12f
            setPadding(dp(14), dp(8), dp(14), dp(8))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                categoriaSeleccionada = id
                actualizarEstiloCategorias()
                viewModel.buscar(binding.campoBuscar.text?.toString(), categoriaSeleccionada)
            }
        }
        chip.tag = id
        contenedor.addView(
            chip,
            android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { marginEnd = dp(8) }
        )
    }

    private fun actualizarEstiloCategorias() {
        val contenedor = binding.contenedorChipsCategorias
        for (i in 0 until contenedor.childCount) {
            val chip = contenedor.getChildAt(i) as? TextView ?: continue
            val seleccionado = chip.tag == categoriaSeleccionada
            val fondo = GradientDrawable().apply {
                cornerRadius = dp(18).toFloat()
                setColor(if (seleccionado) Color.rgb(95, 122, 219) else Color.rgb(55, 60, 68))
            }
            chip.background = fondo
            chip.setTextColor(Color.WHITE)
            chip.alpha = if (seleccionado) 1f else 0.88f
        }
    }

    private fun dp(valor: Int): Int =
        (valor * resources.displayMetrics.density).toInt()

    private fun observarCatalogo() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.estado.collect { estado ->
                    binding.progreso.visibility =
                        if (estado.cargando) View.VISIBLE else View.GONE

                    (binding.listaProductos.adapter as? ProductoAdapter)
                        ?.submitList(estado.productos)

                    binding.textoMensaje.text =
                        estado.error ?: getString(R.string.catalogo_vacio)

                    binding.textoMensaje.visibility =
                        if (!estado.cargando &&
                            (estado.error != null || estado.productos.isEmpty())
                        ) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun configurarNavegacionInferior() {
        binding.navInicio.setOnClickListener {
            seleccionarNavegacion("inicio")
            binding.contenidoHome.smoothScrollTo(0, 0)
        }

        binding.navCategorias.setOnClickListener {
            seleccionarNavegacion("categorias")
            binding.scrollCategorias.smoothScrollTo(0, 0)
            binding.contenidoHome.smoothScrollTo(0, 0)
        }

        binding.navCarrito.setOnClickListener {
            seleccionarNavegacion("carrito")
            Toast.makeText(
                this,
                "Próximamente: Carrito",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.navPerfil.setOnClickListener {
            seleccionarNavegacion("perfil")
            startActivity(Intent(this, PerfilActivity::class.java))
        }

        seleccionarNavegacion("inicio")
    }

    private fun seleccionarNavegacion(opcion: String) {
        val azul = android.graphics.Color.rgb(95, 122, 219)
        val blanco = android.graphics.Color.WHITE

        val opciones = listOf(
            Triple(binding.iconoInicio, binding.textoInicio, "inicio"),
            Triple(binding.iconoCategorias, binding.textoCategorias, "categorias"),
            Triple(binding.iconoCarrito, binding.textoCarrito, "carrito"),
            Triple(binding.iconoPerfil, binding.textoPerfil, "perfil")
        )

        opciones.forEach { (icono, texto, nombre) ->
            val seleccionado = nombre == opcion
            val color = if (seleccionado) azul else blanco

            icono.setColorFilter(color)
            texto.setTextColor(color)
            texto.setTypeface(
                null,
                if (seleccionado) android.graphics.Typeface.BOLD
                else android.graphics.Typeface.NORMAL
            )
        }
    }
}
