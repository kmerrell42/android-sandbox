package io.mercury.coroutinesandbox.view.downloader

import dagger.hilt.android.scopes.ActivityScoped
import io.mercury.coroutinesandbox.interactors.DownloadMovie
import io.mercury.coroutinesandbox.view.downloader.State.DownloadsAvailable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityScoped
class CheeseyDownloaderFeature @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val backgroundDispatcher: CoroutineDispatcher,
    val downloadMovie: DownloadMovie
) {
    private val statePublisher =
        MutableStateFlow<State>(DownloadsAvailable(DOWNLOADABLES, arrayListOf()))
    val state get() = statePublisher.asStateFlow()

    fun download() {
        state.value.also { currentState ->
            if (currentState is DownloadsAvailable) {
                val id = currentState.downloadables.first { !currentState.downloaded.contains(it) }
                coroutineScope.launch(backgroundDispatcher) {
                    downloadMovie(id)
                }

                coroutineScope.launch {
                    statePublisher.emit(State.from(currentState, id))
                }
            }
        }
    }

    companion object {
        private val DOWNLOADABLES: List<String> = arrayListOf("tt0068646", "tt0111161", "tt0108052")
    }
}

sealed class State {
    data class DownloadsAvailable(val downloadables: List<String>, val downloaded: List<String>) :
        State()

    object DownloadsUnavailable : State()

    companion object {
        fun from(state: State, downloadingId: String): State {
            return if (state is DownloadsAvailable && state.downloadables.size > state.downloaded.size + 1) {
                DownloadsAvailable(
                    state.downloadables,
                    arrayListOf(downloadingId).also { it.addAll(state.downloaded) })
            } else {
                DownloadsUnavailable
            }
        }
    }
}