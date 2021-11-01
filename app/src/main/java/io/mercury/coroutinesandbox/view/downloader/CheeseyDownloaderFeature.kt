package io.mercury.coroutinesandbox.view.downloader

import dagger.hilt.android.scopes.ActivityScoped
import io.mercury.coroutinesandbox.interactors.DownloadMovie
import io.mercury.coroutinesandbox.view.downloader.State.DownloadsAvailable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@ActivityScoped
class CheeseyDownloaderFeature @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val backgroundDispatcher: CoroutineDispatcher,
    val downloadMovie: DownloadMovie
) {
    private val statePublisher =
        MutableStateFlow<State>(DownloadsAvailable.from("tt0068646", "tt0111161", "tt0108052"))
    val state get() = statePublisher.asStateFlow()

    fun download() {
        state.value.also { currentState ->
            if (currentState is DownloadsAvailable) {
                val id = currentState.downloadables.pop()
                coroutineScope.launch(backgroundDispatcher) {
                    downloadMovie(id)
                }

                coroutineScope.launch {
                    statePublisher.emit(State.from(currentState))
                }
            }
        }

    }
}

sealed class State {
    class DownloadsAvailable(val downloadables: Stack<String>) : State() {
        companion object {
            fun from(vararg ids: String): DownloadsAvailable {
                return DownloadsAvailable(Stack<String>().also { stack ->
                    ids.forEach { id ->
                        stack.push(id)
                    }
                })
            }
        }
    }

    object DownloadsUnavailable : State()

    companion object {
        fun from(state: State): State {
            return if (state is DownloadsAvailable && state.downloadables.isNotEmpty()) {
                DownloadsAvailable(state.downloadables)
            } else {
                DownloadsUnavailable
            }
        }
    }
}