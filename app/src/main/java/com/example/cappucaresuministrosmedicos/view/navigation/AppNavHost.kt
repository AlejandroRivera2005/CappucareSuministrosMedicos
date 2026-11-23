package com.example.cappucaresuministrosmedicos.view.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cappucaresuministrosmedicos.model.local.entity.TipoMovimiento
import com.example.cappucaresuministrosmedicos.view.screens.AlertasScreen
import com.example.cappucaresuministrosmedicos.view.screens.ConexionEstadoScreen
import com.example.cappucaresuministrosmedicos.view.screens.EscanerScreen
import com.example.cappucaresuministrosmedicos.view.screens.HistorialScreen
import com.example.cappucaresuministrosmedicos.view.screens.HomeScreen
import com.example.cappucaresuministrosmedicos.view.screens.LoginScreen
import com.example.cappucaresuministrosmedicos.view.screens.MovimientoScreen
import com.example.cappucaresuministrosmedicos.view.screens.ProductoDetalleScreen
import com.example.cappucaresuministrosmedicos.view.screens.ProductoFormScreen
import com.example.cappucaresuministrosmedicos.view.screens.ProductoListaScreen
import com.example.cappucaresuministrosmedicos.view.screens.RegistroScreen
import com.example.cappucaresuministrosmedicos.view.screens.UsuariosScreen
import com.example.cappucaresuministrosmedicos.viewModel.AlertasViewModel
import com.example.cappucaresuministrosmedicos.viewModel.ConexionViewModel
import com.example.cappucaresuministrosmedicos.viewModel.HomeViewModel
import com.example.cappucaresuministrosmedicos.viewModel.PokemonAleatorioViewModel
import com.example.cappucaresuministrosmedicos.viewModel.SesionViewModel
import kotlinx.coroutines.launch

object Rutas {
    const val login = "login"
    const val sinInternet = "sin_internet"
    const val mantenimiento = "mantenimiento"
    const val registro = "registro"
    const val home = "home"
    const val escaner = "escaner"
    const val alertas = "alertas"
    const val movimientos = "movimientos"
    const val usuarios = "usuarios"
    const val productos = "productos"
    const val producto_nuevo = "productos/nuevo"
    const val producto_detalle = "producto/{productoId}"
    const val producto_editar = "producto/{productoId}/editar"
    const val producto_movimiento = "producto/{productoId}/movimiento/{tipo}"

    fun detalle(id: Long) = "producto/$id"

    fun editar(id: Long) = "producto/$id/editar"

    fun movimiento(id: Long, tipo: TipoMovimiento) =
        "producto/$id/movimiento/${tipo.name}"
}

