package io.mercury.coroutinesandbox.view.main

import io.mercury.coroutinesandbox.usecases.DownloadUpdate
import io.mercury.coroutinesandbox.view.main.MainFeature.Action.Unload
import io.mercury.coroutinesandbox.view.main.MainFeature.Event.Download
import io.mercury.coroutinesandbox.view.main.MainFeature.NewsEvent.Percent50
import io.mercury.coroutinesandbox.view.main.MainFeature.State.Downloaded
import io.mercury.coroutinesandbox.view.main.MainFeature.State.Downloading
import io.mercury.coroutinesandbox.view.main.MainFeature.State.Unloaded
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainFeature(
    private val downloadUpdate: DownloadUpdate,
    private val scope: CoroutineScope
) {
    private var downloadJob: Job? = null

    private val statePublisher = MutableStateFlow<State>(Unloaded)
    val state get() = statePublisher.asStateFlow()

    private val newsPublisher = Channel<NewsEvent>()
    val newsEvent = newsPublisher.receiveAsFlow()

    fun onAction(action: Action) {
        when (action) {
            is Action.Download -> handleDownloadAction()
            is Action.Cancel -> handleCancelAction()
            is Unload -> handleUnloadAction()
        }
    }

    private fun handleCancelAction() {
        if (state.value is Downloading) {
            process(Event.Cancel)
        }
    }

    private fun handleUnloadAction() {
        if (state.value is Downloaded) {
            process(Event.Unload)
        }
    }

    private fun handleDownloadAction() {
        if (state.value !is Downloaded && statePublisher.value !is Downloading) {
            process(Download)
        }
    }

    private fun process(event: Event) {
        when (event) {
            is Download -> {
                downloadJob?.cancel()
                downloadJob = scope.launch {
                    withContext(Dispatchers.IO) {
                        downloadUpdate.invoke()
                    }.let { downloadUpdateFlow ->
                        downloadUpdateFlow.collect { percentDownloaded ->
                            if (percentDownloaded == 50) {
                                publishNewsEvent(Percent50)
                            }
                            if (percentDownloaded < 100) {
                                updateState(Downloading(percentDownloaded))
                            } else {
                                updateState(Downloaded)
                            }
                        }
                    }
                }
            }
            is Event.Cancel -> {
                downloadJob?.cancel()
                scope.launch { updateState(Unloaded) }
            }
            is Event.Unload -> scope.launch { updateState(Unloaded) }
        }
    }

    private suspend fun publishNewsEvent(newsEvent: NewsEvent) {
        newsPublisher.send(newsEvent)
    }

    private suspend fun updateState(newState: State) {
        statePublisher.emit(newState)
    }

    sealed class State {
        object Unloaded : State()
        data class Downloading(val percentCompete: Int) : State()
        object Downloaded : State()
    }

    sealed class Action {
        object Download : Action()
        object Cancel : Action()
        object Unload : Action()
    }

    private sealed class Event {
        object Download : Event()
        object Unload : Event()
        object Cancel : Event()
    }

    sealed class NewsEvent {
        object Percent50 : NewsEvent()
    }
}