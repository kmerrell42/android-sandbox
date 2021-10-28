package io.mercury.coroutinesandbox.models

import io.mercury.coroutinesandbox.interactors.GetDownloadedStatuses.DownloadState

data class FavoriteableMovie(
    val id: String,
    val title: String,
    val posterUrl: String,
    val isFavorite: Boolean,
    val downloadState: DownloadState
)