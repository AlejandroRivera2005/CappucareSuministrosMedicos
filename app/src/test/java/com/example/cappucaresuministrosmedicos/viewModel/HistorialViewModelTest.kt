
package com.example.cappucaresuministrosmedicos.viewModel

import com.example.cappucaresuministrosmedicos.MainDispatcherRule
import com.example.cappucaresuministrosmedicos.model.MovimientoRepository
import com.example.cappucaresuministrosmedicos.model.ProductoRepository
import com.example.cappucaresuministrosmedicos.model.local.entity.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HistorialViewModelTest {
    @get:Rule val mainRule = MainDispatcherRule()

    private val movimientos = listOf(
        MovimientoEntity(id = 1, productoId = 10, tipo = TipoMovimiento.INGRESO,
            cantidad = 5, motivo = MotivoMovimiento.RECEPCION, fecha = 1L, responsable = "A"),
        MovimientoEntity(id = 2, productoId = 99, tipo = TipoMovimiento.SALIDA,
            cantidad = 2, motivo = MotivoMovimiento.VENTA, fecha = 2L, responsable = "B")
    )

    @Test fun combinaMovimientosYNombreDeProducto() = runTest(mainRule.dispatcher) {
        val movRepo = mockk<MovimientoRepository>()
        val prodRepo = mockk<ProductoRepository>()
        every { movRepo.observarRecientes(100) } returns flowOf(movimientos)
        every { prodRepo.observarTodos() } returns flowOf(
            listOf(ProductoEntity(10, "Guantes", "", "X", 5, "u", 2, "A"))
        )
        val vm = HistorialViewModel(movRepo, prodRepo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
        advanceUntilIdle()
        assertEquals(2, vm.uiState.value.items.size)
        assertEquals("Guantes", vm.uiState.value.items[0].productoNombre)
        assertEquals("Producto", vm.uiState.value.items[1].productoNombre)
        assertFalse(vm.uiState.value.cargando)
    }

    @Test fun filtraIngresosYSalidas() = runTest(mainRule.dispatcher) {
        val movRepo = mockk<MovimientoRepository>()
        val prodRepo = mockk<ProductoRepository>()
        every { movRepo.observarRecientes(100) } returns flowOf(movimientos)
        every { prodRepo.observarTodos() } returns flowOf(emptyList())
        val vm = HistorialViewModel(movRepo, prodRepo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
        advanceUntilIdle()

        vm.onFiltroChange(FiltroHistorial.INGRESOS)
        advanceUntilIdle()
        assertEquals(FiltroHistorial.INGRESOS, vm.uiState.value.filtro)
        assertEquals(listOf(1L), vm.uiState.value.items.map { it.id })

        vm.onFiltroChange(FiltroHistorial.SALIDAS)
        advanceUntilIdle()
        assertEquals(listOf(2L), vm.uiState.value.items.map { it.id })

        vm.onFiltroChange(FiltroHistorial.TODOS)
        advanceUntilIdle()
        assertEquals(2, vm.uiState.value.items.size)
    }
}
