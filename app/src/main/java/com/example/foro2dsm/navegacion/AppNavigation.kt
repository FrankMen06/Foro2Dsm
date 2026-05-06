package com.example.foro2dsm.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foro2dsm.firebase.AuthService
import com.example.foro2dsm.pantallas.HomeScreen
import com.example.foro2dsm.pantallas.LoginScreen
import com.example.foro2dsm.pantallas.RegisterScreen
import com.example.foro2dsm.pantallas.AgregarGastoScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authService = AuthService()

    val rutaInicial = if (authService.usuarioActual() != null) {
        "home"
    } else {
        "login"
    }

    NavHost(
        navController = navController,
        startDestination = rutaInicial
    ) {
        composable("login") {
            LoginScreen(
                onLoginExitoso = {
                    navController.navigate("home") {
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                },
                onIrRegistro = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegistroExitoso = {
                    navController.navigate("home") {
                        popUpTo("register") {
                            inclusive = true
                        }
                    }
                },
                onIrLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("home") {
            HomeScreen(
                onAgregarGasto = {
                    navController.navigate("agregar_gasto")
                },

                onCerrarSesion = {
                    navController.navigate("login") {
                        popUpTo("home") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable("agregar_gasto") {
            AgregarGastoScreen(
                onVolver = {
                    navController.popBackStack()
                },
                onGastoGuardado = {
                    navController.popBackStack()
                }
            )
        }
    }
}