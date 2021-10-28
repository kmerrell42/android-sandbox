package io.mercury.coroutinesandbox.interactors

import io.mercury.coroutinesandbox.interactors.GetDownloadedStatuses.DownloadState.Downloaded
import io.mercury.coroutinesandbox.interactors.GetDownloadedStatuses.DownloadState.Downloading
import io.mercury.coroutinesandbox.interactors.GetDownloadedStatuses.DownloadState.NotDownloaded
import io.mercury.domain.interactors.GetMovies
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetDownloadedStatuses @Inject constructor(
    val getMovies: GetMovies,
    val getDownloadedMovies: GetDownloadedMovies,
    val getInProgressDownloads: GetInProgressDownloads
) {
    operator fun invoke(): Flow<Map<String, DownloadState>> {
        val allMovies = getMovies()
        return getDownloadedMovies().combine(getInProgressDownloads()) { downloaded, inprogress ->
            HashMap<String, DownloadState>().also { stateMap ->
                allMovies.forEach { movie ->
                    if (downloaded.find { it.id == movie.id } != null) {
                        Downloaded
                    } else {
                        inprogress.find { it.id == movie.id }?.let { inProgressDownload ->
                            Downloading(inProgressDownload.downloadProgress)
                        } ?: run {
                            NotDownloaded
                        }
                    }.also { downloadState ->
                            stateMap[movie.id] = downloadState
                        }
                }
            }
        }
    }

    sealed class DownloadState {
        object NotDownloaded : DownloadState()
        object Downloaded : DownloadState()
        data class Downloading(val percent: Int) : DownloadState()
    }
}