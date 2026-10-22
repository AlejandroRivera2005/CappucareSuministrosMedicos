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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductoFormUiState(
    val esEdicion: Boolean = false,
    val cargandoInicial: Boolean = false,

    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val stockDisponible: String = "",
    val unidadMedida: String = "",
    val stockMinimo: String = "",
    val lugarAlmacenamiento: String = "",
    val activo: Boolean = true,

    val errorId: String? = null,
    val errorNombre: String? = null,
    val errorDescripcion: String? = null,
    val errorCategoria: String? = null,
    val errorStock: String? = null,
    val errorUnidad: String? = null,
    val errorStockMinimo: String? = null,
    val errorLugar: String? = null,

    val guardando: Boolean = false,
    val errorGeneral: String? = null,
    val guardado: Boolean = false
)

@HiltViewModel
class ProductoFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repositorio: ProductoRepository
) : ViewModel() {

    private val productoId: Long? = savedStateHandle.get<Long>("productoId")

    private val _uiState = MutableStateFlow(
        ProductoFormUiState(
            esEdicion = productoId != null,
            cargandoInicial = productoId != null
        )
    )
    val uiState: StateFlow<ProductoFormUiState> = _uiState.asStateFlow()

    init {
        if (productoId != null) {
            viewModelScope.launch {
                val p = repositorio.observarPorId(productoId).first()
                _uiState.update {
                    if (p == null) {
                        it.copy(cargandoInicial = false, errorGeneral = "El producto no existe")
                    } else {
                        it.copy(
                            cargandoInicial = false,
                            id = p.id.toString(),
                            nombre = p.nombre,
                            descripcion = p.descripcion,
                            categoria = p.categoria,
                            stockDisponible = p.stockDisponible.toString(),
                            unidadMedida = p.unidadMedida,
                            stockMinimo = p.stockMinimo.toString(),
                            lugarAlmacenamiento = p.lugarAlmacenamiento,
                            activo = p.activo
                        )
                    }
                }
            }
        }
    }

    fun onIdChange(v: String) = _uiState.update {
        it.copy(id = v, errorId = Validadores.idProducto(v), errorGeneral = null)
    }

    fun onNombreChange(v: String) = _uiState.update {
        it.copy(nombre = v, errorNombre = Validadores.textoObligatorio(v, "El nombre"), errorGeneral = null)
    }

    fun onDescripcionChange(v: String) = _uiState.update {
        it.copy(
            descripcion = v,
            errorDescripcion = Validadores.textoObligatorio(v, "La descripción"),
            errorGeneral = null
        )
    }

    fun onCategoriaChange(v: String) = _uiState.update {
        it.copy(categoria = v, errorCategoria = Validadores.textoObligatorio(v, "La categoría"), errorGeneral = null)
    }

    fun onStockChange(v: String) = _uiState.update {
        it.copy(stockDisponible = v, errorStock = Validadores.entero(v, "El stock"), errorGeneral = null)
    }

    fun onUnidadChange(v: String) = _uiState.update {
        it.copy(
            unidadMedida = v,
            errorUnidad = Validadores.textoObligatorio(v, "La unidad de medida"),
            errorGeneral = null
        )
    }

    fun onStockMinimoChange(v: String) = _uiState.update {
        it.copy(stockMinimo = v, errorStockMinimo = Validadores.entero(v, "El stock mínimo"), errorGeneral = null)
    }

    fun onLugarChange(v: String) = _uiState.update {
        it.copy(
            lugarAlmacenamiento = v,
            errorLugar = Validadores.textoObligatorio(v, "El lugar de almacenamiento"),
            errorGeneral = null
        )
    }

    fun onActivoChange(v: Boolean) = _uiState.update { it.copy(activo = v) }

    fun guardar() {
        val a = _uiState.value

        val errId = if (a.esEdicion) null else Validadores.idProducto(a.id)
        val errNombre = Validadores.textoObligatorio(a.nombre, "El nombre")
        val errDescripcion = Validadores.textoObligatorio(a.descripcion, "La descripción")
        val errCategoria = Validadores.textoObligatorio(a.categoria, "La categoría")
        val errStock = Validadores.entero(a.stockDisponible, "El stock")
        val errUnidad = Validadores.textoObligatorio(a.unidadMedida, "La unidad de medida")
        val errMinimo = Validadores.entero(a.stockMinimo, "El stock mínimo")
        val errLugar = Validadores.textoObligatorio(a.lugarAlmacenamiento, "El lugar de almacenamiento")

        val hayErrores = listOf(
            errId, errNombre, errDescripcion, errCategoria, errStock, errUnidad, errMinimo, errLugar
        ).any { it != null }

        if (hayErrores) {
            _uiState.update {
                it.copy(
                    errorId = errId,
                    errorNombre = errNombre,
                    errorDescripcion = errDescripcion,
                    errorCategoria = errCategoria,
                    errorStock = errStock,
                    errorUnidad = errUnidad,
                    errorStockMinimo = errMinimo,
                    errorLugar = errLugar
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(guardando = true, errorGeneral = null) }
            try {
                val id = productoId ?: a.id.trim().toLong()

                if (productoId == null && repositorio.observarPorId(id).first() != null) {
                    _uiState.update {
                        it.copy(guardando = false, errorId = "Ya existe un producto con ese ID")
                    }
                    return@launch
                }

                val producto = ProductoEntity(
                    id = id,
                    nombre = a.nombre.trim(),
                    descripcion = a.descripcion.trim(),
                    categoria = a.categoria.trim(),
                    stockDisponible = a.stockDisponible.trim().toInt(),
                    unidadMedida = a.unidadMedida.trim(),
                    stockMinimo = a.stockMinimo.trim().toInt(),
                    lugarAlmacenamiento = a.lugarAlmacenamiento.trim(),
                    activo = a.activo
                )

                if (productoId == null) repositorio.insertar(producto)
                else repositorio.actualizar(producto)

                _uiState.update { it.copy(guardando = false, guardado = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(guardando = false, errorGeneral = "No se pudo guardar el producto")
                }
            }
        }
    }
}