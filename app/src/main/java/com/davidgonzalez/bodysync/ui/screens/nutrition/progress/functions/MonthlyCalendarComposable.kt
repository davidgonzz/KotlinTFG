package com.davidgonzalez.bodysync.ui.screens.nutrition.progress.functions

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@Composable
fun MonthlyCalendarComposable(caloriasPorDia: Map<String, Int>) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val hoy = LocalDate.now()
    var selectedDate by remember { mutableStateOf(hoy) }

    val primerDiaMes = currentMonth.atDay(1)
    val diasEnMes = currentMonth.lengthOfMonth()
    val primerDiaSemana = primerDiaMes.dayOfWeek.value - 1

    val totalCeldas = ((primerDiaSemana + diasEnMes + 6) / 7) * 7

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val displayFormatter = DateTimeFormatter.ofPattern("d/M/yyyy")

    val nombreMes = currentMonth.month
        .getDisplayName(TextStyle.FULL, Locale("es"))
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("es")) else it.toString() }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(imageVector = Icons.Default.ArrowBackIos, contentDescription = "Mes anterior")
            }

            Text(
                text = "$nombreMes ${currentMonth.year}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(imageVector = Icons.Default.ArrowForwardIos, contentDescription = "Mes siguiente")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            listOf("L", "M", "X", "J", "V", "S", "D").forEach {
                Text(it, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        for (fila in 0 until totalCeldas / 7) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                for (col in 0..6) {
                    val celda = fila * 7 + col
                    val diaDelMes = celda - primerDiaSemana + 1
                    val fecha = try {
                        currentMonth.atDay(diaDelMes)
                    } catch (e: Exception) {
                        null
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (fecha != null && diaDelMes in 1..diasEnMes) {
                            val fechaStr = fecha.format(formatter)
                            val kcal = caloriasPorDia[fechaStr] ?: 0
                            val esHoy = fecha == hoy
                            val isSelected = fecha == selectedDate

                            val animatedBg by animateColorAsState(
                                targetValue = when {
                                    isSelected -> Color(0xFFD0E9D0)
                                    esHoy -> Color(0xFFB8D8B2)
                                    else -> Color.Transparent
                                },
                                label = "bgColor"
                            )

                            val animatedBorder by animateColorAsState(
                                targetValue = when {
                                    isSelected -> Color(0xFF4E8A3E)
                                    esHoy -> Color(0xFF2C5704)
                                    else -> Color.Transparent
                                },
                                label = "borderColor"
                            )

                            val animatedTextColor by animateColorAsState(
                                targetValue = when {
                                    isSelected -> Color(0xFF4E8A3E)
                                    esHoy -> Color(0xFF2C5704)
                                    else -> Color.Black
                                },
                                label = "textColor"
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(animatedBg, shape = CircleShape)
                                        .border(1.dp, animatedBorder, shape = CircleShape)
                                        .clickable { selectedDate = fecha },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = diaDelMes.toString(),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = animatedTextColor
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val fechaSeleccionadaStr = selectedDate.format(formatter)
        val caloriasSeleccionadas = caloriasPorDia[fechaSeleccionadaStr]

        if (selectedDate.isAfter(hoy)) {
            Text(
                text = "Datos todavía no disponibles",
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        } else {
            if (caloriasSeleccionadas != null) {
                Text(
                    text = "Total consumido el ${selectedDate.format(displayFormatter)}: $caloriasSeleccionadas kcal",
                    fontWeight = FontWeight.Medium
                )
            } else {
                Text(
                    text = "Sin datos registrados para el ${selectedDate.format(displayFormatter)}",
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
