
package com.example.cappucaresuministrosmedicos.viewModel

import com.example.cappucaresuministrosmedicos.MainDispatcherRule
import com.example.cappucaresuministrosmedicos.model.UsuarioRepository
import com.example.cappucaresuministrosmedicos.model.local.entity.PerfilUsuario
import com.example.cappucaresuministrosmedicos.model.local.entity.UsuarioEntity
import io.mockk.coEvery
import io.mockk.coVerify
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
class UsuariosViewModelTest {
    @get:Rule val mainRule = MainDispatcherRule()

    @Test fun transformaUsuariosPendientesYPerfil() = runTest(mainRule.dispatcher) {
        val repo = mockk<UsuarioRepository>()
        every { repo.observarPendientes() } returns flowOf(listOf(
            UsuarioEntity(1, "Eva", "eva@test.cl", PerfilUsuario.ENCARGADO, "hash"),
            UsuarioEntity(2, "Luis", "luis@test.cl", PerfilUsuario.AUTORIZADO, "hash")
        ))
        val vm = UsuariosViewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
        advanceUntilIdle()
        assertFalse(vm.uiState.value.cargando)
        assertEquals(listOf("Encargado", "Autorizado"), vm.uiState.value.pendientes.map { it.perfil })
        assertEquals(listOf(1L, 2L), vm.uiState.value.pendientes.map { it.id })
    }

    @Test fun aprobarYRechazarDeleganAlRepositorio() = runTest(mainRule.dispatcher) {
        val repo = mockk<UsuarioRepository>()
        every { repo.observarPendientes() } returns flowOf(emptyList())
        coEvery { repo.aprobar(7L) } returns Unit
        coEvery { repo.rechazar(8L) } returns Unit
        val vm = UsuariosViewModel(repo)
        vm.aprobar(7L)
        vm.rechazar(8L)
        advanceUntilIdle()
        coVerify(exactly = 1) { repo.aprobar(7L) }
        coVerify(exactly = 1) { repo.rechazar(8L) }
    }
}

