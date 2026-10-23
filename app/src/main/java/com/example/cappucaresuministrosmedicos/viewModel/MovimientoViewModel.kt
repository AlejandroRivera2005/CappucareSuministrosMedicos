package com.example.cappucaresuministrosmedicos.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cappucaresuministrosmedicos.model.MovimientoRepository
import com.example.cappucaresuministrosmedicos.model.ProductoRepository
import com.example.cappucaresuministrosmedicos.model.SesionUsuario
import com.example.cappucaresuministrosmedicos.model.local.entity.MotivoMovimiento
import com.example.cappucaresuministrosmedicos.model.local.entity.ProductoEntity
import com.example.cappucaresuministrosmedicos.model.local.entity.TipoMovimiento
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import com.example.cappucaresuministrosmedicos.model.NotificadorStock

data class MovimientoUiState(
    val producto: ProductoEntity? = null,
    val tipo: TipoMovimiento = TipoMovimiento.SALIDA,
    val cantidad: Int = 1,
    val motivo: MotivoMovimiento = MotivoMovimiento.VENTA,
    val fecha: String = "",
    val responsable: String = "",
    val errorCantidad: String? = null,
    val errorGeneral: String? = null,
    val cargando: Boolean = false,
    val guardado: Boolean = false
) {
    val motivos: List<MotivoMovimiento>
        get() = if (tipo == TipoMovimiento.INGRESO)
            listOf(MotivoMovimiento.RECEPCION, MotivoMovimiento.AJUSTE)
        else
            listOf(MotivoMovimiento.VENTA, MotivoMovimiento.MERMA, MotivoMovimiento.AJUSTE)

    val stockResultante: Int?
        get() = producto?.let {
            if (tipo == TipoMovimiento.INGRESO) it.stockDisponible + cantidad
            else it.stockDisponible - cantidad
        }

    val quedaBajoMinimo: Boolean
        get() {
            val p = producto ?: return false
            val resultante = stockResultante ?: return false
            return resultante <= p.stockMinimo
        }
}

@HiltViewModel
class MovimientoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    productoRepository: ProductoRepository,
    private val movimientoRepository: MovimientoRepository,
    private val notificador: NotificadorStock,
    sesion: SesionUsuario
) : ViewModel() {

    private val productoId: Long = checkNotNull(savedStateHandle.get<Long>("productoId"))
    private val tipoInicial = TipoMovimiento.valueOf(
        savedStateHandle.get<String>("tipo") ?: TipoMovimiento.SALIDA.name
    )

    private val _uiState = MutableStateFlow(
        MovimientoUiState(
            tipo = tipoInicial,
            motivo = motivoPorDefecto(tipoInicial),
            fecha = SimpleDateFormat("dd/MM/yyyy", Locale("es", "CL")).format(Date()),
            responsable = sesion.nombre ?: "Sin sesión"
        )
    )
    val uiState: StateFlow<MovimientoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            productoRepository.observarPorId(productoId).collect { producto ->
                _uiState.update { it.copy(producto = producto) }
            }
        }
    }

    fun onTipoChange(tipo: TipoMovimiento) = _uiState.update {
        val nuevo = it.copy(tipo = tipo, motivo = motivoPorDefecto(tipo), errorGeneral = null)
        nuevo.copy(errorCantidad = errorDe(nuevo))
    }

    fun onMotivoChange(motivo: MotivoMovimiento) = _uiState.update { it.copy(motivo = motivo) }

    fun onCantidadDelta(delta: Int) = _uiState.update {
        val nuevo = it.copy(
            cantidad = (it.cantidad + delta).coerceAtLeast(1),
            errorGeneral = null
        )
        nuevo.copy(errorCantidad = errorDe(nuevo))
    }

    fun registrar() {
        val actual = _uiState.value
        val error = errorDe(actual)
        if (actual.producto == null || error != null) {
            _uiState.update { it.copy(errorCantidad = error) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, errorGeneral = null) }
            val resultado = movimientoRepository.registrar(
                productoId = productoId,
                tipo = actual.tipo,
                cantidad = actual.cantidad,
                motivo = actual.motivo,
                responsable = actual.responsable
            )
            resultado.onSuccess { actualizado ->
                if (actual.tipo == TipoMovimiento.SALIDA &&
                    actualizado.activo &&
                    actualizado.stockDisponible <= actualizado.stockMinimo
                ) {
                    notificador.avisarStockBajo(actualizado)
                }
            }
            _uiState.update {
                resultado.fold(
                    onSuccess = { _ -> it.copy(cargando = false, guardado = true) },
                    onFailure = { e -> it.copy(cargando = false, errorGeneral = e.message) }
                )
            }
        }
    }

    private fun errorDe(estado: MovimientoUiState): String? {
        val producto = estado.producto ?: return null
        return Validadores.cantidadMovimiento(
            cantidad = estado.cantidad,
            stockActual = producto.stockDisponible,
            esSalida = estado.tipo == TipoMovimiento.SALIDA
        )
    }

    private fun motivoPorDefecto(tipo: TipoMovimiento) =
        if (tipo == TipoMovimiento.INGRESO) MotivoMovimiento.RECEPCION else MotivoMovimiento.VENTA
}