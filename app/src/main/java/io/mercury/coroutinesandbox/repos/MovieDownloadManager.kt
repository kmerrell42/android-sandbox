package io.mercury.coroutinesandbox.repos

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieDownloadManager @Inject constructor(private val downloadedMovieStore: DownloadedMovieStore) {
    private val _downloadJobs = HashMap<String, DownloadStatus>()
    private val downloadJobsPublisher = MutableSharedFlow<HashMap<String, DownloadStatus>>(replay = 1)
    val downloadJobs: Flow<HashMap<String, DownloadStatus>>
        get() = downloadJobsPublisher.asSharedFlow()

    private val downloadMoviesPublisher = MutableSharedFlow<Set<String>>(replay = 1)
    val downloadMovies: Flow<Set<String>>
        get() = downloadMoviesPublisher.asSharedFlow()

    init {
        downloadJobsPublisher.tryEmit(_downloadJobs)
        downloadMoviesPublisher.tryEmit(downloadedMovieStore.getIds())
    }

    suspend fun download(id: String) {
        if (!_downloadJobs.containsKey(id)) {
            var i = 0
            while (currentCoroutineContext().isActive && i <= 100) {
                delay(100)
                i++
                _downloadJobs[id] = (DownloadStatus(id, i))
                downloadJobsPublisher.emit(_downloadJobs)
            }
            _downloadJobs.remove(id)
            downloadJobsPublisher.emit(_downloadJobs)
            downloadedMovieStore.add(id)
            downloadMoviesPublisher.emit(downloadedMovieStore.getIds())
        }

    }

    data class DownloadStatus(val id: String, val percent: Int)
}