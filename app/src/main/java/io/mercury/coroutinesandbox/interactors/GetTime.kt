package io.mercury.coroutinesandbox.interactors

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetTime @Inject constructor() {

    init {
        // TODO: We should probably inject the scope.
        MainScope().launch {
            while (true) {
                timePublisher.emit(System.currentTimeMillis())
                delay(TICK_INTERVAL_MILLIS)
            }
        }
    }

    private val timePublisher = MutableSharedFlow<Long>(replay = 1)

    operator fun invoke(): Flow<Long> {
        return timePublisher
    }

    companion object {
        const val TICK_INTERVAL_MILLIS = 1000L
    }
}