package com.davidgonzalez.bodysync.ui.screens.nutrition.progress.functions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import java.util.Locale
import java.time.format.TextStyle
import java.util.*

@Composable
fun MonthlyCalendarComposable(caloriasPorDia: Map<String, Int>) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val hoy = LocalDate.now()

    val primerDiaMes = currentMonth.atDay(1)
    val diasEnMes = currentMonth.lengthOfMonth()
    val primerDiaSemana = primerDiaMes.dayOfWeek.value % 7
    val totalCeldas = ((primerDiaSemana + diasEnMes + 6) / 7) * 7

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val nombreMes = currentMonth.month
        .getDisplayName(TextStyle.FULL, Locale("es"))
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("es")) else it.toString() }

    Column {
        // Encabezado con mes y navegación
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

        // Días de la semana
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

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(
                                            if (esHoy) Color(0xFFB8D8B2) else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = if (esHoy) Color(0xFF2C5704) else Color.Transparent,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = diaDelMes.toString(),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (esHoy) Color(0xFF2C5704) else Color.Black
                                    )
                                }

                                if (kcal > 0) {
                                    Text(
                                        text = "$kcal",
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
