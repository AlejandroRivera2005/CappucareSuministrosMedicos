
package com.example.cappucaresuministrosmedicos.viewModel

import com.example.cappucaresuministrosmedicos.MainDispatcherRule
import com.example.cappucaresuministrosmedicos.model.ProductoRepository
import com.example.cappucaresuministrosmedicos.model.local.entity.ProductoEntity
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductoListaViewModelTest {
    @get:Rule val mainRule = MainDispatcherRule()

    private fun producto(id: Long, nombre: String, categoria: String) = ProductoEntity(
        id, nombre, "", categoria, 10, "unid.", 2, "Bodega"
    )

    @Test fun listaCompletaYTotalSeActualizan() = runTest(mainRule.dispatcher) {
        val repo = mockk<ProductoRepository>()
        every { repo.observarTodos() } returns flowOf(
            listOf(producto(1, "Guantes", "Protección"), producto(2, "Alcohol", "Higiene"))
        )
        val vm = ProductoListaViewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
        advanceUntilIdle()
        assertEquals(2, vm.uiState.value.totalProductos)
        assertEquals(2, vm.uiState.value.productos.size)
        assertEquals(false, vm.uiState.value.cargando)
    }

    @Test fun busquedaFiltraPorNombreCategoriaEIdSinDistinguirMayusculas() =
        runTest(mainRule.dispatcher) {
            val repo = mockk<ProductoRepository>()
            every { repo.observarTodos() } returns flowOf(
                listOf(
                    producto(12, "Guantes", "Protección"),
                    producto(20, "Alcohol", "Higiene"),
                    producto(31, "Mascarilla", "Protección")
                )
            )
            val vm = ProductoListaViewModel(repo)
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
            advanceUntilIdle()

            vm.onBusquedaChange(" GUAN ")
            advanceUntilIdle()
            assertEquals(listOf(12L), vm.uiState.value.productos.map { it.id })

            vm.onBusquedaChange("protección")
            advanceUntilIdle()
            assertEquals(listOf(12L, 31L), vm.uiState.value.productos.map { it.id })

            vm.onBusquedaChange("20")
            advanceUntilIdle()
            assertEquals(listOf(20L), vm.uiState.value.productos.map { it.id })
            assertEquals(3, vm.uiState.value.totalProductos)
        }

    @Test fun busquedaVaciaMuestraTodosLosProductos() = runTest(mainRule.dispatcher) {
        val repo = mockk<ProductoRepository>()
        every { repo.observarTodos() } returns flowOf(listOf(producto(1, "A", "C"), producto(2, "B", "D")))
        val vm = ProductoListaViewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
        vm.onBusquedaChange("no existe")
        advanceUntilIdle()
        assertEquals(0, vm.uiState.value.productos.size)
        vm.onBusquedaChange(" ")
        advanceUntilIdle()
        assertEquals(2, vm.uiState.value.productos.size)
        assertEquals(" ", vm.busqueda.value)
    }
}
