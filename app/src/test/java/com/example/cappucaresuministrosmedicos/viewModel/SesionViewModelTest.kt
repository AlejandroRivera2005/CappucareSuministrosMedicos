package com.example.cappucaresuministrosmedicos.viewModel

import com.example.cappucaresuministrosmedicos.MainDispatcherRule
import com.example.cappucaresuministrosmedicos.model.SesionUsuario
import com.example.cappucaresuministrosmedicos.model.local.entity.PerfilUsuario
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class SesionViewModelTest {
    @get:Rule val mainRule = MainDispatcherRule()

    @Test fun esEncargadoReflejaPerfilActual() = runTest(mainRule.dispatcher) {
        val sesion = SesionUsuario()
        val vm = SesionViewModel(sesion)
        advanceUntilIdle()
        assertFalse(vm.esEncargado.value)
        sesion.iniciar("Admin", PerfilUsuario.ENCARGADO)
        advanceUntilIdle()
        assertTrue(vm.esEncargado.value)
        sesion.iniciar("Usuario", PerfilUsuario.AUTORIZADO)
        advanceUntilIdle()
        assertFalse(vm.esEncargado.value)
    }
}
