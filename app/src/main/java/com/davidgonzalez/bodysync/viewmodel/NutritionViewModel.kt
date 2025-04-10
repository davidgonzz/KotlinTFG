package com.davidgonzalez.bodysync.viewmodel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class NutritionViewModel : ViewModel() {

    // Estados para la comida nueva
    private val _tipoSeleccionado = MutableStateFlow("Desayuno")
    val tipoSeleccionado: StateFlow<String> = _tipoSeleccionado

    private val _nombreComida = MutableStateFlow("")
    val nombreComida: StateFlow<String> = _nombreComida

    private val _calorias = MutableStateFlow("")
    val calorias: StateFlow<String> = _calorias
    private val _comidas = MutableStateFlow<List<Pair<String, Int>>>(emptyList())
    val comidas: StateFlow<List<Pair<String, Int>>> = _comidas

    val caloriasConsumidas: StateFlow<Int> = _comidas.map { lista ->
        lista.sumOf { it.second }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    // Actualizar campos
    fun actualizarNombreComida(valor: String) {
        _nombreComida.value = valor
    }

    fun actualizarCalorias(valor: String) {
        _calorias.value = valor
    }

    fun actualizarTipoSeleccionado(valor: String) {
        _tipoSeleccionado.value = valor
    }

    fun añadirComida(onSuccess: () -> Unit = {}) {
        val nombre = _nombreComida.value
        val kcal = _calorias.value.toIntOrNull() ?: 0
        val tipo = _tipoSeleccionado.value

        if (nombre.isNotBlank() && kcal > 0) {
            _comidas.value = _comidas.value + (tipo to kcal)
            limpiarCampos()
            onSuccess()
        }
    }

    private fun limpiarCampos() {
        _nombreComida.value = ""
        _calorias.value = ""
        _tipoSeleccionado.value = "Desayuno"
    }

    // Dropdown tipo comida
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
                }
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
