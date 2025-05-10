package com.davidgonzalez.bodysync.ui.screens.gym.dashboard.functions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.davidgonzalez.bodysync.R

@Composable
fun BottomNavigationGymBar(
    selectedItem: String,
    navController: NavHostController
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 4.dp
    ) {
        val iconSize = 26.dp

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Inicio",
                    modifier = Modifier.size(iconSize)
                )
            },
            label = { Text("Inicio", fontSize = 11.sp) },
            selected = selectedItem == "inicio",
            onClick = {
                navController.navigate("dashboard_gym") {
                    popUpTo("dashboard_gym") { inclusive = false }
                    launchSingleTop = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFD97706),
                selectedTextColor = Color(0xFFD97706),
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.icon_rutinas),
                    contentDescription = "Rutinas",
                    modifier = Modifier.size(iconSize)
                )
            },
            label = { Text("Rutinas", fontSize = 11.sp) },
            selected = selectedItem == "rutinas",
            onClick = {
                navController.navigate("rutinas_gym") {
                    popUpTo("dashboard_gym") { inclusive = false }
                    launchSingleTop = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFD97706),
                selectedTextColor = Color(0xFFD97706),
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.icon_progress),
                    contentDescription = "Progreso",
                    modifier = Modifier.size(iconSize)
                )
            },
            label = { Text("Progreso", fontSize = 11.sp) },
            selected = selectedItem == "progreso",
            onClick = {
                navController.navigate("progreso_gym") {
                    popUpTo("dashboard_gym") { inclusive = false }
                    launchSingleTop = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFD97706),
                selectedTextColor = Color(0xFFD97706),
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.icon_setting),
                    contentDescription = "Modo",
                    modifier = Modifier.size(iconSize)
                )
            },
            label = { Text("Modo", fontSize = 11.sp) },
            selected = selectedItem == "modo",
            onClick = {
                navController.navigate("choose") {
                    popUpTo(0)
                    launchSingleTop = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFD97706),
                selectedTextColor = Color(0xFFD97706),
                indicatorColor = Color.Transparent
            )
        )
    }
}
