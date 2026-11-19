package com.example.cappucaresuministrosmedicos.viewModel


import androidx.lifecycle.SavedStateHandle
import com.example.cappucaresuministrosmedicos.MainDispatcherRule
import com.example.cappucaresuministrosmedicos.model.MovimientoRepository
import com.example.cappucaresuministrosmedicos.model.NotificadorStock
import com.example.cappucaresuministrosmedicos.model.ProductoRepository
import com.example.cappucaresuministrosmedicos.model.SesionUsuario
import com.example.cappucaresuministrosmedicos.model.local.entity.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class MovimientoViewModelTest {
    @get:Rule val mainRule = MainDispatcherRule()
    private val producto = ProductoEntity(3, "Alcohol", "", "Higiene", 10, "unid.", 4, "C")

    private fun crearVm(
        tipo: String? = null,
        repo: ProductoRepository = mockk(),
        movRepo: MovimientoRepository = mockk(),
        notifier: NotificadorStock = mockk(relaxed = true)
    ): MovimientoViewModel {
        every { repo.observarPorId(3L) } returns flowOf(producto)
        return MovimientoViewModel(
            SavedStateHandle(buildMap {
                put("productoId", 3L)
                if (tipo != null) put("tipo", tipo)
            }),
            repo, movRepo, notifier,
            SesionUsuario().apply { iniciar("Operador", PerfilUsuario.AUTORIZADO) }
        )
    }

    @Test fun inicializaTipoResponsableYMotivoPorDefecto() = runTest(mainRule.dispatcher) {
        val vm = crearVm("INGRESO")
        advanceUntilIdle()
        assertEquals(TipoMovimiento.INGRESO, vm.uiState.value.tipo)
        assertEquals(MotivoMovimiento.RECEPCION, vm.uiState.value.motivo)
        assertEquals("Operador", vm.uiState.value.responsable)
        assertEquals(producto, vm.uiState.value.producto)
    }

    @Test fun cambiaTipoYReiniciaMotivo() = runTest(mainRule.dispatcher) {
        val vm = crearVm()
        advanceUntilIdle()
        assertEquals(TipoMovimiento.SALIDA, vm.uiState.value.tipo)
        vm.onTipoChange(TipoMovimiento.INGRESO)
        assertEquals(MotivoMovimiento.RECEPCION, vm.uiState.value.motivo)
        vm.onMotivoChange(MotivoMovimiento.AJUSTE)
        assertEquals(MotivoMovimiento.AJUSTE, vm.uiState.value.motivo)
    }

    @Test fun cantidadNoBajaDeUnoYValidaStock() = runTest(mainRule.dispatcher) {
        val vm = crearVm()
        advanceUntilIdle()
        vm.onCantidadDelta(-10)
        assertEquals(1, vm.uiState.value.cantidad)
        assertNull(vm.uiState.value.errorCantidad)
        vm.onCantidadDelta(10)
        assertEquals(11, vm.uiState.value.cantidad)
        assertEquals("No hay stock suficiente (disponible: 10)", vm.uiState.value.errorCantidad)
    }

    @Test fun registroValidoActualizaEstadoYNotificaStockBajo() = runTest(mainRule.dispatcher) {
        val movRepo = mockk<MovimientoRepository>()
        val notifier = mockk<NotificadorStock>(relaxed = true)
        val actualizado = producto.copy(stockDisponible = 3)
        coEvery {
            movRepo.registrar(3L, TipoMovimiento.SALIDA, 1, MotivoMovimiento.VENTA, "Operador")
        } returns Result.success(actualizado)
        val vm = crearVm(movRepo = movRepo, notifier = notifier)
        advanceUntilIdle()
        vm.registrar()
        advanceUntilIdle()
        assertTrue(vm.uiState.value.guardado)
        coVerify { notifier.avisarStockBajo(actualizado) }
    }

    @Test fun salidaInvalidaNoInvocaRepositorio() = runTest(mainRule.dispatcher) {
        val movRepo = mockk<MovimientoRepository>(relaxed = true)
        val vm = crearVm(movRepo = movRepo)
        advanceUntilIdle()
        vm.onCantidadDelta(20)
        vm.registrar()
        advanceUntilIdle()
        assertFalse(vm.uiState.value.guardado)
        coVerify(exactly = 0) { movRepo.registrar(any(), any(), any(), any(), any()) }
    }

    @Test fun falloDelRepositorioSeMuestraComoErrorGeneral() = runTest(mainRule.dispatcher) {
        val movRepo = mockk<MovimientoRepository>()
        coEvery { movRepo.registrar(any(), any(), any(), any(), any()) } returns
            Result.failure(IllegalStateException("Sin stock"))
        val vm = crearVm(movRepo = movRepo)
        advanceUntilIdle()
        vm.registrar()
        advanceUntilIdle()
        assertEquals("Sin stock", vm.uiState.value.errorGeneral)
        assertFalse(vm.uiState.value.cargando)
    }
}
