package com.example.cappucaresuministrosmedicos.viewModel

import com.example.cappucaresuministrosmedicos.model.SesionUsuario
import com.example.cappucaresuministrosmedicos.model.UsuarioRepository
import com.example.cappucaresuministrosmedicos.model.local.entity.PerfilUsuario
import com.example.cappucaresuministrosmedicos.model.local.entity.UsuarioEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val repo = mockk<UsuarioRepository>(relaxed = true)
    private val sesion = mockk<SesionUsuario>(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun crearViewModel() = LoginViewModel(repo, sesion)

    private fun usuarioFalso(): UsuarioEntity = mockk {
        every { nombre } returns "Ana"
        every { perfil } returns PerfilUsuario.ENCARGADO
    }

    @Test
    fun `al crearse siembra el encargado demo y parte con estado vacio`() {
        val vm = crearViewModel()

        coVerify(exactly = 1) { repo.sembrarEncargadoDemo() }
        assertEquals("", vm.uiState.value.correo)
        assertEquals("", vm.uiState.value.contrasena)
        assertFalse(vm.uiState.value.cargando)
        assertNull(vm.uiState.value.perfilIngresado)
    }

    @Test
    fun `correo invalido muestra error y correo valido lo limpia`() {
        val vm = crearViewModel()

        vm.onCorreoChange("no-es-correo")
        assertEquals("no-es-correo", vm.uiState.value.correo)
        assertNotNull(vm.uiState.value.errorCorreo)

        vm.onCorreoChange("ana@correo.cl")
        assertNull(vm.uiState.value.errorCorreo)
    }

    @Test
    fun `contrasena vacia muestra error y con texto lo limpia`() {
        val vm = crearViewModel()

        vm.onContrasenaChange("")
        assertEquals("La contraseña es obligatoria", vm.uiState.value.errorContrasena)

        vm.onContrasenaChange("Clave1234")
        assertNull(vm.uiState.value.errorContrasena)
        assertEquals("Clave1234", vm.uiState.value.contrasena)
    }

    @Test
    fun `iniciar sesion con campos vacios no llama al repositorio y marca errores`() {
        val vm = crearViewModel()

        vm.iniciarSesion()

        assertNotNull(vm.uiState.value.errorCorreo)
        assertEquals("La contraseña es obligatoria", vm.uiState.value.errorContrasena)
        coVerify(exactly = 0) { repo.iniciarSesion(any(), any()) }
    }

    @Test
    fun `iniciar sesion con correo valido y contrasena vacia marca solo la contrasena`() {
        val vm = crearViewModel()
        vm.onCorreoChange("ana@correo.cl")

        vm.iniciarSesion()

        assertNull(vm.uiState.value.errorCorreo)
        assertNotNull(vm.uiState.value.errorContrasena)
        coVerify(exactly = 0) { repo.iniciarSesion(any(), any()) }
    }

    @Test
    fun `inicio de sesion exitoso guarda la sesion y expone el perfil`() {
        coEvery { repo.iniciarSesion(any(), any()) } returns Result.success(usuarioFalso())
        val vm = crearViewModel()
        vm.onCorreoChange("ana@correo.cl")
        vm.onContrasenaChange("Clave1234")

        vm.iniciarSesion()

        coVerify { repo.iniciarSesion("ana@correo.cl", "Clave1234") }
        coVerify { sesion.iniciar("Ana", PerfilUsuario.ENCARGADO) }
        assertEquals(PerfilUsuario.ENCARGADO, vm.uiState.value.perfilIngresado)
        assertFalse(vm.uiState.value.cargando)
        assertNull(vm.uiState.value.errorGeneral)
    }

    @Test
    fun `inicio de sesion fallido muestra el mensaje de error`() {
        coEvery { repo.iniciarSesion(any(), any()) } returns
                Result.failure(Exception("Credenciales incorrectas"))
        val vm = crearViewModel()
        vm.onCorreoChange("ana@correo.cl")
        vm.onContrasenaChange("Clave1234")

        vm.iniciarSesion()

        assertEquals("Credenciales incorrectas", vm.uiState.value.errorGeneral)
        assertNull(vm.uiState.value.perfilIngresado)
        assertFalse(vm.uiState.value.cargando)
        coVerify(exactly = 0) { sesion.iniciar(any(), any()) }
    }
}