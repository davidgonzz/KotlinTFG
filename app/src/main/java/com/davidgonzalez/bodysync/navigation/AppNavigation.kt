package com.davidgonzalez.bodysync.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.davidgonzalez.bodysync.ui.screens.auth.LoginScreen
import com.davidgonzalez.bodysync.ui.screens.auth.RegistrationScreen
import com.davidgonzalez.bodysync.ui.screens.nutrition.dashboard.screens.BarcodeScannerScreen
import com.davidgonzalez.bodysync.ui.screens.nutrition.dashboard.screens.DashBoardNutritionScreen
import com.davidgonzalez.bodysync.ui.screens.gym.dashboard.screens.DashboardGymScreen
import com.davidgonzalez.bodysync.ui.screens.gym.dashboard.screens.DetailExerciseScreen
import com.davidgonzalez.bodysync.ui.screens.nutrition.progress.screens.ProgressNutritionScreen
import com.davidgonzalez.bodysync.ui.screens.onboarding.ChooseScreenUI
import com.davidgonzalez.bodysync.ui.screens.onboarding.PersonalDataScreen
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
                    navController.navigate("datos_fisicos") {
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
                onElegirNutricion = {
                    navController.navigate("dashboard_nutricion")
                },
                onElegirGimnasio = {
                    navController.navigate("gym") // futura implementación
                }
            )
        }

        composable("datos_fisicos") {
            PersonalDataScreen(onContinuar = {
                navController.navigate("choose") {
                    popUpTo("registration") { inclusive = true }
                }
            })
        }

        composable("dashboard_nutricion") {
            DashBoardNutritionScreen(navController = navController)
        }

        composable("barcode_scanner") {
            BarcodeScannerScreen(
                onCodeScanned = { code ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("codigo_barras", code)

                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() }
            )
        }

        composable("progreso_nutricion") {
            ProgressNutritionScreen(navController = navController)
        }
        composable("gym") {
            DashboardGymScreen(navController = navController)
        }
        composable("detalle_ejercicio/{nombre}") { backStackEntry ->
            val nombreEjercicio = backStackEntry.arguments?.getString("nombre") ?: ""
            DetailExerciseScreen(nombre = nombreEjercicio, navController = navController)
            
        }

    }
}
