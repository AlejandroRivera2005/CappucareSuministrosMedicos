
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
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AlertasViewModelTest {
    @get:Rule val mainRule = MainDispatcherRule()

    @Test fun obtieneProductosBajoMinimo() = runTest(mainRule.dispatcher) {
        val producto = ProductoEntity(1, "Guantes", "", "P", 2, "cajas", 10, "A")
        val repo = mockk<ProductoRepository>()
        every { repo.observarBajoMinimo() } returns flowOf(listOf(producto))
        val vm = AlertasViewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
        advanceUntilIdle()
        assertFalse(vm.uiState.value.cargando)
        assertEquals(listOf(producto), vm.uiState.value.productos)
    }

    @Test fun generaListaDeReposicionConFaltanteYFecha() = runTest(mainRule.dispatcher) {
        val repo = mockk<ProductoRepository>()
        every { repo.observarBajoMinimo() } returns flowOf(listOf(
            ProductoEntity(1, "Guantes", "", "P", 2, "cajas", 10, "A"),
            ProductoEntity(2, "Agua", "", "P", 0, "unid.", 0, "B")
        ))
        val vm = AlertasViewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
        advanceUntilIdle()
        val texto = vm.textoReposicion()
        assertTrue(texto.startsWith("Lista de reposición Cappucare - "))
        assertTrue(texto.contains("- Guantes: pedir 8 cajas"))
        assertTrue(texto.contains("- Agua: pedir 1 unid."))
    }
}
