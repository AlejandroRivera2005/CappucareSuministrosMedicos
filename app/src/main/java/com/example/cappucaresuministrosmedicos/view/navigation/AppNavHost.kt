package com.example.cappucaresuministrosmedicos.view.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cappucaresuministrosmedicos.model.local.entity.TipoMovimiento
import com.example.cappucaresuministrosmedicos.view.screens.AlertasScreen
import com.example.cappucaresuministrosmedicos.view.screens.EscanerScreen
import com.example.cappucaresuministrosmedicos.view.screens.HomeScreen
import com.example.cappucaresuministrosmedicos.view.screens.LoginScreen
import com.example.cappucaresuministrosmedicos.view.screens.MovimientoScreen
import com.example.cappucaresuministrosmedicos.view.screens.ProductoDetalleScreen
import com.example.cappucaresuministrosmedicos.view.screens.ProductoFormScreen
import com.example.cappucaresuministrosmedicos.view.screens.ProductoListaScreen
import com.example.cappucaresuministrosmedicos.view.screens.RegistroScreen
import com.example.cappucaresuministrosmedicos.viewModel.AlertasViewModel
import com.example.cappucaresuministrosmedicos.viewModel.SesionViewModel

object Rutas {
    const val login = "login"
    const val registro = "registro"
    const val home = "home"
    const val escaner = "escaner"
    const val alertas = "alertas"
    const val productos = "productos"
    const val producto_nuevo = "productos/nuevo"
    const val producto_detalle = "producto/{productoId}"
    const val producto_editar = "producto/{productoId}/editar"
    const val producto_movimiento = "producto/{productoId}/movimiento/{tipo}"

    fun detalle(id: Long) = "producto/$id"
    fun editar(id: Long) = "producto/$id/editar"
    fun movimiento(id: Long, tipo: TipoMovimiento) = "producto/$id/movimiento/${tipo.name}"
}

private val rutasConBarra = setOf(Rutas.home, Rutas.productos, Rutas.escaner, Rutas.alertas)

private fun NavController.irA(ruta: String, opciones: NavOptionsBuilder.() -> Unit = {}) {
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        navigate(ruta, opciones)
    }
}

private fun NavController.volver() {
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        popBackStack()
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val argProducto = listOf(navArgument("productoId") { type = NavType.LongType })

    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route

    val sesionViewModel: SesionViewModel = hiltViewModel()
    LaunchedEffect(rutaActual) {
        val esPublica = rutaActual == null ||
                rutaActual == Rutas.login ||
                rutaActual == Rutas.registro
        if (!esPublica && !sesionViewModel.haySesion) {
            navController.navigate(Rutas.login) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    val alertasViewModel: AlertasViewModel = hiltViewModel()
    val alertas by alertasViewModel.uiState.collectAsState()

    val irATab: (String) -> Unit = { ruta ->
        navController.navigate(ruta) {
            popUpTo(Rutas.home) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        bottomBar = {
            if (rutaActual in rutasConBarra) {
                BarraNavegacion(
                    rutaActual = rutaActual,
                    alertas = alertas.productos.size,
                    onNavegar = irATab
                )
            }
        }
    ) { innerPadding ->
        val relleno = PaddingValues(bottom = innerPadding.calculateBottomPadding())

        NavHost(
            navController = navController,
            startDestination = Rutas.login,
            modifier = Modifier
                .padding(relleno)
                .consumeWindowInsets(relleno)
        ) {

            composable(Rutas.login) {
                LoginScreen(
                    onLoginExitoso = {
                        navController.navigate(Rutas.home) {
                            popUpTo(Rutas.login) { inclusive = true }
                        }
                    },
                    onIrARegistro = { navController.irA(Rutas.registro) }
                )
            }

            composable(Rutas.registro) {
                RegistroScreen(
                    onRegistroExitoso = { navController.popBackStack() },
                    onVolver = { navController.volver() }
                )
            }

            composable(Rutas.home) {
                HomeScreen(
                    onIrAProductos = { irATab(Rutas.productos) },
                    onIrAEscaner = { irATab(Rutas.escaner) },
                    onCerrarSesion = {
                        sesionViewModel.cerrar()
                        navController.navigate(Rutas.login) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }

            composable(Rutas.escaner) {
                EscanerScreen(onVolver = { navController.volver() })
            }

            composable(Rutas.alertas) {
                AlertasScreen(
                    onVerDetalle = { id -> navController.irA(Rutas.detalle(id)) },
                    viewModel = alertasViewModel
                )
            }

            composable(Rutas.productos) {
                ProductoListaScreen(
                    onVolver = { navController.volver() },
                    onNuevo = { navController.irA(Rutas.producto_nuevo) },
                    onVerDetalle = { id -> navController.irA(Rutas.detalle(id)) }
                )
            }

            composable(Rutas.producto_nuevo) {
                ProductoFormScreen(
                    onVolver = { navController.volver() },
                    onGuardado = { navController.popBackStack() }
                )
            }

            composable(Rutas.producto_detalle, arguments = argProducto) { entrada ->
                val id = entrada.arguments?.getLong("productoId") ?: return@composable
                ProductoDetalleScreen(
                    onVolver = { navController.volver() },
                    onEditar = { navController.irA(Rutas.editar(id)) },
                    onEliminado = { navController.popBackStack() },
                    onRegistrarMovimiento = { tipo ->
                        navController.irA(Rutas.movimiento(id, tipo))
                    }
                )
            }

            composable(Rutas.producto_editar, arguments = argProducto) {
                ProductoFormScreen(
                    onVolver = { navController.volver() },
                    onGuardado = { navController.popBackStack() }
                )
            }

            composable(
                Rutas.producto_movimiento,
                arguments = argProducto + navArgument("tipo") { type = NavType.StringType }
            ) {
                MovimientoScreen(
                    onVolver = { navController.volver() },
                    onGuardado = { navController.popBackStack() }
                )
            }
        }
    }
}