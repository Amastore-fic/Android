package com.example.comprasegura.ui.perfil

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.comprasegura.MainActivity
import com.example.comprasegura.data.remote.RetrofitCliente
import com.example.comprasegura.data.remote.dto.ActualizarPerfilPeticion
import com.example.comprasegura.data.remote.dto.CambiarPasswordPeticion
import com.example.comprasegura.data.sesion.SesionManager
import com.example.comprasegura.databinding.ActivityPerfilBinding
import com.example.comprasegura.ui.auth.LoginActivity
import com.example.comprasegura.ui.auth.RegisterActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private lateinit var sesion: SesionManager

    private val api by lazy {
        RetrofitCliente.api(sesion)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ajusta el contenido a las barras del sistema.
        val paddingInicialIzquierdo = binding.perfilRoot.paddingLeft
        val paddingInicialSuperior = binding.perfilRoot.paddingTop
        val paddingInicialDerecho = binding.perfilRoot.paddingRight
        val paddingInicialInferior = binding.perfilRoot.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(binding.perfilRoot) { view, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                paddingInicialIzquierdo + barras.left,
                paddingInicialSuperior + barras.top,
                paddingInicialDerecho + barras.right,
                paddingInicialInferior + barras.bottom
            )

            insets
        }

        ViewCompat.requestApplyInsets(binding.perfilRoot)

        sesion = SesionManager(this)

        configurarBotones()
        configurarNavegacionInferior()
    }

    override fun onResume() {
        super.onResume()
        if (::sesion.isInitialized) {
            mostrarEstadoSesion()
        }
    }

    private fun configurarNavegacionInferior() {
        binding.navInicio.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        binding.navCategorias.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("abrir_categorias", true)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }

        binding.navCarrito.setOnClickListener {
            Toast.makeText(
                this,
                "El carrito estará disponible próximamente.",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Perfil es la sección actual.
        binding.iconoPerfil.setColorFilter(android.graphics.Color.rgb(95, 122, 219))
        binding.textoPerfil.setTextColor(android.graphics.Color.rgb(95, 122, 219))
        binding.textoPerfil.setTypeface(null, android.graphics.Typeface.BOLD)
    }

    private fun configurarBotones() {
        binding.botonIniciarSesion.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.botonRegistrarse.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.botonGuardarPerfil.setOnClickListener {
            guardarPerfil()
        }

        binding.botonCambiarPassword.setOnClickListener {
            cambiarPassword()
        }

        binding.botonCerrarSesion.setOnClickListener {
            cerrarSesion()
        }
    }

    private fun mostrarEstadoSesion() {
        if (!sesion.haySesion) {
            binding.contenedorSinSesion.visibility = View.VISIBLE
            binding.contenedorConSesion.visibility = View.GONE
            binding.textoError.visibility = View.GONE
        } else {
            binding.contenedorSinSesion.visibility = View.GONE
            binding.contenedorConSesion.visibility = View.VISIBLE
            cargarPerfil()
        }
    }

    private fun cargarPerfil() {
        lifecycleScope.launch {
            mostrarCargando(true)

            try {
                val usuario = withContext(Dispatchers.IO) {
                    api.perfil()
                }

                binding.textoNombreUsuario.text =
                    "${usuario.nombre} ${usuario.apellido}"
                binding.textoCorreoUsuario.text = usuario.correo
                binding.textoRolUsuario.text = usuario.rol.replaceFirstChar {
                    it.uppercase()
                }

                binding.campoNombre.setText(usuario.nombre)
                binding.campoApellido.setText(usuario.apellido)
                binding.campoCorreo.setText(usuario.correo)
                binding.campoTelefono.setText(usuario.telefono.orEmpty())

                mostrarError(null)

            } catch (e: Exception) {
                mostrarError(mensajeError(e))
            } finally {
                mostrarCargando(false)
            }
        }
    }

    private fun guardarPerfil() {
        val nombre = binding.campoNombre.text?.toString()?.trim().orEmpty()
        val apellido = binding.campoApellido.text?.toString()?.trim().orEmpty()
        val correo = binding.campoCorreo.text?.toString()?.trim().orEmpty()
        val telefono = binding.campoTelefono.text?.toString()?.trim()

        if (nombre.isBlank() || apellido.isBlank() || correo.isBlank()) {
            mostrarError("Completa nombre, apellido y correo.")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            mostrarError("Escribe un correo electrónico válido.")
            return
        }

        lifecycleScope.launch {
            mostrarCargando(true)

            try {
                val usuario = withContext(Dispatchers.IO) {
                    api.actualizarPerfil(
                        ActualizarPerfilPeticion(
                            nombre = nombre,
                            apellido = apellido,
                            correo = correo,
                            telefono = telefono?.ifBlank { null }
                        )
                    )
                }

                binding.textoNombreUsuario.text =
                    "${usuario.nombre} ${usuario.apellido}"
                binding.textoCorreoUsuario.text = usuario.correo

                mostrarError(null)
                Toast.makeText(
                    this@PerfilActivity,
                    "Perfil actualizado correctamente.",
                    Toast.LENGTH_LONG
                ).show()

            } catch (e: Exception) {
                mostrarError(mensajeError(e))
            } finally {
                mostrarCargando(false)
            }
        }
    }

    private fun cambiarPassword() {
        val actual = binding.campoPasswordActual.text?.toString().orEmpty()
        val nueva = binding.campoPasswordNueva.text?.toString().orEmpty()
        val confirmar = binding.campoPasswordConfirmar.text?.toString().orEmpty()

        if (actual.isBlank() || nueva.isBlank() || confirmar.isBlank()) {
            mostrarError("Completa los tres campos de contraseña.")
            return
        }

        if (nueva.length < 8) {
            mostrarError("La nueva contraseña debe tener al menos 8 caracteres.")
            return
        }

        if (nueva != confirmar) {
            mostrarError("La confirmación de contraseña no coincide.")
            return
        }

        lifecycleScope.launch {
            mostrarCargando(true)

            try {
                val respuesta = withContext(Dispatchers.IO) {
                    api.cambiarPassword(
                        CambiarPasswordPeticion(
                            password_actual = actual,
                            password = nueva,
                            password_confirmation = confirmar
                        )
                    )
                }

                binding.campoPasswordActual.text?.clear()
                binding.campoPasswordNueva.text?.clear()
                binding.campoPasswordConfirmar.text?.clear()

                mostrarError(null)
                Toast.makeText(
                    this@PerfilActivity,
                    respuesta.message ?: "Contraseña actualizada.",
                    Toast.LENGTH_LONG
                ).show()

            } catch (e: Exception) {
                mostrarError(mensajeError(e))
            } finally {
                mostrarCargando(false)
            }
        }
    }

    private fun cerrarSesion() {
        lifecycleScope.launch {
            mostrarCargando(true)

            try {
                withContext(Dispatchers.IO) {
                    api.logout()
                }
            } catch (_: Exception) {
                // Aunque falle la petición, se limpiará la sesión local.
            } finally {
                sesion.cerrar()
                mostrarCargando(false)
                mostrarEstadoSesion()

                Toast.makeText(
                    this@PerfilActivity,
                    "Sesión cerrada.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun mostrarCargando(cargando: Boolean) {
        binding.progresoPerfil.visibility =
            if (cargando) View.VISIBLE else View.GONE

        binding.botonGuardarPerfil.isEnabled = !cargando
        binding.botonCambiarPassword.isEnabled = !cargando
        binding.botonCerrarSesion.isEnabled = !cargando
    }

    private fun mostrarError(mensaje: String?) {
        binding.textoError.text = mensaje.orEmpty()
        binding.textoError.visibility =
            if (mensaje.isNullOrBlank()) View.GONE else View.VISIBLE
    }

    private fun mensajeError(error: Exception): String {
        return when (error) {
            is HttpException -> {
                when (error.code()) {
                    401 -> "Tu sesión expiró. Inicia sesión nuevamente."
                    422 -> "Verifica los datos ingresados. Algunos no son válidos."
                    else -> "Error del servidor (${error.code()})."
                }
            }

            is IOException ->
                "No se pudo conectar con el servidor. Revisa tu conexión."

            else -> error.message ?: "Ocurrió un error inesperado."
        }
    }
}