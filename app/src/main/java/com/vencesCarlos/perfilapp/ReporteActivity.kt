package com.vencesCarlos.perfilapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vencesCarlos.perfilapp.databinding.ActivityReporteBinding

class ReporteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReporteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReporteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mostrarDatos()
        configurarBotonVolver()
    }

    private fun mostrarDatos() {
        binding.tvNombresValor.text =
            intent.getStringExtra(MainActivity.EXTRA_NOMBRES).orEmpty()

        binding.tvApellidosValor.text =
            intent.getStringExtra(MainActivity.EXTRA_APELLIDOS).orEmpty()

        binding.tvFechaNacimientoValor.text =
            intent.getStringExtra(MainActivity.EXTRA_FECHA_NACIMIENTO).orEmpty()

        binding.tvGeneroValor.text =
            intent.getStringExtra(MainActivity.EXTRA_GENERO).orEmpty()

        binding.tvTelefonoValor.text =
            intent.getStringExtra(MainActivity.EXTRA_TELEFONO).orEmpty()

        binding.tvCorreoValor.text =
            intent.getStringExtra(MainActivity.EXTRA_CORREO).orEmpty()

        binding.tvInteresesValor.text =
            intent.getStringExtra(MainActivity.EXTRA_INTERESES).orEmpty()

        binding.tvDescripcionValor.text =
            intent.getStringExtra(MainActivity.EXTRA_DESCRIPCION).orEmpty()
    }

    private fun configurarBotonVolver() {
        binding.btnVolver.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}