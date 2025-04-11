package com.davidgonzalez.bodysync.ui.screens.nutrition.dashboard.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.davidgonzalez.bodysync.R
import com.davidgonzalez.bodysync.ui.screens.nutrition.dashboard.functions.BottomNavigationBar
import com.davidgonzalez.bodysync.ui.screens.nutrition.dashboard.functions.CaloriasProgressCircle
import com.davidgonzalez.bodysync.viewmodel.NutritionViewModel

@Composable
fun DashBoardNutritionScreen(viewModel: NutritionViewModel = viewModel(), navController: NavHostController) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    val caloriasConsumidas by viewModel.caloriasConsumidas.collectAsState()
    val comidas by viewModel.comidas.collectAsState()
    val nombre by viewModel.nombreComida.collectAsState()
    val calorias by viewModel.calorias.collectAsState()
    val resumenPorTipo by viewModel.resumenPorTipo.collectAsState()
    val nombreUsuario by viewModel.nombreUsuario.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.obtenerNombreUsuario()
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(selectedItem = "inicio")
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = nombreUsuario,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            CaloriasProgressCircle(
                caloriasConsumidas = caloriasConsumidas,
                caloriasTotales = 2000
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { mostrarDialogo = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A5F0B))
            ) {
                Text("Añadir comida")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón para iniciar el escaneo
            Button(
                onClick = { navController.navigate("barcode_scanner") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Escanear producto")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Mostrar resumen por tipo y alimentos
            listOf("Desayuno", "Comida", "Cena", "Snack").forEach { tipo ->
                val kcalTotales = resumenPorTipo[tipo] ?: 0
                Text(
                    text = tipo,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "$kcalTotales kcal", fontSize = 16.sp)
                }

                comidas.filter { it.third == tipo }.forEach {
                    val nombreComida = it.first
                    val kcal = it.second

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 2.dp, bottom = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "- $nombreComida", fontSize = 14.sp)
                        Text(text = "$kcal kcal", fontSize = 14.sp)
                    }
                }

                Divider(color = Color(0xFFDAE2DB))
            }
        }

        if (mostrarDialogo) {
            AlertDialog(
                onDismissRequest = { mostrarDialogo = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.añadirComida {
                                mostrarDialogo = false
                            }
                        }
                    ) {
                        Text("Guardar", color = Color(0xFF2C5704))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogo = false }) {
                        Text("Cancelar", color = Color.Gray)
                    }
                },
                title = { Text("Añadir comida", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tipo de comida", fontWeight = FontWeight.SemiBold)
                            IconButton(onClick = {
                                navController.navigate("barcode_scanner")
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.icon_qrcode),
                                    contentDescription = "Escanear",
                                    tint = Color(0xFF2C5704)
                                )
                            }

                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        viewModel.DropdownMenuTipo()

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { viewModel.actualizarNombreComida(it) },
                            label = { Text("Nombre del alimento") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = calorias,
                            onValueChange = { viewModel.actualizarCalorias(it) },
                            label = { Text("Calorías") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                },
                containerColor = Color.White,
                shape = MaterialTheme.shapes.large
            )
        }
    }
}
