package io.mercury.coroutinesandbox.view.compose

import androidx.compose.runtime.Composable
import io.mercury.coroutinesandbox.BuildConfig

@Composable
fun ComposeIfDebug(action: @Composable () -> Unit) {
    if (BuildConfig.DEBUG) {
        action()
    }
}