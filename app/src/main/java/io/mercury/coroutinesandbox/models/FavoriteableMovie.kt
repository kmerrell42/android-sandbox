package io.mercury.coroutinesandbox.models

data class FavoriteableMovie(
    val id: String,
    val title: String,
    val posterUrl: String,
    val isFavorite: Boolean
)