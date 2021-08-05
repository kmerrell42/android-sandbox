package io.mercury.coroutinesandbox.view.main

import io.mercury.coroutinesandbox.usecases.GetTimeSlowly
import io.mercury.coroutinesandbox.view.main.MainFeature.Action.Unload
import io.mercury.coroutinesandbox.view.main.MainFeature.Event.Load
import io.mercury.coroutinesandbox.view.main.MainFeature.State.Loaded
import io.mercury.coroutinesandbox.view.main.MainFeature.State.Loading
import io.mercury.coroutinesandbox.view.main.MainFeature.State.Unloaded
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainFeature(
    private val getTimeSlowly: GetTimeSlowly,
    private val scope: CoroutineScope
) {
    private val consumers = ArrayList<(State) -> Unit>()
    private var state: State = Unloaded
        set(value) { consumers.forEach { it(value) } }

    fun bind(consumer: (State) -> Unit) {
        consumers.add(consumer)
        consumer(state) // Immediately tell the consumer what the existing state is
    }

    fun unbind(consumer: (State) -> Unit) {
        consumers.remove(consumer)
    }

    fun accept(action: Action) {
        when (action) {
            is Action.Load -> processLoadAction()
            is Unload -> processUnloadAction()
        }
    }

    private fun processUnloadAction() {
        if (state is Loaded) {
            process(Event.Unload)
        }
    }

    private fun processLoadAction() {
        if (state !is Loaded && state !is Loading) {
            process(Load)
        }
    }

    private fun process(event: Event) {
        scope.launch {
            when (event) {
                is Load -> {
                    updateState(Loading)

                    withContext(Dispatchers.IO) {
                        getTimeSlowly.invoke()
                    }.let { time ->
                        updateState(Loaded(time))
                    }
                }
                Event.Unload -> updateState(Unloaded)
            }
        }
    }

    private fun updateState(newState: State) {
        state = newState
        consumers.forEach { it(newState) }
    }

    sealed class State {
        object Unloaded : State()
        object Loading : State()
        data class Loaded(val time: Long) : State()
    }

    sealed class Action {
        object Load : Action()
        object Unload : Action()
    }

    private sealed class Event {
        object Load : Event()
        object Unload : Event()
    }
}