package com.example.cappucaresuministrosmedicos.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cappucaresuministrosmedicos.model.ProductoRepository
import com.example.cappucaresuministrosmedicos.model.local.entity.ProductoEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class AlertasUiState(
    val productos: List<ProductoEntity> = emptyList(),
    val cargando: Boolean = true
)

@HiltViewModel
class AlertasViewModel @Inject constructor(
    productoRepository: ProductoRepository
) : ViewModel() {

    val uiState: StateFlow<AlertasUiState> = productoRepository.observarBajoMinimo()
        .map { AlertasUiState(productos = it, cargando = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AlertasUiState())


    fun textoReposicion(): String {
        val fecha = SimpleDateFormat("dd/MM/yyyy", Locale("es", "CL")).format(Date())
        val lineas = uiState.value.productos.joinToString("\n") { p ->
            val faltan = (p.stockMinimo - p.stockDisponible).coerceAtLeast(1)
            "- ${p.nombre}: pedir $faltan ${p.unidadMedida} " +
                    "(disponible ${p.stockDisponible}, mínimo ${p.stockMinimo})"
        }
        return "Lista de reposición Cappucare - $fecha\n$lineas"
    }
}