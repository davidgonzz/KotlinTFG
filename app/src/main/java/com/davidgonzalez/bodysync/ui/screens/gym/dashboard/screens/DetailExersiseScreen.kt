package com.davidgonzalez.bodysync.ui.screens.gym.dashboard.screens

import android.os.CountDownTimer
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.davidgonzalez.bodysync.utils.ExerciseState
import com.davidgonzalez.bodysync.utils.ExerciseStateManager
import com.davidgonzalez.bodysync.viewmodel.GymViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailExerciseScreen(
    nombre: String,
    viewModel: GymViewModel = viewModel(),
    navController: NavHostController
) {
    val ejercicio = viewModel.ejercicios.collectAsState().value.find { it.nombre == nombre }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        if (ejercicio != null) {
            val context = LocalContext.current
            val manager = remember { ExerciseStateManager(context) }

            val estadoInicial = remember {
                manager.getEstado(
                    ejercicio.nombre,
                    ejercicio.series.split("x").firstOrNull()?.toIntOrNull() ?: 3
                )
            }

            var seriesRestantes by remember { mutableStateOf(estadoInicial.seriesRestantes) }
            var completado by remember { mutableStateOf(estadoInicial.completado) }
            var tiempoRestante by remember { mutableStateOf(ejercicio.descanso) }
            var temporizadorActivo by remember { mutableStateOf(false) }

            LaunchedEffect(temporizadorActivo, tiempoRestante) {
                if (temporizadorActivo && tiempoRestante > 0) {
                    kotlinx.coroutines.delay(1000L)
                    tiempoRestante--
                } else if (temporizadorActivo && tiempoRestante == 0) {
                    temporizadorActivo = false // solo al terminar el descanso se puede volver a usar
                    tiempoRestante = ejercicio.descanso // vuelve a poner el tiempo base
                }
            }



            val repeticiones = ejercicio.series.split("x").getOrNull(1) ?: "?"

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = ejercicio.nombre,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF065F46),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                AsyncImage(
                    model = ejercicio.gifUrl,
                    contentDescription = ejercicio.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón Series
                    Button(
                        onClick = {
                            if (ejercicio.seriesRestantes > 0 && !temporizadorActivo) {
                                viewModel.reducirSeries(ejercicio.nombre)
                                temporizadorActivo = true
                                tiempoRestante = ejercicio.descanso
                            }
                        },
                        enabled = ejercicio.seriesRestantes > 0 && !temporizadorActivo,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (ejercicio.seriesRestantes > 0) Color(0xFFF97316) else Color(
                                0xFFBDBDBD
                            )
                        ),
                        modifier = Modifier
                            .height(54.dp)
                            .defaultMinSize(minWidth = 140.dp)
                    ) {
                        Text(
                            if (ejercicio.seriesRestantes > 0)
                                "Series: ${ejercicio.seriesRestantes}x$repeticiones"
                            else "Terminado",
                            color = Color.White
                        )
                    }


                    // Botón Descanso
                    Button(
                        onClick = { /* ya no hace nada si está en marcha */ },
                        enabled = !temporizadorActivo && ejercicio.seriesRestantes > 0,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (temporizadorActivo) Color(0xFF9E9E9E) else Color(
                                0xFFF97316
                            )
                        ),
                        modifier = Modifier
                            .height(54.dp)
                            .defaultMinSize(minWidth = 140.dp)
                    ) {
                        Text(
                            if (temporizadorActivo) "Descanso: ${tiempoRestante}s" else "Descansar",
                            color = Color.White
                        )
                    }
                }

                    Spacer(modifier = Modifier.height(32.dp))

                AnimatedVisibility(
                    visible = seriesRestantes == 0,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completado",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(64.dp)
                        )
                        Text("¡Ejercicio completado!", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF2C5704))
            }
        }
    }
}
