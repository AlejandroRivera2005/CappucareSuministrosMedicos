package com.example.cappucaresuministrosmedicos.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cappucaresuministrosmedicos.model.ProductoRepository
import com.example.cappucaresuministrosmedicos.model.local.entity.ProductoEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ProductoListaUiState(
    val productos: List<ProductoEntity> = emptyList(),
    val totalProductos: Int = 0,
    val cargando: Boolean = true
)

@HiltViewModel
class ProductoListaViewModel @Inject constructor(
    repositorio: ProductoRepository
) : ViewModel() {

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    val uiState: StateFlow<ProductoListaUiState> =
        combine(repositorio.observarTodos(), _busqueda) { lista, texto ->
            val filtro = texto.trim()
            val filtrados = if (filtro.isEmpty()) lista else lista.filter {
                it.nombre.contains(filtro, ignoreCase = true) ||
                        it.categoria.contains(filtro, ignoreCase = true) ||
                        it.id.toString().contains(filtro)
            }
            ProductoListaUiState(
                productos = filtrados,
                totalProductos = lista.size,
                cargando = false
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProductoListaUiState())

    fun onBusquedaChange(valor: String) {
        _busqueda.value = valor
    }
}