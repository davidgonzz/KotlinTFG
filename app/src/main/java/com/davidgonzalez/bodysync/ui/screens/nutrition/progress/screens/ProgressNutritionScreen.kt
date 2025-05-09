package com.davidgonzalez.bodysync.ui.screens.nutrition.progress.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.davidgonzalez.bodysync.ui.screens.nutrition.dashboard.functions.BottomNavigationBar
import com.davidgonzalez.bodysync.ui.screens.nutrition.progress.functions.BarChartComposable
import com.davidgonzalez.bodysync.ui.screens.nutrition.progress.functions.MonthlyCalendarComposable
import com.davidgonzalez.bodysync.viewmodel.NutritionViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.roundToInt

@Composable
fun ProgressNutritionScreen(
    navController: NavHostController,
    viewModel: NutritionViewModel = viewModel()
) {
    val nombreUsuario by viewModel.nombreUsuario.collectAsState()
    val caloriasConsumidas by viewModel.caloriasConsumidas.collectAsState()
    val caloriasObjetivo by viewModel.caloriasObjetivo.collectAsState()
    val progresoSemanal by viewModel.progresoSemanal.collectAsState()
    val caloriasPorDia by viewModel.caloriasPorDia.collectAsState()
    var expandedMenu by remember { mutableStateOf(false) }

    var isWeekly by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.obtenerNombreUsuario()
        viewModel.obtenerComidasDeHoy()
        viewModel.obtenerProgresoSemanal()
        viewModel.calcularCaloriasDesdeDatosUsuario()
    }

    LaunchedEffect(isWeekly) {
        if (!isWeekly) {
            viewModel.obtenerCaloriasDelMes()
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(selectedItem = "progreso", navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = nombreUsuario,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Box {
                    IconButton(onClick = { expandedMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Menú usuario",
                            tint = Color(0xFF2C5704)
                        )
                    }

                    DropdownMenu(
                        expanded = expandedMenu,
                        onDismissRequest = { expandedMenu = false },
                        modifier = Modifier.align(Alignment.TopEnd),
                        offset = DpOffset(x = 0.dp, y = 0.dp)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Cerrar sesión") },
                            onClick = {
                                expandedMenu = false
                                FirebaseAuth.getInstance().signOut()
                                navController.navigate("login") {
                                    popUpTo("dashboard") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Progreso nutricional",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFFF0F5EC))
                    .padding(4.dp)
            ) {
                listOf("Semana", "Mes").forEachIndexed { index, label ->
                    val selected = (index == 0 && isWeekly) || (index == 1 && !isWeekly)
                    TextButton(
                        onClick = { isWeekly = (index == 0) },
                        modifier = Modifier
                            .background(
                                if (selected) Color(0xFFB8D8B2) else Color.Transparent,
                                shape = CircleShape
                            )
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text(label, color = Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val porcentaje = (caloriasConsumidas.toFloat() / caloriasObjetivo * 100).coerceIn(0f, 100f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE6E6E6))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(porcentaje / 100f)
                        .background(Color(0xFF7DA86C))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "$caloriasConsumidas kcal consumidas de $caloriasObjetivo kcal",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${porcentaje.roundToInt()} % de tu objetivo",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isWeekly) {
                BarChartComposable(data = progresoSemanal)

                Spacer(modifier = Modifier.height(24.dp))

                Surface(
                    tonalElevation = 1.dp,
                    shape = MaterialTheme.shapes.medium,
                    color = Color(0xFFF8FBF7),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val dias = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
                        val data = progresoSemanal
                        val diaMax = dias.getOrElse(data.indexOf(data.maxOrNull() ?: 0)) { "-" }
                        val diaMin = dias.getOrElse(data.indexOf(data.minOrNull() ?: 0)) { "-" }

                        Text("• Día con más calorías: ", fontSize = 14.sp)
                        Text(text = "  $diaMax", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("• Día con menos calorías: ", fontSize = 14.sp)
                        Text(text = "  $diaMin", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            } else {
                MonthlyCalendarComposable(caloriasPorDia = caloriasPorDia)
            }
        }
    }
}
