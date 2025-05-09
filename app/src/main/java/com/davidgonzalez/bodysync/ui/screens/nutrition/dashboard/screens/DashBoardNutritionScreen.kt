package com.davidgonzalez.bodysync.ui.screens.nutrition.dashboard.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.davidgonzalez.bodysync.R
import com.davidgonzalez.bodysync.ui.screens.nutrition.dashboard.functions.BottomNavigationBar
import com.davidgonzalez.bodysync.ui.screens.nutrition.dashboard.functions.CaloriasProgressCircle
import com.davidgonzalez.bodysync.viewmodel.NutritionViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun DashBoardNutritionScreen(viewModel: NutritionViewModel = viewModel(), navController: NavHostController) {
    var mostrarDialogo by remember { mutableStateOf(false) }
    var expandedMenu by remember { mutableStateOf(false) }
    var mostrarDialogoGramos by remember { mutableStateOf(false) }
    var gramosInput by remember { mutableStateOf("") }
    var codigoEscaneado by remember { mutableStateOf<String?>(null) }
    val caloriasConsumidas by viewModel.caloriasConsumidas.collectAsState()
    val comidas by viewModel.comidas.collectAsState()
    val nombre by viewModel.nombreComida.collectAsState()
    val calorias by viewModel.calorias.collectAsState()
    val resumenPorTipo by viewModel.resumenPorTipo.collectAsState()
    val nombreUsuario by viewModel.nombreUsuario.collectAsState()
    val caloriasObjetivo by viewModel.caloriasObjetivo.collectAsState()
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val codigoLiveData = savedStateHandle?.getLiveData<String>("codigo_barras")?.observeAsState()
    var mostrarMenuModo by remember { mutableStateOf(false) }
    var modoActual by remember { mutableStateOf("nutricion") }

    LaunchedEffect(Unit) {
        viewModel.obtenerNombreUsuario()
        viewModel.obtenerComidasDeHoy()
        viewModel.calcularCaloriasDesdeDatosUsuario()
    }

    // Detecta si ha llegado un código
    LaunchedEffect(codigoLiveData?.value) {
        codigoLiveData?.value?.let { codigo ->
            codigoEscaneado = codigo
            mostrarDialogoGramos = true
            savedStateHandle.remove<String>("codigo_barras")
        }
    }
    Scaffold(
        bottomBar = {
            BottomNavigationBar(selectedItem = "inicio", navController = navController, onModoClick = { mostrarMenuModo = true})
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

            CaloriasProgressCircle(
                caloriasConsumidas = caloriasConsumidas,
                caloriasTotales = caloriasObjetivo
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
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

            // Diálogo para pedir gramos
            if (mostrarDialogoGramos && codigoEscaneado != null) {
                AlertDialog(
                    onDismissRequest = {
                        mostrarDialogoGramos = false
                        codigoEscaneado = null
                        gramosInput = ""
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            val gramos = gramosInput.toIntOrNull() ?: 100
                            viewModel.buscarAlimentoPorCodigo(
                                codigoEscaneado!!,
                                gramos
                            ) { nombre, kcal ->
                                viewModel.actualizarNombreComida(nombre)
                                viewModel.actualizarCalorias(kcal.toString())
                            }
                            mostrarDialogoGramos = false
                            codigoEscaneado = null
                            gramosInput = ""
                        }) {
                            Text("Aceptar", color = Color(0xFF2C5704))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            mostrarDialogoGramos = false
                            codigoEscaneado = null
                            gramosInput = ""
                        }) {
                            Text("Cancelar", color = Color.Gray)
                        }
                    },
                    title = { Text("¿Cuántos gramos?", fontWeight = FontWeight.Bold) },
                    text = {
                        OutlinedTextField(
                            value = gramosInput,
                            onValueChange = { gramosInput = it },
                            label = { Text("Gramos") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    },
                    containerColor = Color.White,
                    shape = MaterialTheme.shapes.large
                )
            }
        }
    }
}

