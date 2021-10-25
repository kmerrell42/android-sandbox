package io.mercury.coroutinesandbox.model

import com.squareup.moshi.Json

data class Movie(
    @field:Json(name = "title") val title: String)