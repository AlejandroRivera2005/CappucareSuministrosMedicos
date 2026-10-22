package com.example.cappucaresuministrosmedicos.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cappucaresuministrosmedicos.model.ProductoRepository
import com.example.cappucaresuministrosmedicos.model.local.entity.ProductoEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductoDetalleUiState(
    val producto: ProductoEntity? = null,
    val cargando: Boolean = true,
    val eliminando: Boolean = false,
    val eliminado: Boolean = false,
    val errorGeneral: String? = null
)

@HiltViewModel
class ProductoDetalleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repositorio: ProductoRepository
) : ViewModel() {

    private val productoId: Long = checkNotNull(savedStateHandle.get<Long>("productoId"))

    private val _uiState = MutableStateFlow(ProductoDetalleUiState())
    val uiState: StateFlow<ProductoDetalleUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repositorio.observarPorId(productoId).collect { producto ->
                _uiState.update { it.copy(producto = producto, cargando = false) }
            }
        }
    }

    fun eliminar() {
        val producto = _uiState.value.producto ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(eliminando = true, errorGeneral = null) }
            try {
                repositorio.eliminar(producto)
                _uiState.update { it.copy(eliminando = false, eliminado = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(eliminando = false, errorGeneral = "No se pudo eliminar el producto")
                }
            }
        }
    }
}