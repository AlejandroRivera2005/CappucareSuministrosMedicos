package com.example.cappucaresuministrosmedicos.viewModel

import com.example.cappucaresuministrosmedicos.MainDispatcherRule
import com.example.cappucaresuministrosmedicos.model.SesionUsuario
import com.example.cappucaresuministrosmedicos.model.UsuarioRepository
import com.example.cappucaresuministrosmedicos.model.local.entity.PerfilUsuario
import com.example.cappucaresuministrosmedicos.model.local.entity.UsuarioEntity
import io.mockk.coEvery
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
class LoginViewModelTest {
    @get:Rule val mainRule = MainDispatcherRule()

    @Test fun camposInvalidosBloqueanLogin() = runTest(mainRule.dispatcher) {
        val repo = mockk<UsuarioRepository>()
        coEvery { repo.sembrarEncargadoDemo() } returns Unit
        val vm = LoginViewModel(repo, SesionUsuario())
        advanceUntilIdle()
        vm.iniciarSesion()
        assertEquals("El correo es obligatorio", vm.uiState.value.errorCorreo)
        assertEquals("La contraseña es obligatoria", vm.uiState.value.errorContrasena)
    }

    @Test fun loginExitosoActualizaSesionYPerfil() = runTest(mainRule.dispatcher) {
        val user = UsuarioEntity(
            id = 1, nombre = "Encargada", correo = "ana@example.com",
            perfil = PerfilUsuario.ENCARGADO, contrasenaHash = "hash", aprobado = true
        )
        val repo = mockk<UsuarioRepository>()
        coEvery { repo.sembrarEncargadoDemo() } returns Unit
        coEvery { repo.iniciarSesion("ana@example.com", "clave123") } returns Result.success(user)
        val sesion = SesionUsuario()
        val vm = LoginViewModel(repo, sesion)
        advanceUntilIdle()
        vm.onCorreoChange(" ANA@EXAMPLE.COM ")
        vm.onContrasenaChange("clave123")
        vm.iniciarSesion()
        advanceUntilIdle()
        assertEquals(PerfilUsuario.ENCARGADO, vm.uiState.value.perfilIngresado)
        assertEquals("Encargada", sesion.nombre)
        assertEquals(PerfilUsuario.ENCARGADO, sesion.perfil)
        assertFalse(vm.uiState.value.cargando)
    }

    @Test fun errorDeAutenticacionSeMuestraEnEstado() = runTest(mainRule.dispatcher) {
        val repo = mockk<UsuarioRepository>()
        coEvery { repo.sembrarEncargadoDemo() } returns Unit
        coEvery { repo.iniciarSesion("a@example.com", "incorrecta") } returns
            Result.failure(IllegalArgumentException("Credenciales incorrectas"))
        val vm = LoginViewModel(repo, SesionUsuario())
        advanceUntilIdle()
        vm.onCorreoChange("a@example.com")
        vm.onContrasenaChange("incorrecta")
        vm.iniciarSesion()
        advanceUntilIdle()
        assertEquals("Credenciales incorrectas", vm.uiState.value.errorGeneral)
    }

    @Test fun cambiarCamposLimpiaErrorGeneral() = runTest(mainRule.dispatcher) {
        val repo = mockk<UsuarioRepository>()
        coEvery { repo.sembrarEncargadoDemo() } returns Unit
        val vm = LoginViewModel(repo, SesionUsuario())
        advanceUntilIdle()
        vm.onCorreoChange("bad")
        assertNotNull(vm.uiState.value.errorCorreo)
        vm.onCorreoChange("ok@example.com")
        assertNull(vm.uiState.value.errorCorreo)
        assertNull(vm.uiState.value.errorGeneral)
    }
}
