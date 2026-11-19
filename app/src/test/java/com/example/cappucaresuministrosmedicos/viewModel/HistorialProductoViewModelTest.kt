
package com.example.cappucaresuministrosmedicos.viewModel

import androidx.lifecycle.SavedStateHandle
import com.example.cappucaresuministrosmedicos.MainDispatcherRule
import com.example.cappucaresuministrosmedicos.model.MovimientoRepository
import com.example.cappucaresuministrosmedicos.model.local.entity.MotivoMovimiento
import com.example.cappucaresuministrosmedicos.model.local.entity.MovimientoEntity
import com.example.cappucaresuministrosmedicos.model.local.entity.TipoMovimiento
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
class HistorialProductoViewModelTest {
    @get:Rule val mainRule = MainDispatcherRule()

    @Test fun consultaMovimientosDelProductoIndicadoYLosMapea() = runTest(mainRule.dispatcher) {
        val dao = mockk<MovimientoRepository>()
        val movimiento = MovimientoEntity(
            id = 12, productoId = 7, tipo = TipoMovimiento.SALIDA,
            cantidad = 4, motivo = MotivoMovimiento.MERMA,
            fecha = 1_700_000_000_000L, responsable = "Bodega"
        )
        every { dao.observarPorProducto(7L) } returns flowOf(listOf(movimiento))
        val vm = HistorialProductoViewModel(
            SavedStateHandle(mapOf("productoId" to 7L)), dao
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.movimientos.collect {} }
        advanceUntilIdle()
        assertEquals(1, vm.movimientos.value.size)
        assertEquals(12L, vm.movimientos.value.single().id)
        assertEquals("", vm.movimientos.value.single().productoNombre)
        assertEquals("Merma", vm.movimientos.value.single().motivo)
        assertFalse(vm.movimientos.value.single().esIngreso)
    }

    @Test fun flujoVacioEntregaListaVacia() = runTest(mainRule.dispatcher) {
        val dao = mockk<MovimientoRepository>()
        every { dao.observarPorProducto(7L) } returns flowOf(emptyList())
        val vm = HistorialProductoViewModel(SavedStateHandle(mapOf("productoId" to 7L)), dao)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.movimientos.collect {} }
        advanceUntilIdle()
        assertTrue(vm.movimientos.value.isEmpty())
    }
}

