package io.mercury.coroutinesandbox.view.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import io.mercury.coroutinesandbox.usecases.GetTime
import io.mercury.coroutinesandbox.view.DateTimeFormatter
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * Factory method for the Clock composable to manage dependencies.
 */
class ClockFactory @Inject constructor(
    private val getTime: GetTime,
    private val dateTimeFormatter: DateTimeFormatter
) {
    @Composable
    fun Clock(coroutineContext: CoroutineContext, modifier: Modifier = Modifier) {
        val state = getTime.invoke()
            .collectAsState(context = coroutineContext, initial = System.currentTimeMillis())

        Box(modifier = modifier.background(MaterialTheme.colors.secondary)) {
            Text(
                text = dateTimeFormatter.formatClockTime(state.value),
                color = MaterialTheme.colors.onSecondary
            )
        }
    }
}