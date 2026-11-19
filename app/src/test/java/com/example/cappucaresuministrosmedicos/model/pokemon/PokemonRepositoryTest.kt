package com.example.cappucaresuministrosmedicos.model.pokemon

import com.example.cappucaresuministrosmedicos.model.pokemon.remote.ApiService
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.IOException

class PokemonRepositoryTest : StringSpec({
    val api = mockk<ApiService>()
    val repository = DefaultPokemonRepository(api)

    "devuelve pokemon cuando la respuesta es exitosa" {
        val pokemon = Pokemon(id = 25, name = "pikachu")
        coEvery { api.getPokemon("25") } returns Response.success(pokemon)
        runTest { repository.getPokemon("25") shouldBe pokemon }
    }

    "convierte 404 en NotFound" {
        coEvery { api.getPokemon("missing") } returns
            Response.error(404, "missing".toResponseBody())
        runTest {
            shouldThrow<PokemonException.NotFound> {
                repository.getPokemon("missing")
            }
        }
    }

    "convierte respuesta HTTP no exitosa en Server" {
        coEvery { api.getPokemon("1") } returns
            Response.error(500, "server".toResponseBody())
        runTest {
            val error = shouldThrow<PokemonException.Server> { repository.getPokemon("1") }
            error.code shouldBe 500
        }
    }

    "convierte body nulo exitoso en Server" {
        coEvery { api.getPokemon("2") } returns Response.success(null)
        runTest {
            val error = shouldThrow<PokemonException.Server> { repository.getPokemon("2") }
            error.code shouldBe 200
        }
    }

    "convierte IOException en Network" {
        coEvery { api.getPokemon("25") } throws IOException("offline")
        runTest {
            shouldThrow<PokemonException.Network> { repository.getPokemon("25") }
        }
    }

    "convierte excepción inesperada en Unknown" {
        coEvery { api.getPokemon("25") } throws IllegalArgumentException("bad")
        runTest {
            shouldThrow<PokemonException.Unknown> { repository.getPokemon("25") }
        }
    }

    "propaga cancelación de coroutine" {
        coEvery { api.getPokemon("25") } throws CancellationException("cancel")
        runTest {
            shouldThrow<CancellationException> { repository.getPokemon("25") }
        }
    }
})
