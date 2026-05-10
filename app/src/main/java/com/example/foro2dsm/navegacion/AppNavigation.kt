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
import com.example.foro2dsm.pantallas.HistorialScreen
import com.example.foro2dsm.pantallas.FiltrarGastosScreen
import com.example.foro2dsm.pantallas.TotalMensualScreen
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
                onVerHistorial = {
                    navController.navigate("historial")
                },
                onCerrarSesion = {
                    navController.navigate("login") {
                        popUpTo("home") {
                            inclusive = true
                        }
                    }
                },
                onFiltrar = {
                    navController.navigate("filtrar")
                },
                onGastosMensuales = {
                    navController.navigate("total_mensual")
                },

                )
        }

        composable("agregar_gasto") {
            AgregarGastoScreen(
                onVolver = { navController.popBackStack() },
                onGastoGuardado = { navController.popBackStack() }
            )
        }

        composable("historial") {
            HistorialScreen(
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
        composable("filtrar") {
            FiltrarGastosScreen(
                onVolver = { navController.popBackStack() }
            )
        }
        composable("total_mensual") {
            TotalMensualScreen(
                onVolver = { navController.popBackStack() }
            )
        }
    }
}