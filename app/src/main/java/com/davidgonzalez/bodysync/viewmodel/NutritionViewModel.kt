package com.davidgonzalez.bodysync.viewmodel

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidgonzalez.bodysync.network.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.roundToInt

class NutritionViewModel : ViewModel() {

    private val _tipoSeleccionado = MutableStateFlow("Desayuno")
    val tipoSeleccionado: StateFlow<String> = _tipoSeleccionado

    private val _nombreComida = MutableStateFlow("")
    val nombreComida: StateFlow<String> = _nombreComida

    private val _calorias = MutableStateFlow("")
    val calorias: StateFlow<String> = _calorias

    private val _comidas = MutableStateFlow<List<Triple<String, Int, String>>>(emptyList())
    val comidas: StateFlow<List<Triple<String, Int, String>>> = _comidas

    val caloriasConsumidas: StateFlow<Int> = _comidas.map { it.sumOf { it.second } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val resumenPorTipo = MutableStateFlow(
        mutableMapOf("Desayuno" to 0, "Comida" to 0, "Cena" to 0, "Snack" to 0)
    )

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _nombreUsuario = MutableStateFlow("")
    val nombreUsuario: StateFlow<String> = _nombreUsuario

    private val _progresoSemanal = MutableStateFlow<List<Int>>(emptyList())
    val progresoSemanal: StateFlow<List<Int>> = _progresoSemanal

    private val _caloriasObjetivo = MutableStateFlow(2000)
    val caloriasObjetivo: StateFlow<Int> = _caloriasObjetivo

    private val _caloriasPorDia = MutableStateFlow<Map<String, Int>>(emptyMap())
    val caloriasPorDia: StateFlow<Map<String, Int>> = _caloriasPorDia


    fun calcularCaloriasDesdeDatosUsuario() {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("usuarios").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val peso = doc.getString("peso")?.toDoubleOrNull() ?: 70.0
                    val altura = doc.getString("altura")?.toDoubleOrNull() ?: 170.0
                    val edad = doc.getString("edad")?.toIntOrNull() ?: 25
                    val sexo = doc.getString("genero") ?: "masculino"
                    val objetivo = doc.getString("objetivo") ?: "mantener"
                    val actividad = doc.getString("actividad") ?: "moderada"

                    val tmb = when (sexo.lowercase()) {
                        "masculino" -> 88.36 + (13.4 * peso) + (4.8 * altura) - (5.7 * edad)
                        "femenino" -> 447.6 + (9.2 * peso) + (3.1 * altura) - (4.3 * edad)
                        else -> 88.36 + (13.4 * peso) + (4.8 * altura) - (5.7 * edad)
                    }

                    val factorActividad = when (actividad.lowercase()) {
                        "sedentaria" -> 1.2
                        "ligera" -> 1.375
                        "moderada" -> 1.55
                        "intensa" -> 1.725
                        else -> 1.55
                    }

                    var calorias = tmb * factorActividad

                    calorias += when (objetivo.lowercase()) {
                        "definir" -> -400
                        "volumen", "engordar" -> 400
                        else -> 0
                    }

                    _caloriasObjetivo.value = ((calorias / 100).roundToInt() * 100)
                }
            }
    }

    fun actualizarNombreComida(valor: String) {
        _nombreComida.value = valor
    }

    fun actualizarCalorias(valor: String) {
        _calorias.value = valor
    }

    fun actualizarTipoSeleccionado(valor: String) {
        _tipoSeleccionado.value = valor
    }

    fun añadirComida(onSuccess: () -> Unit) {
        val tipo = tipoSeleccionado.value
        val kcal = calorias.value.toIntOrNull() ?: 0
        val nombre = nombreComida.value

        if (nombre.isNotBlank() && kcal > 0) {
            val uid = auth.currentUser?.uid ?: return
            val fecha = LocalDate.now().toString()

            val comidaMap = hashMapOf(
                "nombre" to nombre,
                "calorias" to kcal,
                "tipo" to tipo,
                "fecha" to fecha
            )

            firestore.collection("usuarios")
                .document(uid)
                .collection("comidas")
                .add(comidaMap)
                .addOnSuccessListener {
                    _nombreComida.value = ""
                    _calorias.value = ""
                    obtenerComidasDeHoy()
                    onSuccess()
                }
        }
    }

    fun obtenerComidasDeHoy() {
        val uid = auth.currentUser?.uid ?: return
        val fechaHoy = LocalDate.now().toString()

        firestore.collection("usuarios")
            .document(uid)
            .collection("comidas")
            .whereEqualTo("fecha", fechaHoy)
            .get()
            .addOnSuccessListener { result ->
                val lista = mutableListOf<Triple<String, Int, String>>()
                val resumen = mutableMapOf("Desayuno" to 0, "Comida" to 0, "Cena" to 0, "Snack" to 0)

                for (doc in result) {
                    val nombre = doc.getString("nombre") ?: continue
                    val kcal = doc.getLong("calorias")?.toInt() ?: continue
                    val tipo = doc.getString("tipo") ?: "Desayuno"

                    lista.add(Triple(nombre, kcal, tipo))
                    resumen[tipo] = resumen[tipo]!! + kcal
                }

                _comidas.value = lista
                resumenPorTipo.value = resumen
            }
    }

    fun buscarAlimentoPorCodigo(
        codigo: String,
        gramos: Int,
        onResultado: (String, Int) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getProduct(codigo)

                val nombre = response.product?.product_name ?: "Producto desconocido"
                val kcalPor100g = response.product?.nutriments?.kcalPer100g ?: 0.0

                val kcalCalculadas = ((kcalPor100g * gramos) / 100).toInt()
                onResultado(nombre, kcalCalculadas)
            } catch (e: Exception) {
                Log.e("NutritionVM", "Error buscando código $codigo: ${e.message}")
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DropdownMenuTipo() {
        var expanded by remember { mutableStateOf(false) }
        val opciones = listOf("Desayuno", "Comida", "Cena", "Snack")

        Box {
            OutlinedTextField(
                value = tipoSeleccionado.collectAsState().value,
                onValueChange = {},
                label = { Text("Tipo de comida") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Expandir",
                        modifier = Modifier.clickable { expanded = !expanded }
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF2C5704),
                    cursorColor = Color(0xFF2C5704)
                )
            )

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            actualizarTipoSeleccionado(opcion)
                            expanded = false
                        }
                    )
                }
            }
        }
    }

    fun obtenerNombreUsuario() {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("usuarios").document(uid).get()
            .addOnSuccessListener { document ->
                _nombreUsuario.value = document.getString("nombre") ?: "Usuario"
            }
            .addOnFailureListener {
                _nombreUsuario.value = "Usuario"
            }
    }
    fun obtenerProgresoSemanal() {
        val uid = auth.currentUser?.uid ?: return
        val hoy = LocalDate.now()
        val fechasSemana = (0..6).map { hoy.minusDays(it.toLong()).toString() }.reversed()

        firestore.collection("usuarios")
            .document(uid)
            .collection("comidas")
            .whereIn("fecha", fechasSemana)
            .get()
            .addOnSuccessListener { result ->
                val mapa = mutableMapOf<String, Int>().apply {
                    fechasSemana.forEach { put(it, 0) }
                }

                for (doc in result) {
                    val fecha = doc.getString("fecha") ?: continue
                    val kcal = doc.getLong("calorias")?.toInt() ?: continue
                    mapa[fecha] = mapa.getOrDefault(fecha, 0) + kcal
                }

                _progresoSemanal.value = fechasSemana.map { mapa[it] ?: 0 }
            }
    }

    fun obtenerCaloriasDelMes() {
        val uid = auth.currentUser?.uid ?: return
        val hoy = LocalDate.now()
        val mesActual = hoy.monthValue
        val añoActual = hoy.year

        firestore.collection("usuarios")
            .document(uid)
            .collection("comidas")
            .get()
            .addOnSuccessListener { result ->
                val mapa = mutableMapOf<String, Int>()

                for (doc in result) {
                    val fechaStr = doc.getString("fecha") ?: continue
                    val kcal = doc.getLong("calorias")?.toInt() ?: 0

                    val fecha = try {
                        LocalDate.parse(fechaStr)
                    } catch (e: Exception) {
                        continue
                    }

                    if (fecha.monthValue == mesActual && fecha.year == añoActual) {
                        mapa[fechaStr] = (mapa[fechaStr] ?: 0) + kcal
                    }
                }

                _caloriasPorDia.value = mapa
            }
    }

}
