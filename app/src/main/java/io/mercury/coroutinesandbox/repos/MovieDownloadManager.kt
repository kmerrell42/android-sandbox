package io.mercury.coroutinesandbox.repos

import io.mercury.coroutinesandbox.di.SingletonModule.ApplicationCoroutineScope
import io.mercury.coroutinesandbox.di.SingletonModule.BackgroundDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieDownloadManager @Inject constructor(
    @ApplicationCoroutineScope  private val coroutineScope: CoroutineScope,
    @BackgroundDispatcher private val backgroundDispatcher: CoroutineDispatcher,
    private val downloadedMovieStore: DownloadedMovieStore
) {
    private val downloadJobs = HashMap<String, Job>()

    private val _downloadStatuses = hashMapOf<String, Int>()
    private val downloadStatusesPublisher = MutableSharedFlow<HashMap<String, Int>>(replay = 1)
    val downloadStatuses: Flow<HashMap<String, Int>>
        get() = downloadStatusesPublisher.asSharedFlow()

    private val downloadMoviesPublisher = MutableSharedFlow<Set<String>>(replay = 1)
    val downloadMovies: Flow<Set<String>>
        get() = downloadMoviesPublisher.asSharedFlow()

    init {
        coroutineScope.launch {
            downloadStatusesPublisher.emit(_downloadStatuses)
            downloadMoviesPublisher.emit(downloadedMovieStore.getIds())
        }
    }

    fun download(id: String) {
        if (!downloadJobs.containsKey(id)) {
            downloadJobs[id] = coroutineScope.launch(backgroundDispatcher) {
                var i = 0
                while (isActive && i < 100) {
                    delay(100)
                    i++
                    _downloadStatuses[id] = i
                    downloadStatusesPublisher.emit(_downloadStatuses)
                }

                if (i >= 100) { // We downloaded the whole movie without being canceled

                    downloadedMovieStore.add(id)
                    downloadMoviesPublisher.emit(downloadedMovieStore.getIds())
                }

                _downloadStatuses.remove(id)
                downloadJobs.remove(id)
            }
        }
    }

    fun cancelDownload(id: String) {
        downloadJobs[id]?.also { it.cancel() }
        _downloadStatuses.remove(id)

        coroutineScope.launch {
            downloadStatusesPublisher.emit(_downloadStatuses)
        }

    }
}