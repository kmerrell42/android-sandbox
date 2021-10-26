package io.mercury.domain.models

import com.squareup.moshi.Json

data class Movie(
    @field:Json(name = "imdbId") val id: String,
    @field:Json(name = "title") val title: String
)