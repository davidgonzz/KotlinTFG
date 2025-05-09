package com.davidgonzalez.bodysync.ui.screens.nutrition.progress.functions

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight

@Composable
fun BarChartComposable(data: List<Int>) {
    val isMensual = data.size > 7
    val max = data.maxOrNull()?.takeIf { it > 0 } ?: 1

    val dias = if (isMensual) {
        (1..30).map { it.toString().padStart(2, '0') }
    } else {
        listOf("L", "M", "X", "J", "V", "S", "D")
    }

    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .horizontalScroll(if (isMensual) scrollState else rememberScrollState(0)),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        data.forEachIndexed { index, valor ->
            val altura = (valor.toFloat() / max) * 80f

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Box(
                    modifier = Modifier
                        .width(12.dp)
                        .height(altura.dp)
                        .background(Color(0xFFB8D8B2), shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dias.getOrElse(index) { "" },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF444444)
                )
            }
        }
    }
}
