package com.davidgonzalez.bodysync.ui.screens.gym.dashboard.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.davidgonzalez.bodysync.ui.screens.gym.dashboard.functions.BottomNavigationGymBar
import com.davidgonzalez.bodysync.viewmodel.GymViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun DashboardGymScreen(
    viewModel: GymViewModel = viewModel(),
    navController: NavHostController
) {
    val ejercicios by viewModel.ejercicios.collectAsState()
    val nombreUsuario by viewModel.nombreUsuario.collectAsState()
    val gruposDisponibles = listOf("pecho", "espalda", "pierna", "hombro", "biceps", "triceps", "cardio")
    var grupoSeleccionado by remember { mutableStateOf("pecho") }
    var expandedMenu by remember { mutableStateOf(false) }

    val ejerciciosFiltrados = ejercicios.filter { it.grupoMuscular.lowercase() == grupoSeleccionado }

    LaunchedEffect(Unit) {
        viewModel.obtenerNombreUsuario()
    }


    Scaffold(
        bottomBar = {
            BottomNavigationGymBar(
                selectedItem = "inicio",
                navController = navController
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("¡Vamos, $nombreUsuario!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, fontSize = 28.sp,)
                Box {
                    IconButton(onClick = { expandedMenu = true }) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil", tint = Color(0xFF2C5704))
                    }
                    DropdownMenu(
                        expanded = expandedMenu,
                        onDismissRequest = { expandedMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Cerrar sesión") },
                            onClick = {
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

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(gruposDisponibles) { grupo ->
                    val seleccionado = grupo == grupoSeleccionado
                    Button(
                        onClick = { grupoSeleccionado = grupo },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (seleccionado) Color(0xFFFBCE9C) else Color(0xFFEFEFEF),
                            contentColor = if (seleccionado) Color.Black else Color.DarkGray
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text(grupo.replaceFirstChar { it.uppercase() })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (ejerciciosFiltrados.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Cargando ejercicios...", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(ejerciciosFiltrados) { ejercicio ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate("detalle_ejercicio/${ejercicio.nombre}")
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (ejercicio.completado) Color(0xFFD8F5D0) else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = ejercicio.gifUrl,
                                    contentDescription = ejercicio.nombre,
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(ejercicio.nombre, style = MaterialTheme.typography.titleMedium)
                                    Text("Series: ${ejercicio.series}")
                                    Text("Descanso: ${ejercicio.descanso}s")
                                }
                                Checkbox(
                                    checked = ejercicio.completado,
                                    onCheckedChange = {
                                        viewModel.toggleCompletado(ejercicio.nombre)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
