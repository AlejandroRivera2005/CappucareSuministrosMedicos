package com.example.cappucaresuministrosmedicos.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cappucaresuministrosmedicos.model.pokemon.Pokemon
import com.example.cappucaresuministrosmedicos.model.pokemon.PokemonException
import com.example.cappucaresuministrosmedicos.model.pokemon.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import kotlin.random.Random

data class PokemonTipoUi(
    val clave: String,
    val etiqueta: String
)

data class PokemonAleatorioUi(
    val id: Int,
    val nombre: String,
    val imagenUrl: String?,
    val tipos: List<PokemonTipoUi>,
    val peso: String,
    val altura: String
)

data class PokemonAleatorioUiState(
    val visible: Boolean = false,
    val cargando: Boolean = false,
    val pokemon: PokemonAleatorioUi? = null,
    val error: String? = null
)

@HiltViewModel
class PokemonAleatorioViewModel @Inject constructor(
    private val repositorio: PokemonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PokemonAleatorioUiState())
    val uiState: StateFlow<PokemonAleatorioUiState> = _uiState.asStateFlow()

    private var trabajo: Job? = null

    fun llamar() {
        trabajo?.cancel()
        _uiState.value = PokemonAleatorioUiState(visible = true, cargando = true)
        trabajo = viewModelScope.launch {
            val id = Random.nextInt(1, MAX_POKEMON + 1)
            try {
                val pokemon = repositorio.getPokemon(id.toString())
                _uiState.update { it.copy(cargando = false, pokemon = pokemon.aUi()) }
            } catch (e: PokemonException) {
                _uiState.update { it.copy(cargando = false, error = mensajeDe(e)) }
            }
        }
    }

    fun cerrar() {
        trabajo?.cancel()
        _uiState.value = PokemonAleatorioUiState()
    }

    private fun mensajeDe(e: PokemonException): String = when (e) {
        is PokemonException.Network -> "Sin conexión. Revisa tu internet e inténtalo de nuevo."
        is PokemonException.NotFound -> "No se encontró el Pokémon."
        is PokemonException.Server -> "El servidor respondió con un error (${e.code})."
        is PokemonException.Unknown -> "Ocurrió un error inesperado."
    }

    private companion object {
        const val MAX_POKEMON = 1025
    }
}

private fun Pokemon.aUi() = PokemonAleatorioUi(
    id = id ?: 0,
    nombre = name?.let(::formatName) ?: "Desconocido",
    imagenUrl = imageUrl,
    tipos = types.orEmpty().map { tipoUi(it.type?.name ?: "normal") },
    peso = formatTenths(weight, "kg"),
    altura = formatTenths(height, "m")
)

private fun formatName(name: String): String =
    name.split('-')
        .filter { it.isNotEmpty() }
        .joinToString(" ") { palabra -> palabra.replaceFirstChar { it.uppercase() } }

private fun formatTenths(valor: Int?, unidad: String): String =
    if (valor == null) "—"
    else String.format(Locale("es", "CL"), "%.1f %s", valor / 10.0, unidad)

private fun tipoUi(tipo: String): PokemonTipoUi {
    val clave = tipo.lowercase()
    val etiqueta = when (clave) {
        "normal" -> "Normal"
        "fire" -> "Fuego"
        "water" -> "Agua"
        "electric" -> "Eléctrico"
        "grass" -> "Planta"
        "ice" -> "Hielo"
        "fighting" -> "Lucha"
        "poison" -> "Veneno"
        "ground" -> "Tierra"
        "flying" -> "Volador"
        "psychic" -> "Psíquico"
        "bug" -> "Bicho"
        "rock" -> "Roca"
        "ghost" -> "Fantasma"
        "dragon" -> "Dragón"
        "dark" -> "Siniestro"
        "steel" -> "Acero"
        "fairy" -> "Hada"
        else -> tipo.replaceFirstChar { it.uppercase() }
    }
    return PokemonTipoUi(clave, etiqueta)
}