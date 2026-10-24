package com.example.cappucaresuministrosmedicos.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cappucaresuministrosmedicos.model.MovimientoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HistorialProductoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    movimientoRepository: MovimientoRepository
) : ViewModel() {

    private val productoId: Long = checkNotNull(savedStateHandle.get<Long>("productoId"))

    val movimientos: StateFlow<List<ItemHistorial>> =
        movimientoRepository.observarPorProducto(productoId)
            .map { lista -> lista.map { it.aItem("") } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}