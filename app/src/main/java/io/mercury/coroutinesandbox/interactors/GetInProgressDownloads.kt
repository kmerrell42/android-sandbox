package io.mercury.coroutinesandbox.interactors

import io.mercury.coroutinesandbox.repos.MovieDownloadManager
import io.mercury.domain.interactors.GetMovies
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetInProgressDownloads @Inject constructor(
    private val downloadManager: MovieDownloadManager,
    private val getMovies: GetMovies
) {
    operator fun invoke(): Flow<List<InProgressDownload>> {
        val moviesFlow = flowOf(getMovies())
        return downloadManager.downloadJobs
            .combine(moviesFlow) { jobs, movies ->
                jobs.values.mapNotNull { downloadStatus ->
                    movies.find { it.id == downloadStatus.id }?.let { movie ->
                        InProgressDownload(downloadStatus.id, movie.title, downloadStatus.percent)
                    }
                }
            }
    }

    data class InProgressDownload(val id: String, val title: String, val downloadProgress: Int)
}