package io.mercury.coroutinesandbox.usecases

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import javax.inject.Inject

class DownloadUpdate @Inject constructor() {
    suspend fun invoke(): Flow<Int> {
        return flow {
            var i = 0
            while (currentCoroutineContext().isActive && i <= 100) {
                delay(100)
                emit(i)
                i++
            }
        }
    }
}