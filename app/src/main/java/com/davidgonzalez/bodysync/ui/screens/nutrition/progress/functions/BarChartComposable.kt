package com.davidgonzalez.bodysync.ui.screens.nutrition.progress.functions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun BarChartComposable(data: List<Int>) {
    val max = data.maxOrNull()?.takeIf { it > 0 } ?: 1

    val today = LocalDate.now()
    val dias = (6 downTo 0).map {
        today.minusDays(it.toLong()).dayOfWeek
            .getDisplayName(TextStyle.SHORT, Locale("es"))
            .replaceFirstChar { it.uppercase() }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(140.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        data.forEachIndexed { index, valor ->
            val altura = (valor.toFloat() / max) * 100f

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Box(
                    modifier = Modifier
                        .width(14.dp)
                        .height(altura.dp)
                        .background(Color(0xFFA4D7A7), shape = RoundedCornerShape(6.dp))
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = dias.getOrElse(index) { "" },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF444444)
                )
            }
        }
    }
}
