package com.example.cappucaresuministrosmedicos.viewModel

import com.example.cappucaresuministrosmedicos.MainDispatcherRule
import com.example.cappucaresuministrosmedicos.model.pokemon.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class PokemonAleatorioViewModelTest {
    @get:Rule val mainRule = MainDispatcherRule()

    @Test fun transformaRespuestaPokemonAEstadoUi() = runTest(mainRule.dispatcher) {
        val repo = mockk<PokemonRepository>()
        coEvery { repo.getPokemon(any()) } returns Pokemon(
            id = 25, name = "mr-mime", height = 13, weight = 545,
            sprites = Sprites(frontDefault = "front.png"),
            types = listOf(Types(type = Type(name = "electric")))
        )
        val vm = PokemonAleatorioViewModel(repo)
        vm.llamar()
        advanceUntilIdle()
        val state = vm.uiState.value
        assertTrue(state.visible)
        assertFalse(state.cargando)
        assertEquals(25, state.pokemon?.id)
        assertEquals("Mr Mime", state.pokemon?.nombre)
        assertEquals("54,5 kg", state.pokemon?.peso)
        assertEquals("1,3 m", state.pokemon?.altura)
        assertEquals("Eléctrico", state.pokemon?.tipos?.first()?.etiqueta)
        assertEquals("front.png", state.pokemon?.imagenUrl)
        coVerify { repo.getPokemon(match { it.toInt() in 1..1025 }) }
    }

    @Test fun usaValoresPorDefectoCuandoDatosPokemonSonNulos() = runTest(mainRule.dispatcher) {
        val repo = mockk<PokemonRepository>()
        coEvery { repo.getPokemon(any()) } returns Pokemon()
        val vm = PokemonAleatorioViewModel(repo)
        vm.llamar()
        advanceUntilIdle()
        assertEquals(0, vm.uiState.value.pokemon?.id)
        assertEquals("Desconocido", vm.uiState.value.pokemon?.nombre)
        assertEquals("—", vm.uiState.value.pokemon?.peso)
        assertEquals("—", vm.uiState.value.pokemon?.altura)
        assertNull(vm.uiState.value.pokemon?.imagenUrl)
    }

    @Test fun traduceErroresDeRedY404() = runTest(mainRule.dispatcher) {
        val repo = mockk<PokemonRepository>()
        coEvery { repo.getPokemon(any()) } throws PokemonException.Network(java.io.IOException())
        val vm = PokemonAleatorioViewModel(repo)
        vm.llamar()
        advanceUntilIdle()
        assertEquals("Sin conexión. Revisa tu internet e inténtalo de nuevo.", vm.uiState.value.error)

        coEvery { repo.getPokemon(any()) } throws PokemonException.NotFound()
        vm.llamar()
        advanceUntilIdle()
        assertEquals("No se encontró el Pokémon.", vm.uiState.value.error)
    }

    @Test fun traduceErroresServidorYDesconocido() = runTest(mainRule.dispatcher) {
        val repo = mockk<PokemonRepository>()
        coEvery { repo.getPokemon(any()) } throws PokemonException.Server(503)
        val vm = PokemonAleatorioViewModel(repo)
        vm.llamar()
        advanceUntilIdle()
        assertEquals("El servidor respondió con un error (503).", vm.uiState.value.error)

        coEvery { repo.getPokemon(any()) } throws PokemonException.Unknown(RuntimeException())
        vm.llamar()
        advanceUntilIdle()
        assertEquals("Ocurrió un error inesperado.", vm.uiState.value.error)
    }

    @Test fun traduceLosTiposPokemonConocidosYDesconocidos() = runTest(mainRule.dispatcher) {
        val repo = mockk<PokemonRepository>()
        val nombres = listOf(
            "normal", "fire", "water", "electric", "grass", "ice", "fighting",
            "poison", "ground", "flying", "psychic", "bug", "rock", "ghost",
            "dragon", "dark", "steel", "fairy", "mystery"
        )
        coEvery { repo.getPokemon(any()) } returns Pokemon(
            id = 6, name = "charizard-mega-x", types = nombres.map {
                Types(type = Type(name = it))
            } + Types(type = null)
        )
        val vm = PokemonAleatorioViewModel(repo)
        vm.llamar()
        advanceUntilIdle()
        val pokemon = vm.uiState.value.pokemon!!
        assertEquals("Charizard Mega X", pokemon.nombre)
        assertEquals(
            listOf(
                "Normal", "Fuego", "Agua", "Eléctrico", "Planta", "Hielo", "Lucha",
                "Veneno", "Tierra", "Volador", "Psíquico", "Bicho", "Roca", "Fantasma",
                "Dragón", "Siniestro", "Acero", "Hada", "Mystery", "Normal"
            ),
            pokemon.tipos.map { it.etiqueta }
        )
    }

    @Test fun cerrarRestableceEstadoInicial() = runTest(mainRule.dispatcher) {
        val repo = mockk<PokemonRepository>()
        coEvery { repo.getPokemon(any()) } returns Pokemon(id = 1, name = "bulbasaur")
        val vm = PokemonAleatorioViewModel(repo)
        vm.llamar()
        advanceUntilIdle()
        vm.cerrar()
        assertEquals(PokemonAleatorioUiState(), vm.uiState.value)
    }
}