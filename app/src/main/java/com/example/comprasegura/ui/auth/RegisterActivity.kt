package com.example.comprasegura.ui.auth

import android.content.Intent
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
import com.example.comprasegura.MainActivity
import com.example.comprasegura.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch
import android.widget.Toast
import com.example.comprasegura.util.PasswordGenerator
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom)
            insets
        }

        // Botón volver al catálogo
        binding.botonVolver.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        // Enlace a Login (si vino desde el login se regresa a el)
        binding.botonIrALogin.setOnClickListener {
            finish()
        }

        // Botón para registrarse
        binding.botonRegistrar.setOnClickListener {
            viewModel.registrar(
                nombre = binding.campoNombre.text.toString(),
                apellido = binding.campoApellido.text.toString(),
                correo = binding.campoCorreo.text.toString(),
                password = binding.campoPassword.text.toString(),
                confirmar = binding.campoConfirmar.text.toString(),
            )
        }

        // Botón para generar contraseña segura
        binding.botonGenerarPassword.setOnClickListener {
            val passwordGenerada = PasswordGenerator.generar()
            binding.campoPassword.setText(passwordGenerada)
            binding.campoConfirmar.setText(passwordGenerada)
            Toast.makeText(
                this,
                "Contraseña generada. Toca el ojo para verla.",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Observar el estado
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.estado.collect { estado ->
                    binding.progreso.visibility = if (estado.cargando) View.VISIBLE else View.GONE
                    binding.botonRegistrar.isEnabled = !estado.cargando
                    binding.textoError.text = estado.error
                    binding.textoError.visibility = if (estado.error != null) View.VISIBLE else View.GONE

                    // Registro exitoso → volvemos al catálogo ya logueados
                    if (estado.exito) {
                        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }
}