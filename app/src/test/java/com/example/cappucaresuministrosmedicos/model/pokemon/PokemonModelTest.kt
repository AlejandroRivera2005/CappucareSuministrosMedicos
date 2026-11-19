package com.example.cappucaresuministrosmedicos.model.pokemon

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class PokemonModelTest : StringSpec({
    "prefiere artwork oficial sobre sprite frontal" {
        val pokemon = Pokemon(
            sprites = Sprites(
                frontDefault = "front.png",
                other = OtherSprites(OfficialArtwork("official.png"))
            )
        )
        pokemon.imageUrl shouldBe "official.png"
    }

    "usa sprite frontal cuando no existe artwork oficial" {
        Pokemon(sprites = Sprites(frontDefault = "front.png")).imageUrl shouldBe "front.png"
    }

    "imageUrl es nula cuando no hay sprites" {
        Pokemon(sprites = null).imageUrl shouldBe null
    }

    "imageUrl es nula cuando sprites no contienen imágenes" {
        Pokemon(sprites = Sprites()).imageUrl shouldBe null
    }
})
