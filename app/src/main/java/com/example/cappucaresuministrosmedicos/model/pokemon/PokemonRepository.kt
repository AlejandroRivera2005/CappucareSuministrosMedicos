package com.example.cappucaresuministrosmedicos.model.pokemon

import com.example.cappucaresuministrosmedicos.model.pokemon.remote.ApiService
import kotlinx.coroutines.CancellationException
import java.io.IOException
import java.net.HttpURLConnection.HTTP_NOT_FOUND

sealed class PokemonException(cause: Throwable? = null) : Exception(cause) {
    class Network(cause: Throwable) : PokemonException(cause)
    class NotFound : PokemonException()
    class Server(val code: Int) : PokemonException()
    class Unknown(cause: Throwable) : PokemonException(cause)
}

interface PokemonRepository {
    suspend fun getPokemon(idOrName: String): Pokemon
}

class DefaultPokemonRepository(
    private val api: ApiService
) : PokemonRepository {

    override suspend fun getPokemon(idOrName: String): Pokemon {
        val response = try {
            api.getPokemon(idOrName)
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            throw PokemonException.Network(e)
        } catch (e: Exception) {
            throw PokemonException.Unknown(e)
        }

        if (response.code() == HTTP_NOT_FOUND) throw PokemonException.NotFound()

        return response.body()?.takeIf { response.isSuccessful }
            ?: throw PokemonException.Server(response.code())
    }
}