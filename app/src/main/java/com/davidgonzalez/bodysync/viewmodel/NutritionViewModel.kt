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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

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
            _comidas.value = _comidas.value + Triple(nombre, kcal, tipo)
            _nombreComida.value = ""
            _calorias.value = ""

            resumenPorTipo.value = resumenPorTipo.value.toMutableMap().apply {
                this[tipo] = (this[tipo] ?: 0) + kcal
            }

            onSuccess()
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
}