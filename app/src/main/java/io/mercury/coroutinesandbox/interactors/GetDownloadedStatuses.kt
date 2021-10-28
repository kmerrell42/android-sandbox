package io.mercury.coroutinesandbox.interactors

import io.mercury.coroutinesandbox.interactors.GetDownloadedStatuses.DownloadState.Downloaded
import io.mercury.coroutinesandbox.interactors.GetDownloadedStatuses.DownloadState.Downloading
import io.mercury.coroutinesandbox.repos.DownloadedMovieStore
import io.mercury.coroutinesandbox.repos.MovieDownloadManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetDownloadedStatuses @Inject constructor(
    private val downloadedMovieStore: DownloadedMovieStore,
    private val downloadManager: MovieDownloadManager,
) {
    operator fun invoke(): Flow<Map<String, DownloadState>> {
        return downloadManager.downloadJobs
            .combine(flow { emit(downloadedMovieStore.getIds()) }) { inprogress, downloaded ->
                HashMap<String, DownloadState>()
                    .also { stateMap ->
                        downloaded.forEach { id ->
                            stateMap[id] = Downloaded
                        }

                        stateMap.putAll(inprogress.mapValues { entry ->
                            Downloading(entry.value.percent)
                        })

                    }
            }
    }

    sealed class DownloadState {
        object NotDownloaded : DownloadState()
        object Downloaded : DownloadState()
        data class Downloading(val percent: Int) : DownloadState()
    }
}