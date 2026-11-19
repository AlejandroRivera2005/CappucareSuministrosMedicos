package com.example.cappucaresuministrosmedicos.viewModel


import androidx.lifecycle.SavedStateHandle
import com.example.cappucaresuministrosmedicos.MainDispatcherRule
import com.example.cappucaresuministrosmedicos.model.ProductoRepository
import com.example.cappucaresuministrosmedicos.model.local.entity.ProductoEntity
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

class ProductoDetalleViewModelTest {
    @get:Rule val mainRule = MainDispatcherRule()
    private val producto = ProductoEntity(5, "Gasas", "Estériles", "Curación", 8, "paquetes", 2, "C1")

    @Test fun cargaProductoDesdeSavedState() = runTest(mainRule.dispatcher) {
        val repo = mockk<ProductoRepository>()
        every { repo.observarPorId(5L) } returns flowOf(producto)
        val vm = ProductoDetalleViewModel(SavedStateHandle(mapOf("productoId" to 5L)), repo)
        advanceUntilIdle()
        assertEquals(producto, vm.uiState.value.producto)
        assertFalse(vm.uiState.value.cargando)
    }

    @Test fun eliminarSinProductoNoHaceNada() = runTest(mainRule.dispatcher) {
        val repo = mockk<ProductoRepository>()
        every { repo.observarPorId(5L) } returns flowOf(null)
        val vm = ProductoDetalleViewModel(SavedStateHandle(mapOf("productoId" to 5L)), repo)
        advanceUntilIdle()
        vm.eliminar()
        advanceUntilIdle()
        assertFalse(vm.uiState.value.eliminado)
        coVerify(exactly = 0) { repo.eliminar(any()) }
    }

    @Test fun eliminaProductoYActualizaEstado() = runTest(mainRule.dispatcher) {
        val repo = mockk<ProductoRepository>()
        every { repo.observarPorId(5L) } returns flowOf(producto)
        coEvery { repo.eliminar(producto) } returns Unit
        val vm = ProductoDetalleViewModel(SavedStateHandle(mapOf("productoId" to 5L)), repo)
        advanceUntilIdle()
        vm.eliminar()
        advanceUntilIdle()
        assertTrue(vm.uiState.value.eliminado)
        assertFalse(vm.uiState.value.eliminando)
        coVerify(exactly = 1) { repo.eliminar(producto) }
    }

    @Test fun errorAlEliminarSeMuestraEnEstado() = runTest(mainRule.dispatcher) {
        val repo = mockk<ProductoRepository>()
        every { repo.observarPorId(5L) } returns flowOf(producto)
        coEvery { repo.eliminar(producto) } throws IllegalStateException("db")
        val vm = ProductoDetalleViewModel(SavedStateHandle(mapOf("productoId" to 5L)), repo)
        advanceUntilIdle()
        vm.eliminar()
        advanceUntilIdle()
        assertEquals("No se pudo eliminar el producto", vm.uiState.value.errorGeneral)
        assertFalse(vm.uiState.value.eliminando)
    }
}
