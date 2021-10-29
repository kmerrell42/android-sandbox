package io.mercury.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    @SerialName("imdbId")val id: String,
    val title: String,
    @SerialName("poster") val posterUrl: String
)