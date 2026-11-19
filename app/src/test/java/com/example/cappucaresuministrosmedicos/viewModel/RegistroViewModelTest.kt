package com.example.cappucaresuministrosmedicos.viewModel

import com.example.cappucaresuministrosmedicos.MainDispatcherRule
import com.example.cappucaresuministrosmedicos.model.UsuarioRepository
import com.example.cappucaresuministrosmedicos.model.local.entity.PerfilUsuario
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class RegistroViewModelTest {
    @get:Rule val mainRule = MainDispatcherRule()

    private fun completar(vm: RegistroViewModel) {
        vm.onNombreChange("Ana")
        vm.onCorreoChange(" ANA@EXAMPLE.COM ")
        vm.onContrasenaChange("clave123")
        vm.onConfirmacionChange("clave123")
        vm.onPerfilChange(PerfilUsuario.ENCARGADO)
    }

    @Test fun estadoInicialYEdicionDeCampos() {
        val vm = RegistroViewModel(mockk())
        assertEquals(PerfilUsuario.AUTORIZADO, vm.uiState.value.perfil)
        vm.onNombreChange("A")
        assertEquals("Debe tener al menos 3 caracteres", vm.uiState.value.errorNombre)
        vm.onCorreoChange("invalido")
        assertEquals("Ingresa un correo válido", vm.uiState.value.errorCorreo)
        vm.onContrasenaChange("abc")
        assertNotNull(vm.uiState.value.errorContrasena)
        vm.onConfirmacionChange("otra")
        assertEquals("Las contraseñas no coinciden", vm.uiState.value.errorConfirmacion)
    }

    @Test fun registrarNoInvocaRepositorioSiHayErrores() = runTest(mainRule.dispatcher) {
        val repo = mockk<UsuarioRepository>(relaxed = true)
        val vm = RegistroViewModel(repo)
        vm.registrar()
        advanceUntilIdle()
        assertNotNull(vm.uiState.value.errorNombre)
        coVerify(exactly = 0) { repo.registrar(any(), any(), any(), any()) }
    }

    @Test fun registroValidoNormalizaCorreoYFinalizaExitosamente() = runTest(mainRule.dispatcher) {
        val repo = mockk<UsuarioRepository>()
        coEvery { repo.registrar(any(), any(), any(), any()) } returns Result.success(Unit)
        val vm = RegistroViewModel(repo)
        completar(vm)
        vm.registrar()
        advanceUntilIdle()
        assertTrue(vm.uiState.value.registroExitoso)
        assertFalse(vm.uiState.value.cargando)
        coVerify {
            repo.registrar("Ana", "ana@example.com", "clave123", PerfilUsuario.ENCARGADO)
        }
    }

    @Test fun errorDelRepositorioSeReflejaEnEstado() = runTest(mainRule.dispatcher) {
        val repo = mockk<UsuarioRepository>()
        coEvery { repo.registrar(any(), any(), any(), any()) } returns
            Result.failure(IllegalStateException("Correo duplicado"))
        val vm = RegistroViewModel(repo)
        completar(vm)
        vm.registrar()
        advanceUntilIdle()
        assertEquals("Correo duplicado", vm.uiState.value.errorGeneral)
        assertFalse(vm.uiState.value.cargando)
    }

    @Test fun cambiarContrasenaRevalidaConfirmacionExistente() {
        val vm = RegistroViewModel(mockk())
        vm.onContrasenaChange("clave123")
        vm.onConfirmacionChange("clave123")
        assertNull(vm.uiState.value.errorConfirmacion)
        vm.onContrasenaChange("clave456")
        assertEquals("Las contraseñas no coinciden", vm.uiState.value.errorConfirmacion)
    }
}
