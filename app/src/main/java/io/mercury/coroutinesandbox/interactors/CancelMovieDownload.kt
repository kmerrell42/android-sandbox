package io.mercury.coroutinesandbox.interactors

import io.mercury.coroutinesandbox.repos.MovieDownloadManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CancelMovieDownload @Inject constructor(private val movieDownloadManager: MovieDownloadManager) {
    operator fun invoke(id: String) {
        movieDownloadManager.cancelDownload(id)
    }
}