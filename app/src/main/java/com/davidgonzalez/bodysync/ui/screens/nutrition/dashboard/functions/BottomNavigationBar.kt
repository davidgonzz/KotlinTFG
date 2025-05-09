package com.davidgonzalez.bodysync.ui.screens.nutrition.dashboard.functions

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.davidgonzalez.bodysync.R

@Composable
fun BottomNavigationBar(
    selectedItem: String,
    navController: NavHostController,
    onModoClick: () -> Unit = {}
) {
    NavigationBar(containerColor = Color.White, tonalElevation = 6.dp) {
        // Botón Inicio
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Inicio",
                    modifier = Modifier.size(if (selectedItem == "inicio") 28.dp else 24.dp)
                )
            },
            label = { Text("Inicio", fontSize = 12.sp) },
            selected = selectedItem == "inicio",
            onClick = {
                if (selectedItem != "inicio") {
                    navController.navigate("dashboard_nutricion") {
                        popUpTo("dashboard_nutricion") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2C5704),
                selectedTextColor = Color(0xFF2C5704),
                indicatorColor = Color.Transparent
            )
        )

        // Botón Progreso
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.icon_progress),
                    contentDescription = "Progreso",
                    modifier = Modifier.size(if (selectedItem == "progreso") 28.dp else 24.dp)
                )
            },
            label = { Text("Progreso", fontSize = 12.sp) },
            selected = selectedItem == "progreso",
            onClick = {
                if (selectedItem != "progreso") {
                    navController.navigate("progreso_nutricion") {
                        popUpTo("dashboard_nutricion") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2C5704),
                selectedTextColor = Color(0xFF2C5704),
                indicatorColor = Color.Transparent
            )
        )

        // Botón Modo → activa el menú flotante
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.icon_setting),
                    contentDescription = "Modo",
                    modifier = Modifier.size(if (selectedItem == "modo") 28.dp else 24.dp)
                )
            },
            label = { Text("Modo", fontSize = 12.sp) },
            selected = selectedItem == "modo",
            onClick = {
                navController.navigate("choose") {
                    popUpTo(0) // Limpia el backstack si lo deseas
                    launchSingleTop = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2C5704),
                selectedTextColor = Color(0xFF2C5704),
                indicatorColor = Color.Transparent
            )
        )
    }
}
