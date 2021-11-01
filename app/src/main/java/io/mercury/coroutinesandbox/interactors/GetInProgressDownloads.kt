package io.mercury.coroutinesandbox.interactors

import io.mercury.coroutinesandbox.repos.MovieDownloadManager
import io.mercury.domain.interactors.GetMovies
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetInProgressDownloads @Inject constructor(
    private val downloadManager: MovieDownloadManager,
    private val getMovies: GetMovies
) {
    operator fun invoke(): Flow<List<InProgressDownload>> {
        return downloadManager.downloadStatuses
            .combine(flow { emit(getMovies()) }) { jobs, movies ->
                arrayListOf<InProgressDownload>().also { list ->
                    for ((id, percentage) in jobs) {
                        movies.find { it.id == id }?.let { movie ->
                            list.add(InProgressDownload(id, movie.title, percentage))
                        }
                    }
                }
            }
    }

    data class InProgressDownload(val id: String, val title: String, val downloadProgress: Int)
}