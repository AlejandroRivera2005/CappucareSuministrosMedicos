package com.example.cappucaresuministrosmedicos.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cappucaresuministrosmedicos.model.MovimientoRepository
import com.example.cappucaresuministrosmedicos.model.ProductoRepository
import com.example.cappucaresuministrosmedicos.model.local.entity.TipoMovimiento
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

enum class FiltroHistorial { TODOS, INGRESOS, SALIDAS }

data class HistorialUiState(
    val items: List<ItemHistorial> = emptyList(),
    val filtro: FiltroHistorial = FiltroHistorial.TODOS,
    val cargando: Boolean = true
)

@HiltViewModel
class HistorialViewModel @Inject constructor(
    movimientoRepository: MovimientoRepository,
    productoRepository: ProductoRepository
) : ViewModel() {

    private val filtro = MutableStateFlow(FiltroHistorial.TODOS)

    val uiState: StateFlow<HistorialUiState> = combine(
        movimientoRepository.observarRecientes(100),
        productoRepository.observarTodos(),
        filtro
    ) { movimientos, productos, filtroActual ->
        val nombres = productos.associate { it.id to it.nombre }
        val items = movimientos
            .filter {
                when (filtroActual) {
                    FiltroHistorial.TODOS -> true
                    FiltroHistorial.INGRESOS -> it.tipo == TipoMovimiento.INGRESO
                    FiltroHistorial.SALIDAS -> it.tipo == TipoMovimiento.SALIDA
                }
            }
            .map { it.aItem(nombres[it.productoId] ?: "Producto") }
        HistorialUiState(items = items, filtro = filtroActual, cargando = false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HistorialUiState())

    fun onFiltroChange(nuevo: FiltroHistorial) {
        filtro.value = nuevo
    }
}