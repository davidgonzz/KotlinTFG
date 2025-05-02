package com.davidgonzalez.bodysync.viewmodel

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    val caloriasConsumidas: StateFlow<Int> = _comidas.map { lista ->
        lista.sumOf { it.second }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val resumenPorTipo = MutableStateFlow(
        mutableMapOf(
            "Desayuno" to 0,
            "Comida" to 0,
            "Cena" to 0,
            "Snack" to 0
        )
    )

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _nombreUsuario = MutableStateFlow("")
    val nombreUsuario: StateFlow<String> = _nombreUsuario

    private val _caloriasObjetivo = MutableStateFlow(2000)
    val caloriasObjetivo: StateFlow<Int> = _caloriasObjetivo

    fun calcularCaloriasDesdeDatosUsuario() {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("usuarios").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val pesoStr = document.getString("peso") ?: "70"
                    val alturaStr = document.getString("altura") ?: "170"
                    val edadStr = document.getString("edad") ?: "25"
                    val sexo = document.getString("genero") ?: "masculino"
                    val objetivo = document.getString("objetivo") ?: "mantener"
                    val actividad = document.getString("actividad") ?: "moderada"

                    val peso = pesoStr.toDoubleOrNull() ?: 70.0
                    val altura = alturaStr.toDoubleOrNull() ?: 170.0
                    val edad = edadStr.toIntOrNull() ?: 25

                    val tmb = when (sexo.lowercase()) {
                        "masculino" -> 88.36 + (13.4 * peso) + (4.8 * altura) - (5.7 * edad)
                        "femenino"  -> 447.6 + (9.2 * peso) + (3.1 * altura) - (4.3 * edad)
                        else -> 88.36 + (13.4 * peso) + (4.8 * altura) - (5.7 * edad)
                    }

                    val factorActividad = when (actividad.lowercase()) {
                        "sedentaria" -> 1.2
                        "ligera"     -> 1.375
                        "moderada"   -> 1.55
                        "intensa"    -> 1.725
                        else         -> 1.55
                    }

                    var calorias = tmb * factorActividad

                    calorias += when (objetivo.lowercase()) {
                        "definir"  -> -400
                        "volumen"  -> 400
                        "engordar" -> 400
                        else       -> 0
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
                val resumen = mutableMapOf(
                    "Desayuno" to 0,
                    "Comida" to 0,
                    "Cena" to 0,
                    "Snack" to 0
                )

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
                if (document.exists()) {
                    val nombre = document.getString("nombre") ?: "Usuario"
                    _nombreUsuario.value = nombre
                }
            }
            .addOnFailureListener {
                _nombreUsuario.value = "Usuario"
            }
    }

    fun añadirComidaConCodigo(codigo: String) {
        val nombreComida = "Producto escaneado: $codigo"
        val calorias = 100
        _comidas.value = _comidas.value + Triple(nombreComida, calorias, tipoSeleccionado.value)
    }
}
