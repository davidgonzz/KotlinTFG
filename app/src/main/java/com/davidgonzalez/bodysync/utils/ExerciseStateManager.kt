package com.davidgonzalez.bodysync.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import java.time.Instant
import java.time.temporal.ChronoUnit

data class ExerciseState(
    val seriesRestantes: Int,
    val completado: Boolean,
    val ultimaModificacion: Long // Epoch en milisegundos
)

class ExerciseStateManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("gym_exercise_states", Context.MODE_PRIVATE)
    private val gson = Gson()

    /**
     * Devuelve el estado actual de un ejercicio. Si han pasado más de 24 horas desde la última
     * modificación, lo reinicia (series por defecto y completado = false).
     */
    fun getEstado(nombreEjercicio: String, seriesDefault: Int): ExerciseState {
        val json = prefs.getString(nombreEjercicio, null)
        val ahora = Instant.now().toEpochMilli()

        return if (json != null) {
            val estado = gson.fromJson(json, ExerciseState::class.java)

            // Si han pasado más de 24 horas, se reinicia
            val ultima = Instant.ofEpochMilli(estado.ultimaModificacion)
            if (ultima.plus(24, ChronoUnit.HOURS).isBefore(Instant.now())) {
                val reiniciado = ExerciseState(seriesDefault, false, ahora)
                guardarEstado(nombreEjercicio, reiniciado)
                reiniciado
            } else {
                estado
            }
        } else {
            // No había estado guardado, se crea uno nuevo
            val nuevo = ExerciseState(seriesDefault, false, ahora)
            guardarEstado(nombreEjercicio, nuevo)
            nuevo
        }
    }

    /**
     * Guarda o actualiza el estado de un ejercicio concreto.
     */
    fun guardarEstado(nombreEjercicio: String, estado: ExerciseState) {
        val json = gson.toJson(estado)
        prefs.edit().putString(nombreEjercicio, json).apply()
    }

    /**
     * Actualiza solo las series y el estado completado. Reinicia también la fecha de modificación.
     */
    fun actualizarSeriesYCompletado(nombreEjercicio: String, seriesRestantes: Int, completado: Boolean) {
        val nuevoEstado = ExerciseState(
            seriesRestantes = seriesRestantes,
            completado = completado,
            ultimaModificacion = Instant.now().toEpochMilli()
        )
        guardarEstado(nombreEjercicio, nuevoEstado)
    }
}
