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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainFeature(
    private val downloadUpdate: DownloadUpdate,
    private val scope: CoroutineScope
) {
    private var downloadJob: Job? = null

    private val stateConsumers = ArrayList<(State) -> Unit>()
    private val newsConsumers = ArrayList<(NewsEvent) -> Unit>()

    private var state: State = Unloaded
        set(value) {
            field = value
            stateConsumers.forEach { it(value) }
        }

    fun bind(consumer: (State) -> Unit) {
        stateConsumers.add(consumer)
        consumer(state) // Immediately tell the consumer what the existing state is
    }

    fun unbind(consumer: (State) -> Unit) {
        stateConsumers.remove(consumer)
    }

    @JvmName("bindNews")
    fun bind(consumer: (NewsEvent) -> Unit) {
        newsConsumers.add(consumer)
    }

    @JvmName("unbindNews")
    fun unbind(consumer: (NewsEvent) -> Unit) {
        newsConsumers.remove(consumer)
    }

    fun accept(action: Action) {
        when (action) {
            is Action.Download -> handleDownloadAction()
            is Action.Cancel -> handleCancelAction()
            is Unload -> handleUnloadAction()
        }
    }

    private fun handleCancelAction() {
        if (state is Downloading) {
            process(Event.Cancel)
        }
    }

    private fun handleUnloadAction() {
        if (state is Downloaded) {
            process(Event.Unload)
        }
    }

    private fun handleDownloadAction() {
        if (state !is Downloaded && state !is Downloading) {
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
                        downloadUpdateFlow.collect { percentDownladed ->
                            if (percentDownladed == 50) {
                                publishNewsEvent(Percent50)
                            }
                            if (percentDownladed < 100) {
                                updateState(Downloading(percentDownladed))
                            } else {
                                updateState(Downloaded)
                            }
                        }
                    }
                }
            }
            is Event.Cancel -> {
                downloadJob?.cancel()
                updateState(Unloaded)
            }
            is Event.Unload -> updateState(Unloaded)
        }
    }

    private fun publishNewsEvent(newsEvent: NewsEvent) {
        newsConsumers.forEach { it(newsEvent) }
    }

    private fun updateState(newState: State) {
        state = newState
        stateConsumers.forEach { it(newState) }
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