package com.davidgonzalez.bodysync.ui.screens.choose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.davidgonzalez.bodysync.ui.theme.BodySyncTheme

class ChooseScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChooseScreenUI()
        }
    }
}

@Composable
fun ChooseScreenUI() {
    BodySyncTheme {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Aquí simula un h4 usando Text con un tamaño grande
                Text(
                    text = "Elige entre Nutrición o Gimnasio",
                    style = TextStyle(
                        fontSize = 32.sp, // Tamaño de fuente grande
                        fontWeight = FontWeight.Bold, // Peso de la fuente
                        color = MaterialTheme.colorScheme.primary // Color primario
                    ),
                    modifier = Modifier.padding(bottom = 32.dp) // Espacio debajo del título
                )

                // Aquí irían los botones para elegir entre nutrición o gimnasio
                Button(onClick = { /* Acción para elegir nutrición */ }) {
                    Text("Nutrición")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { /* Acción para elegir gimnasio */ }) {
                    Text("Gimnasio")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChooseScreenPreview() {
    ChooseScreenUI()
}