private val rutasConBarra = setOf(
    Rutas.home,
    Rutas.productos,
    Rutas.escaner,
    Rutas.alertas
)

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    val conexionViewModel: ConexionViewModel = hiltViewModel()

    val internet by conexionViewModel.internetDisponible.collectAsState(
        initial = null
    )

    val scope = rememberCoroutineScope()

    var comprobando by remember {
        mutableStateOf(false)
    }

    var destinoPendiente by remember {
        mutableStateOf(Rutas.productos)
    }

    fun navegarConVerificacion(ruta: String) {
        if (comprobando || internet != true) {
            return
        }

        scope.launch {
            comprobando = true

            try {
                if (conexionViewModel.apiDisponible()) {
                    navController.navigate(ruta) {
                        launchSingleTop = true
                    }
                } else {
                    destinoPendiente = ruta

                    navController.navigate(Rutas.mantenimiento) {
                        launchSingleTop = true
                    }
                }
            } finally {
                comprobando = false
            }
        }
    }

    val entradaActual by navController.currentBackStackEntryAsState()

    val rutaActual = entradaActual?.destination?.route

    val cantidadAlertas = if (rutaActual in rutasConBarra) {
        val alertasViewModel: AlertasViewModel = hiltViewModel()
        val alertas by alertasViewModel.uiState.collectAsState()
        alertas.productos.size
    } else {
        0
    }

    val sesionViewModel: SesionViewModel = hiltViewModel()

    val esEncargado by sesionViewModel.esEncargado.collectAsState()

    val irATab: (String) -> Unit = { ruta ->
        when (ruta) {
            Rutas.home -> {
                navController.popBackStack(
                    Rutas.home,
                    inclusive = false
                )
            }

            Rutas.productos -> {
                navegarConVerificacion(ruta)
            }

            else -> {
                navController.navigate(ruta) {
                    popUpTo(Rutas.home) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            contentWindowInsets = WindowInsets(
                0.dp,
                0.dp,
                0.dp,
                0.dp
            ),
            bottomBar = {
                if (rutaActual in rutasConBarra) {
                    BarraNavegacion(
                        rutaActual = rutaActual,
                        alertas = cantidadAlertas,
                        onNavegar = irATab
                    )
                }
            }
        ) { innerPadding ->

            val relleno = PaddingValues(
                bottom = innerPadding.calculateBottomPadding()
            )

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
                                popUpTo(Rutas.login) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        },
                        onIrARegistro = {
                            navController.navigate(Rutas.registro)
                        }
                    )
                }

                composable(Rutas.registro) {
                    RegistroScreen(
                        onRegistroExitoso = {
                            navController.popBackStack()
                        },
                        onVolver = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(Rutas.mantenimiento) {
                    ConexionEstadoScreen(
                        sinInternet = false,
                        comprobando = comprobando,
                        onReintentar = {
                            if (!comprobando && internet == true) {
                                scope.launch {
                                    comprobando = true

                                    try {
                                        if (conexionViewModel.apiDisponible()) {
                                            navController.navigate(
                                                destinoPendiente
                                            ) {
                                                popUpTo(Rutas.mantenimiento) {
                                                    inclusive = true
                                                }
                                                launchSingleTop = true
                                            }
                                        }
                                    } finally {
                                        comprobando = false
                                    }
                                }
                            }
                        },
                        onVolver = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(Rutas.home) {
                    val homeViewModel: HomeViewModel = hiltViewModel()

                    val estado by homeViewModel.uiState.collectAsState()

                    val pokemonViewModel: PokemonAleatorioViewModel =
                        hiltViewModel()

                    val pokemon by pokemonViewModel.uiState.collectAsState()

                    HomeScreen(
                        nombre = estado.nombre,
                        esEncargado = estado.esEncargado,
                        pendientes = estado.pendientes,
                        onIrAProductos = {
                            irATab(Rutas.productos)
                        },
                        onIrAMovimientos = {
                            navegarConVerificacion(Rutas.movimientos)
                        },
                        onIrAEscaner = {
                            irATab(Rutas.escaner)
                        },
                        onIrAUsuarios = {
                            navController.navigate(Rutas.usuarios)
                        },
                        onCerrarSesion = {
                            homeViewModel.cerrarSesion()

                            navController.navigate(Rutas.login) {
                                popUpTo(Rutas.home) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        },
                        pokemon = pokemon,
                        onLlamarPokemon = pokemonViewModel::llamar,
                        onCerrarPokemon = pokemonViewModel::cerrar
                    )
                }

                composable(Rutas.usuarios) {
                    UsuariosScreen(
                        onVolver = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(Rutas.movimientos) {
                    HistorialScreen(
                        onVolver = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(Rutas.escaner) {
                    EscanerScreen(
                        onVolver = {
                            irATab(Rutas.home)
                        }
                    )
                }

                composable(Rutas.alertas) {
                    AlertasScreen(
                        onVerDetalle = { id ->
                            navController.navigate(Rutas.detalle(id))
                        }
                    )
                }

                composable(Rutas.productos) {
                    ProductoListaScreen(
                        onVolver = {
                            irATab(Rutas.home)
                        },
                        onNuevo = {
                            navController.navigate(Rutas.producto_nuevo)
                        },
                        onVerDetalle = { id ->
                            navController.navigate(Rutas.detalle(id))
                        },
                        puedeCrear = esEncargado
                    )
                }

                composable(Rutas.producto_nuevo) {
                    ProductoFormScreen(
                        onVolver = {
                            navController.popBackStack()
                        },
                        onGuardado = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(
                    Rutas.producto_detalle,
                    arguments = listOf(
                        navArgument("productoId") {
                            type = NavType.LongType
                        }
                    )
                ) { entrada ->

                    val id = entrada.arguments?.getLong("productoId")
                        ?: return@composable

                    ProductoDetalleScreen(
                        onVolver = {
                            navController.popBackStack()
                        },
                        onEditar = {
                            navController.navigate(Rutas.editar(id))
                        },
                        onEliminado = {
                            navController.popBackStack()
                        },
                        onRegistrarMovimiento = { tipo ->
                            navController.navigate(
                                Rutas.movimiento(id, tipo)
                            )
                        },
                        esEncargado = esEncargado
                    )
                }

                composable(
                    Rutas.producto_editar,
                    arguments = listOf(
                        navArgument("productoId") {
                            type = NavType.LongType
                        }
                    )
                ) {
                    ProductoFormScreen(
                        onVolver = {
                            navController.popBackStack()
                        },
                        onGuardado = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(
                    Rutas.producto_movimiento,
                    arguments = listOf(
                        navArgument("productoId") {
                            type = NavType.LongType
                        },
                        navArgument("tipo") {
                            type = NavType.StringType
                        }
                    )
                ) {
                    MovimientoScreen(
                        onVolver = {
                            navController.popBackStack()
                        },
                        onGuardado = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
        if (internet != true) {
            BackHandler(enabled = true) {}
            Surface(modifier = Modifier.fillMaxSize()) {
                if (internet == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    ConexionEstadoScreen(
                        sinInternet = true,
                        onReintentar = {}
                    )
                }
            }
        }
    }
}

