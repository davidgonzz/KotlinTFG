package com.davidgonzalez.bodysync.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.davidgonzalez.bodysync.ui.screens.auth.LoginScreen
import com.davidgonzalez.bodysync.ui.screens.auth.RegistrationScreen
import com.davidgonzalez.bodysync.ui.screens.onboarding.ChooseScreenUI
import com.davidgonzalez.bodysync.ui.screens.splash.SplashScreen

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {

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

        composable("choose") {
            ChooseScreenUI(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("choose") { inclusive = true }
                    }
                }
            )
        }
    }
}
