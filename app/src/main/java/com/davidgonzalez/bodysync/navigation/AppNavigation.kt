package com.davidgonzalez.bodysync.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.davidgonzalez.bodysync.ui.screens.auth.LoginScreen
import com.davidgonzalez.bodysync.ui.screens.auth.RegistrationScreen
import com.davidgonzalez.bodysync.ui.screens.splash.SplashScreen
import com.davidgonzalez.bodysync.ui.screens.onboarding.ChooseScreenUI

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        // SplashScreen decide hacia dónde ir
        composable("splash") {
            SplashScreen(navController)
        }

        composable("login") {
            LoginScreen(
                onLoginExitoso = {
                    navController.navigate("choose") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onIrARegistro = {
                    navController.navigate("registro")
                }
            )
        }
        composable("registro") {
            RegistrationScreen(
                onRegistroExitoso = {
                    navController.navigate("choose") {
                        popUpTo("registro") { inclusive = true }
                    }
                },
                onIrALogin = {
                    navController.navigate("login")
                }
            )
        }
        // Pantalla principal post-login
        composable("choose") {
            ChooseScreenUI()
        }
    }
}
