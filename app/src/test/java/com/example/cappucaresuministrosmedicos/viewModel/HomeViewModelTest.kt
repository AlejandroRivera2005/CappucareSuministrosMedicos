
package com.example.cappucaresuministrosmedicos.viewModel

import com.example.cappucaresuministrosmedicos.MainDispatcherRule
import com.example.cappucaresuministrosmedicos.model.SesionUsuario
import com.example.cappucaresuministrosmedicos.model.UsuarioRepository
import com.example.cappucaresuministrosmedicos.model.local.entity.PerfilUsuario
import com.example.cappucaresuministrosmedicos.model.local.entity.UsuarioEntity
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
class HomeViewModelTest {
    @get:Rule val mainRule = MainDispatcherRule()

    @Test fun encargadoVePendientesYNombre() = runTest(mainRule.dispatcher) {
        val sesion = SesionUsuario().apply { iniciar("María", PerfilUsuario.ENCARGADO) }
        val repo = mockk<UsuarioRepository>()
        every { repo.observarPendientes() } returns flowOf(listOf(
            UsuarioEntity(1, "A", "a@a.cl", PerfilUsuario.AUTORIZADO, "h"),
            UsuarioEntity(2, "B", "b@a.cl", PerfilUsuario.AUTORIZADO, "h")
        ))
        val vm = HomeViewModel(sesion, repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
        advanceUntilIdle()
        assertEquals("María", vm.uiState.value.nombre)
        assertTrue(vm.uiState.value.esEncargado)
        assertEquals(2, vm.uiState.value.pendientes)
        vm.cerrarSesion()
        assertNull(sesion.perfil)
    }

    @Test fun autorizadoNoVeContadorDePendientes() = runTest(mainRule.dispatcher) {
        val sesion = SesionUsuario().apply { iniciar("Pedro", PerfilUsuario.AUTORIZADO) }
        val repo = mockk<UsuarioRepository>()
        every { repo.observarPendientes() } returns flowOf(listOf(
            UsuarioEntity(1, "A", "a@a.cl", PerfilUsuario.AUTORIZADO, "h")
        ))
        val vm = HomeViewModel(sesion, repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
        advanceUntilIdle()
        assertFalse(vm.uiState.value.esEncargado)
        assertEquals(0, vm.uiState.value.pendientes)
    }
}
