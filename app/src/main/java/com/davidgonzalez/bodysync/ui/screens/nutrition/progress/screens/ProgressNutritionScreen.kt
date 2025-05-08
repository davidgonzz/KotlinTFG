package com.davidgonzalez.bodysync.ui.screens.nutrition.progress.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidgonzalez.bodysync.ui.screens.nutrition.dashboard.functions.BottomNavigationBar
import androidx.navigation.NavHostController

@Composable
fun ProgressNutritionScreen(
    navController: NavHostController
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(selectedItem = "progreso", navController = navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Pantalla de Progreso",
                fontSize = 24.sp
            )
        }
    }
}
