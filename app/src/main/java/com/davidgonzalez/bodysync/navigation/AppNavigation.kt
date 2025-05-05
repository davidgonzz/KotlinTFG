package com.davidgonzalez.bodysync.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.davidgonzalez.bodysync.ui.screens.auth.LoginScreen
import com.davidgonzalez.bodysync.ui.screens.auth.RegistrationScreen
import com.davidgonzalez.bodysync.ui.screens.nutrition.dashboard.screens.BarcodeScannerScreen
import com.davidgonzalez.bodysync.ui.screens.nutrition.dashboard.screens.DashBoardNutritionScreen
import com.davidgonzalez.bodysync.ui.screens.onboarding.ChooseScreenUI
import com.davidgonzalez.bodysync.ui.screens.onboarding.PersonalDataScreen
import com.davidgonzalez.bodysync.ui.screens.splash.SplashScreen
import com.davidgonzalez.bodysync.viewmodel.NutritionViewModel

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()
    val viewModel: NutritionViewModel = viewModel()

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
                    navController.navigate("gym") // si luego añades esta
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("choose") { inclusive = true }
                    }
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
            DashBoardNutritionScreen(navController= navController)
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



    }
}