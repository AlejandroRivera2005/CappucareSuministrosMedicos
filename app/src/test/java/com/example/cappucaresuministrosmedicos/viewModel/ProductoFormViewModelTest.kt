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

class ProductoFormViewModelTest {
    @get:Rule val mainRule = MainDispatcherRule()

    private fun validVm(repo: ProductoRepository): ProductoFormViewModel {
        val vm = ProductoFormViewModel(SavedStateHandle(), repo)
        vm.onIdChange("44")
        vm.onNombreChange("Guantes")
        vm.onDescripcionChange("Caja nitrilo")
        vm.onCategoriaChange("Protección")
        vm.onStockChange("12")
        vm.onUnidadChange("cajas")
        vm.onStockMinimoChange("3")
        vm.onLugarChange("Bodega A")
        return vm
    }

    @Test fun estadoInicialEsCreacion() {
        val repo = mockk<ProductoRepository>()
        val vm = ProductoFormViewModel(SavedStateHandle(), repo)
        assertFalse(vm.uiState.value.esEdicion)
        assertFalse(vm.uiState.value.guardado)
        assertEquals("", vm.uiState.value.id)
    }

    @Test fun cambiosDeCamposActualizanEstadoYValidaciones() {
        val vm = ProductoFormViewModel(SavedStateHandle(), mockk())
        vm.onNombreChange("")
        assertEquals("El nombre es obligatorio", vm.uiState.value.errorNombre)
        vm.onNombreChange("Guantes")
        assertNull(vm.uiState.value.errorNombre)
        vm.onActivoChange(false)
        assertFalse(vm.uiState.value.activo)
    }

    @Test fun guardarConCamposInvalidosNoInvocaRepositorio() = runTest(mainRule.dispatcher) {
        val repo = mockk<ProductoRepository>(relaxed = true)
        val vm = ProductoFormViewModel(SavedStateHandle(), repo)
        vm.guardar()
        advanceUntilIdle()
        assertNotNull(vm.uiState.value.errorId)
        assertNotNull(vm.uiState.value.errorNombre)
        coVerify(exactly = 0) { repo.insertar(any()) }
    }

    @Test fun crearProductoValidoLoInserta() = runTest(mainRule.dispatcher) {
        val repo = mockk<ProductoRepository>()
        every { repo.observarPorId(44L) } returns flowOf(null)
        coEvery { repo.insertar(any()) } returns Unit
        val vm = validVm(repo)
        vm.guardar()
        advanceUntilIdle()
        assertTrue(vm.uiState.value.guardado)
        assertFalse(vm.uiState.value.guardando)
        coVerify {
            repo.insertar(match {
                it.id == 44L && it.nombre == "Guantes" &&
                    it.stockDisponible == 12 && it.stockMinimo == 3
            })
        }
    }

    @Test fun crearProductoConIdDuplicadoMuestraError() = runTest(mainRule.dispatcher) {
        val repo = mockk<ProductoRepository>()
        every { repo.observarPorId(44L) } returns flowOf(
            ProductoEntity(44, "Otro", "", "X", 1, "u", 0, "B")
        )
        val vm = validVm(repo)
        vm.guardar()
        advanceUntilIdle()
        assertEquals("Ya existe un producto con ese ID", vm.uiState.value.errorId)
        assertFalse(vm.uiState.value.guardado)
    }

    @Test fun errorDeRepositorioSeExponeComoErrorGeneral() = runTest(mainRule.dispatcher) {
        val repo = mockk<ProductoRepository>()
        every { repo.observarPorId(44L) } returns flowOf(null)
        coEvery { repo.insertar(any()) } throws IllegalStateException("db")
        val vm = validVm(repo)
        vm.guardar()
        advanceUntilIdle()
        assertEquals("No se pudo guardar el producto", vm.uiState.value.errorGeneral)
        assertFalse(vm.uiState.value.guardando)
    }

    @Test fun modoEdicionCargaDatosExistentesYActualiza() = runTest(mainRule.dispatcher) {
        val producto = ProductoEntity(9, "Mascarilla", "Desc", "Protección", 20, "unid.", 5, "A", false)
        val repo = mockk<ProductoRepository>()
        every { repo.observarPorId(9L) } returns flowOf(producto)
        coEvery { repo.actualizar(any()) } returns Unit
        val vm = ProductoFormViewModel(SavedStateHandle(mapOf("productoId" to 9L)), repo)
        advanceUntilIdle()
        assertTrue(vm.uiState.value.esEdicion)
        assertEquals("Mascarilla", vm.uiState.value.nombre)
        assertFalse(vm.uiState.value.activo)
        vm.guardar()
        advanceUntilIdle()
        assertTrue(vm.uiState.value.guardado)
        coVerify { repo.actualizar(match { it.id == 9L && it.nombre == "Mascarilla" }) }
    }

    @Test fun modoEdicionInformaProductoInexistente() = runTest(mainRule.dispatcher) {
        val repo = mockk<ProductoRepository>()
        every { repo.observarPorId(9L) } returns flowOf(null)
        val vm = ProductoFormViewModel(SavedStateHandle(mapOf("productoId" to 9L)), repo)
        advanceUntilIdle()
        assertEquals("El producto no existe", vm.uiState.value.errorGeneral)
        assertFalse(vm.uiState.value.cargandoInicial)
    }
}
