package com.example.cappucaresuministrosmedicos.model.pokemon

import com.google.gson.annotations.SerializedName

data class Pokemon(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("height")
    val height: Int? = null,
    @SerializedName("weight")
    val weight: Int? = null,
    @SerializedName("sprites")
    val sprites: Sprites? = Sprites(),
    @SerializedName("types")
    val types: List<Types>? = null
) {
    val imageUrl: String?
        get() = sprites?.other?.officialArtwork?.frontDefault ?: sprites?.frontDefault
}

data class Sprites(
    @SerializedName("front_default")
    val frontDefault: String? = null,
    @SerializedName("other")
    val other: OtherSprites? = null
)

data class OtherSprites(
    @SerializedName("official-artwork")
    val officialArtwork: OfficialArtwork? = null
)

data class OfficialArtwork(
    @SerializedName("front_default")
    val frontDefault: String? = null
)

data class Types(
    @SerializedName("slot")
    val slot: Int? = null,
    @SerializedName("type")
    val type: Type? = Type()
)

data class Type(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("url")
    val url: String? = null
)