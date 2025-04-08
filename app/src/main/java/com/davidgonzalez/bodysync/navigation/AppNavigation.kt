package com.davidgonzalez.bodysync.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.davidgonzalez.bodysync.ui.screens.auth.LoginScreen
import com.davidgonzalez.bodysync.ui.screens.auth.RegistrationScreen

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("registro") {
            RegistrationScreen(
                onRegistroExitoso = {
                    navController.navigate("login") {
                        popUpTo("registro") { inclusive = true }
                    }
                },
                onIrALogin = {
                    navController.navigate("login")
                }
            )
        }

        composable("login") {
            LoginScreen(
                onLoginExitoso = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onIrARegistro = {
                    navController.navigate("registro")
                }
            )
        }
    }
}
