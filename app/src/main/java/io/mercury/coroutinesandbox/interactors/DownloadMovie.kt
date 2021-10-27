package io.mercury.coroutinesandbox.interactors

import io.mercury.coroutinesandbox.repos.MovieDownloadManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadMovie @Inject constructor(private val downloadManager: MovieDownloadManager) {
    suspend operator fun invoke(id: String) {
        downloadManager.download(id)
    }
}