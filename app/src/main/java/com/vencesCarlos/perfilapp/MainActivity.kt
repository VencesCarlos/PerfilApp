package com.vencesCarlos.perfilapp

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Patterns
import android.view.TouchDelegate
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.vencesCarlos.perfilapp.databinding.ActivityMainBinding
import java.time.LocalDate
import java.time.Period
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var selectedDay = 0
    private var selectedMonth = 0
    private var selectedYear = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarSwitchIdioma()
        configurarAreaTactilSwitch()

        binding.etFechaNacimiento.setOnClickListener {
            mostrarDatePicker()
        }

        binding.btnCrearPerfil.setOnClickListener {
            validarYContinuar()
        }
    }

    private fun mostrarDatePicker() {
        val calendar = Calendar.getInstance()

        val year = if (selectedYear != 0) selectedYear else calendar.get(Calendar.YEAR) - 13
        val month = if (selectedYear != 0) selectedMonth else 0
        val day = if (selectedYear != 0) selectedDay else 1

        val datePickerDialog = DatePickerDialog(
            this,
            { _, anio, mes, dia ->
                selectedYear = anio
                selectedMonth = mes
                selectedDay = dia

                val fechaFormateada = String.format(
                    Locale.getDefault(),
                    "%02d/%02d/%04d",
                    dia,
                    mes + 1,
                    anio
                )

                binding.etFechaNacimiento.setText(fechaFormateada)
                binding.etFechaNacimiento.error = null
            },
            year,
            month,
            day
        )

        datePickerDialog.datePicker.maxDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    private fun validarYContinuar() {
        limpiarErrores()

        val nombres = binding.etNombres.text.toString().trim()
        val apellidos = binding.etApellidos.text.toString().trim()
        val fechaNacimiento = binding.etFechaNacimiento.text.toString().trim()
        val generoSeleccionado = binding.spGenero.selectedItemPosition
        val genero = if (generoSeleccionado > 0) binding.spGenero.selectedItem.toString() else ""
        val clavePais = binding.etClavePais.text.toString().trim()
        val telefono = binding.etTelefono.text.toString().trim()
        val correo = binding.etCorreo.text.toString().trim()
        val descripcion = binding.etDescripcion.text.toString().trim()

        var esValido = true

        if (nombres.isEmpty()) {
            binding.etNombres.error = getString(R.string.error_nombre_requerido)
            esValido = false
        }

        if (apellidos.isEmpty()) {
            binding.etApellidos.error = getString(R.string.error_apellidos_requeridos)
            esValido = false
        }

        if (fechaNacimiento.isEmpty()) {
            binding.etFechaNacimiento.error = getString(R.string.error_fecha_requerida)
            esValido = false
        } else if (!tieneAlMenos13Anios()) {
            Toast.makeText(
                this,
                getString(R.string.error_menor_edad),
                Toast.LENGTH_SHORT
            ).show()
            esValido = false
        }

        if (generoSeleccionado == 0) {
            Toast.makeText(
                this,
                getString(R.string.error_genero_requerido),
                Toast.LENGTH_SHORT
            ).show()
            binding.spGenero.requestFocus()
            esValido = false
        }

        if (clavePais.isEmpty()) {
            binding.etClavePais.error = getString(R.string.error_clave_pais_requerida)
            esValido = false
        } else if (!clavePais.matches(Regex("^\\+[0-9]{1,4}$"))) {
            binding.etClavePais.error = getString(R.string.error_clave_pais_invalida)
            esValido = false
        }

        if (telefono.isEmpty()) {
            binding.etTelefono.error = getString(R.string.error_telefono_requerido)
            esValido = false
        } else if (clavePais == "+52") {
            if (!telefono.matches(Regex("^[0-9]{10}$"))) {
                binding.etTelefono.error = getString(R.string.error_telefono_invalido_mexico)
                esValido = false
            }
        } else {
            if (!telefono.matches(Regex("^[0-9]{8,15}$"))) {
                binding.etTelefono.error = getString(R.string.error_telefono_invalido_general)
                esValido = false
            }
        }

        if (correo.isEmpty()) {
            binding.etCorreo.error = getString(R.string.error_correo_requerido)
            esValido = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            binding.etCorreo.error = getString(R.string.error_correo_invalido)
            esValido = false
        }

        if (!esValido) {
            return
        }

        val interesesSeleccionados = obtenerInteresesSeleccionados()
        val interesesTexto = if (interesesSeleccionados.isEmpty()) {
            getString(R.string.sin_intereses)
        } else {
            interesesSeleccionados.joinToString(getString(R.string.separador_lista))
        }

        val descripcionTexto = if (descripcion.isEmpty()) {
            getString(R.string.sin_descripcion)
        } else {
            descripcion
        }

        val intent = Intent(this, ReporteActivity::class.java).apply {
            putExtra(EXTRA_NOMBRES, nombres)
            putExtra(EXTRA_APELLIDOS, apellidos)
            putExtra(EXTRA_FECHA_NACIMIENTO, fechaNacimiento)
            putExtra(EXTRA_GENERO, genero)
            putExtra(EXTRA_TELEFONO, "$clavePais $telefono")
            putExtra(EXTRA_CORREO, correo)
            putExtra(EXTRA_INTERESES, interesesTexto)
            putExtra(EXTRA_DESCRIPCION, descripcionTexto)
        }

        startActivity(intent)
    }

    private fun limpiarErrores() {
        binding.etNombres.error = null
        binding.etApellidos.error = null
        binding.etFechaNacimiento.error = null
        binding.etClavePais.error = null
        binding.etTelefono.error = null
        binding.etCorreo.error = null
    }

    private fun tieneAlMenos13Anios(): Boolean {
        if (selectedYear == 0 && selectedMonth == 0 && selectedDay == 0) {
            return false
        }

        val fechaNacimiento = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
        val fechaActual = LocalDate.now()
        val edad = Period.between(fechaNacimiento, fechaActual).years

        return edad >= 13
    }

    private fun obtenerInteresesSeleccionados(): List<String> {
        val intereses = mutableListOf<String>()

        if (binding.cbMusica.isChecked) {
            intereses.add(binding.cbMusica.text.toString())
        }
        if (binding.cbDeportes.isChecked) {
            intereses.add(binding.cbDeportes.text.toString())
        }
        if (binding.cbLectura.isChecked) {
            intereses.add(binding.cbLectura.text.toString())
        }
        if (binding.cbViajes.isChecked) {
            intereses.add(binding.cbViajes.text.toString())
        }
        if (binding.cbTecnologia.isChecked) {
            intereses.add(binding.cbTecnologia.text.toString())
        }

        return intereses
    }

    private fun configurarSwitchIdioma() {
        val currentLocales = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        val currentTag = if (currentLocales.isNotEmpty()) {
            currentLocales
        } else {
            Locale.getDefault().toLanguageTag()
        }

        binding.switchIdioma.setOnCheckedChangeListener(null)
        binding.switchIdioma.isChecked = currentTag.startsWith(LANG_EN, ignoreCase = true)

        binding.switchIdioma.setOnCheckedChangeListener { _, isChecked ->
            val languageTag = if (isChecked) LANG_EN else LANG_ES
            cambiarIdioma(languageTag)
        }
    }

    private fun cambiarIdioma(languageTag: String) {
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(languageTag)
        )
    }

    private fun configurarAreaTactilSwitch() {
        val parent = binding.switchIdioma.parent as? View ?: return

        parent.post {
            val rect = Rect()
            binding.switchIdioma.getHitRect(rect)

            val extra = 60
            rect.top -= extra
            rect.bottom += extra
            rect.left -= extra
            rect.right += extra

            parent.touchDelegate = TouchDelegate(rect, binding.switchIdioma)
        }
    }

    companion object {
        const val EXTRA_NOMBRES = "extra_nombres"
        const val EXTRA_APELLIDOS = "extra_apellidos"
        const val EXTRA_FECHA_NACIMIENTO = "extra_fecha_nacimiento"
        const val EXTRA_GENERO = "extra_genero"
        const val EXTRA_TELEFONO = "extra_telefono"
        const val EXTRA_CORREO = "extra_correo"
        const val EXTRA_INTERESES = "extra_intereses"
        const val EXTRA_DESCRIPCION = "extra_descripcion"

        const val LANG_ES = "es"
        const val LANG_EN = "en"
    }
}