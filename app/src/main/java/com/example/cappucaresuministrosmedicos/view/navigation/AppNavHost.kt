package com.example.cappucaresuministrosmedicos.view.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cappucaresuministrosmedicos.view.screens.HomeScreen
import com.example.cappucaresuministrosmedicos.view.screens.LoginScreen
import com.example.cappucaresuministrosmedicos.view.screens.ProductoDetalleScreen
import com.example.cappucaresuministrosmedicos.view.screens.ProductoFormScreen
import com.example.cappucaresuministrosmedicos.view.screens.ProductoListaScreen
import com.example.cappucaresuministrosmedicos.view.screens.RegistroScreen

object Rutas {
    const val login = "login"
    const val registro = "registro"
    const val home = "home"
    const val productos = "productos"
    const val producto_nuevo = "productos/nuevo"
    const val producto_detalle = "producto/{productoId}"
    const val producto_editar = "producto/{productoId}/editar"

    fun detalle(id: Long) = "producto/$id"
    fun editar(id: Long) = "producto/$id/editar"
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val argProducto = listOf(navArgument("productoId") { type = NavType.LongType })

    NavHost(navController = navController, startDestination = Rutas.login) {

        composable(Rutas.login) {
            LoginScreen(
                onLoginExitoso = {
                    navController.navigate(Rutas.home) {
                        popUpTo(Rutas.login) { inclusive = true }
                    }
                },
                onIrARegistro = { navController.navigate(Rutas.registro) }
            )
        }

        composable(Rutas.registro) {
            RegistroScreen(
                onRegistroExitoso = { navController.popBackStack() },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.home) {
            HomeScreen(
                onIrAProductos = { navController.navigate(Rutas.productos) },
                onIrAMovimientos = { /* TODO: navegar a movimientos */ }
            )
        }

        composable(Rutas.productos) {
            ProductoListaScreen(
                onVolver = { navController.popBackStack() },
                onNuevo = { navController.navigate(Rutas.producto_nuevo) },
                onVerDetalle = { id -> navController.navigate(Rutas.detalle(id)) }
            )
        }

        composable(Rutas.producto_nuevo) {
            ProductoFormScreen(
                onVolver = { navController.popBackStack() },
                onGuardado = { navController.popBackStack() }
            )
        }

        composable(Rutas.producto_detalle, arguments = argProducto) { entrada ->
            val id = entrada.arguments?.getLong("productoId") ?: return@composable
            ProductoDetalleScreen(
                onVolver = { navController.popBackStack() },
                onEditar = { navController.navigate(Rutas.editar(id)) },
                onEliminado = { navController.popBackStack() }
            )
        }

        composable(Rutas.producto_editar, arguments = argProducto) {
            ProductoFormScreen(
                onVolver = { navController.popBackStack() },
                onGuardado = { navController.popBackStack() }
            )
        }
    }
}