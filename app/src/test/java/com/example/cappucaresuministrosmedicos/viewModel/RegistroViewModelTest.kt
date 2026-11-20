package com.example.cappucaresuministrosmedicos.viewModel

import com.example.cappucaresuministrosmedicos.model.UsuarioRepository
import com.example.cappucaresuministrosmedicos.model.local.entity.PerfilUsuario
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
@OptIn(ExperimentalCoroutinesApi::class)
class RegistroViewModelTest {

    private val repo = mockk<UsuarioRepository>(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun crearViewModel() = RegistroViewModel(repo)

    private fun RegistroViewModel.llenarFormularioValido() {
        onNombreChange("Ana Pérez")
        onCorreoChange("ana@correo.cl")
        onContrasenaChange("Clave1234")
        onConfirmacionChange("Clave1234")
    }

    @Test
    fun `estado inicial vacio con perfil autorizado`() {
        val vm = crearViewModel()

        assertEquals("", vm.uiState.value.nombre)
        assertEquals(PerfilUsuario.AUTORIZADO, vm.uiState.value.perfil)
        assertFalse(vm.uiState.value.registroExitoso)
        assertFalse(vm.uiState.value.cargando)
    }

    @Test
    fun `nombre y correo se validan al escribir`() {
        val vm = crearViewModel()

        vm.onNombreChange("")
        assertNotNull(vm.uiState.value.errorNombre)
        vm.onNombreChange("Ana Pérez")
        assertNull(vm.uiState.value.errorNombre)

        vm.onCorreoChange("malo")
        assertNotNull(vm.uiState.value.errorCorreo)
        vm.onCorreoChange("ana@correo.cl")
        assertNull(vm.uiState.value.errorCorreo)
    }

    @Test
    fun `contrasena debil muestra error y valida la confirmacion solo si ya existe`() {
        val vm = crearViewModel()

        // sin confirmacion escrita: no se calcula error de confirmacion
        vm.onContrasenaChange("abc")
        assertNotNull(vm.uiState.value.errorContrasena)
        assertNull(vm.uiState.value.errorConfirmacion)

        // con confirmacion escrita: cambiar la contrasena revalida la confirmacion
        vm.onContrasenaChange("Clave1234")
        vm.onConfirmacionChange("Clave1234")
        assertNull(vm.uiState.value.errorConfirmacion)

        vm.onContrasenaChange("Otra12345")
        assertNotNull(vm.uiState.value.errorConfirmacion)
    }

    @Test
    fun `confirmacion distinta de la contrasena muestra error`() {
        val vm = crearViewModel()
        vm.onContrasenaChange("Clave1234")

        vm.onConfirmacionChange("Distinta99")
        assertNotNull(vm.uiState.value.errorConfirmacion)

        vm.onConfirmacionChange("Clave1234")
        assertNull(vm.uiState.value.errorConfirmacion)
    }

    @Test
    fun `cambiar perfil actualiza el estado`() {
        val vm = crearViewModel()

        vm.onPerfilChange(PerfilUsuario.ENCARGADO)

        assertEquals(PerfilUsuario.ENCARGADO, vm.uiState.value.perfil)
    }

    @Test
    fun `registrar con formulario vacio no llama al repositorio y marca errores`() {
        val vm = crearViewModel()

        vm.registrar()

        assertNotNull(vm.uiState.value.errorNombre)
        assertNotNull(vm.uiState.value.errorCorreo)
        assertNotNull(vm.uiState.value.errorContrasena)
        assertFalse(vm.uiState.value.registroExitoso)
        coVerify(exactly = 0) { repo.registrar(any(), any(), any(), any()) }
    }

    @Test
    fun `registro exitoso llama al repositorio con los datos del formulario`() {
        coEvery { repo.registrar(any(), any(), any(), any()) } returns Result.success(Unit)
        val vm = crearViewModel()
        vm.llenarFormularioValido()
        vm.onPerfilChange(PerfilUsuario.ENCARGADO)

        vm.registrar()

        coVerify {
            repo.registrar("Ana Pérez", "ana@correo.cl", "Clave1234", PerfilUsuario.ENCARGADO)
        }
        assertTrue(vm.uiState.value.registroExitoso)
        assertFalse(vm.uiState.value.cargando)
        assertNull(vm.uiState.value.errorGeneral)
    }

    @Test
    fun `registro fallido muestra el mensaje del repositorio`() {
        coEvery { repo.registrar(any(), any(), any(), any()) } returns
                Result.failure(Exception("El correo ya está registrado"))
        val vm = crearViewModel()
        vm.llenarFormularioValido()

        vm.registrar()

        assertEquals("El correo ya está registrado", vm.uiState.value.errorGeneral)
        assertFalse(vm.uiState.value.registroExitoso)
        assertFalse(vm.uiState.value.cargando)
    }
}