package com.davidgonzalez.bodysync.ui.model

data class Ejercicio(
    val nombre: String,
    val grupoMuscular: String,
    val series: String,
    val descanso: Int,
    val gifUrl: String,
    val completado: Boolean = false,
    val seriesRestantes: Int = extraerSeries(series)
)

fun extraerSeries(series: String): Int {
    return series.split("x").firstOrNull()?.toIntOrNull() ?: 3
}

