package io.mercury.domain.models

import com.squareup.moshi.Json

data class Movie(
    @field:Json(name = "title") val title: String
)