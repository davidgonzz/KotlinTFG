package com.davidgonzalez.bodysync.ui.screens.nutrition.dashboard.functions

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CaloriasProgressCircle(caloriasConsumidas: Int, caloriasTotales: Int) {
    val porcentaje = if (caloriasTotales > 0) {
        caloriasConsumidas.toFloat() / caloriasTotales.toFloat()
    } else {
        0f
    }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(240.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = Color(0xFFB0E29E),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 24f)
            )
            drawArc(
                color = Color(0xFF187B1D),
                startAngle = -90f,
                sweepAngle = 360f * porcentaje,
                useCenter = false,
                style = Stroke(width = 24f, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$caloriasConsumidas kcal",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D2814)
            )
            Text(
                text = "de $caloriasTotales kcal",
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }
    }
}

