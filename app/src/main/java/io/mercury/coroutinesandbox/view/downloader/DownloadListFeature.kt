package io.mercury.coroutinesandbox.view.downloader

import io.mercury.coroutinesandbox.interactors.GetDownloadedMovies
import io.mercury.coroutinesandbox.interactors.GetInProgressDownloads
import io.mercury.coroutinesandbox.interactors.GetInProgressDownloads.InProgressDownload
import io.mercury.coroutinesandbox.view.downloader.DownloadListFeature.State.Loaded
import io.mercury.coroutinesandbox.view.downloader.DownloadListFeature.State.Uninitialized
import io.mercury.domain.models.Movie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class DownloadListFeature(
    private val scope: CoroutineScope,
    private val getInProgressDownloads: GetInProgressDownloads,
    private val getDownloadedMovies: GetDownloadedMovies
) {

    private val statePublisher = MutableStateFlow<State>(Uninitialized)
    val state get() = statePublisher.asStateFlow()

    fun load() {
        if (state.value is Uninitialized) {
            scope.launch(Dispatchers.IO) {
                getInProgressDownloads().combine(getDownloadedMovies()) { inprogress, downloaded ->
                    Loaded(inprogress, downloaded)
                }.collect {
                    statePublisher.emit(it)
                }
            }
        }
    }

    sealed class State {
        object Uninitialized : State()
        data class Error(val e: Exception) : State()
        object Loading : State()
        data class Loaded(
            val inProgressDownloads: List<InProgressDownload>,
            val finishedDownloads: List<Movie>
        ) : State()
    }
}