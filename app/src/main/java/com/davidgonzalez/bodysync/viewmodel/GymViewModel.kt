package com.davidgonzalez.bodysync.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.davidgonzalez.bodysync.ui.model.Ejercicio
import com.davidgonzalez.bodysync.utils.ExerciseStateManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GymViewModel(application: Application) : AndroidViewModel(application) {

    private val _ejercicios = MutableStateFlow<List<Ejercicio>>(emptyList())
    val ejercicios: StateFlow<List<Ejercicio>> = _ejercicios

    private val _nombreUsuario = MutableStateFlow("Usuario")
    val nombreUsuario: StateFlow<String> = _nombreUsuario

    private val storageRef = Firebase.storage.reference
    private val stateManager = ExerciseStateManager(application.applicationContext)

    private val gruposMusculares = listOf("pecho", "espalda", "hombro", "pierna", "biceps", "triceps")

    init {
        cargarEjerciciosDesdeStorage()
    }

    private fun cargarEjerciciosDesdeStorage() {
        viewModelScope.launch {
            val listaFinal = mutableListOf<Ejercicio>()

            val configuraciones = mapOf(
                // Ejemplos: asegúrate de que los nombres coincidan con los archivos
                "press plano" to ("3x12" to 60),
                "press inclinado" to ("3x10" to 75),
                "cruce poleas" to ("3x15" to 60),
                "dumbbell pullover" to ("3x12" to 60),
                "aperturas banco plano" to ("3x15" to 45),
                "peck deck" to ("3x12" to 60),
                "jalon pecho" to ("4x12" to 75),
                "remo barra" to ("4x10" to 90),
                "remo mancuernas" to ("3x12" to 60),
                "face pull" to ("3x15" to 45),
                "elevaciones frontales" to ("3x15" to 45),
                "elevaciones laterales" to ("3x15" to 45),
                "press militar barra" to ("4x8" to 90),
                "press militar mancuernas" to ("4x8" to 90),
                "peso muerto" to ("5x5" to 120),
                "sentadilla barra" to ("5x5" to 120),
                "curl martillo" to ("3x15" to 45),
                "curl alternado" to ("3x15" to 45),
                "curl polea baja trenza" to ("3x12" to 60),
                "press frances" to ("3x10" to 60),
                "triceps polea alta" to ("3x12" to 60),
                // ...añade todos los demás
            )

            gruposMusculares.forEach { grupo ->
                val carpetaRef = storageRef.child(grupo)

                carpetaRef.listAll()
                    .addOnSuccessListener { resultado ->
                        resultado.items.forEach { archivo ->
                            archivo.downloadUrl.addOnSuccessListener { uri ->
                                val nombre = archivo.name
                                    .removeSuffix(".gif")
                                    .replace("_", " ")
                                    .replaceFirstChar { it.uppercase() }

                                val clave = nombre.lowercase().trim()
                                val (series, descanso) = configuraciones[clave] ?: ("3x12" to 60)
                                val seriesTotales = series.split("x").getOrNull(0)?.toIntOrNull() ?: 3

                                val estado = stateManager.getEstado(clave, seriesTotales)

                                listaFinal.add(
                                    Ejercicio(
                                        nombre = nombre,
                                        grupoMuscular = grupo,
                                        series = series,
                                        descanso = descanso,
                                        gifUrl = uri.toString(),
                                        completado = estado.completado,
                                        seriesRestantes = estado.seriesRestantes
                                    )
                                )

                                _ejercicios.value = listaFinal.sortedBy { it.grupoMuscular }
                            }
                        }
                    }
                    .addOnFailureListener {
                        Log.e("GIMNASIO", "Error cargando carpeta $grupo: ${it.message}")
                    }
            }
        }
    }

    fun obtenerNombreUsuario() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                _nombreUsuario.value = document.getString("nombre") ?: "Usuario"
            }
            .addOnFailureListener {
                _nombreUsuario.value = "Usuario"
            }
    }

    fun toggleCompletado(nombre: String) {
        val clave = nombre.lowercase().trim()
        _ejercicios.value.find { it.nombre == nombre }?.let { ejercicio ->
            val nuevoEstado = !ejercicio.completado
            val actualizado = ejercicio.copy(completado = nuevoEstado)
            _ejercicios.value = _ejercicios.value.map {
                if (it.nombre == nombre) actualizado else it
            }
            stateManager.guardarEstado(clave, com.davidgonzalez.bodysync.utils.ExerciseState(
                ejercicio.seriesRestantes,
                nuevoEstado,
                System.currentTimeMillis()
            ))
        }
    }
    fun reducirSeries(nombre: String) {
        val clave = nombre.lowercase().trim()
        val actual = _ejercicios.value.find { it.nombre == nombre } ?: return
        val nuevasSeries = (actual.seriesRestantes - 1).coerceAtLeast(0)
        val completado = nuevasSeries == 0
        val actualizado = actual.copy(
            seriesRestantes = nuevasSeries,
            completado = completado
        )
        _ejercicios.value = _ejercicios.value.map {
            if (it.nombre == nombre) actualizado else it
        }
        stateManager.guardarEstado(clave, com.davidgonzalez.bodysync.utils.ExerciseState(nuevasSeries, completado, System.currentTimeMillis()))
    }
}
