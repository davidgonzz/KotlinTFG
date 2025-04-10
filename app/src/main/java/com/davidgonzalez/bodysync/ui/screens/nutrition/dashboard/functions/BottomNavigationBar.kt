package com.davidgonzalez.bodysync.ui.screens.nutrition.dashboard.functions

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidgonzalez.bodysync.R

@Composable
fun BottomNavigationBar(selectedItem: String) {
    NavigationBar(containerColor = Color.White, tonalElevation = 6.dp) {
        NavigationBarItem(
            icon = {
                Icon(imageVector = Icons.Default.Home, contentDescription = "Inicio")
            },
            label = { Text("Inicio", fontSize = 12.sp) },
            selected = selectedItem == "inicio",
            onClick = { /* TODO */ },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2C5704),
                selectedTextColor = Color(0xFF2C5704),
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.icon_progress),
                    contentDescription = "Progreso",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Progreso", fontSize = 12.sp) },
            selected = selectedItem == "progreso",
            onClick = { /* TODO */ }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.icon_setting),
                    contentDescription = "Modo",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Modo", fontSize = 12.sp) },
            selected = selectedItem == "modo",
            onClick = { /* TODO */ }
        )
    }
}
