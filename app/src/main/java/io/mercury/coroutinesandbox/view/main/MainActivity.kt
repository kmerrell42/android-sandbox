package io.mercury.coroutinesandbox.view.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.mercury.coroutinesandbox.R.string
import io.mercury.coroutinesandbox.view.main.MainFeature.Action
import io.mercury.coroutinesandbox.view.main.MainFeature.Action.Cancel
import io.mercury.coroutinesandbox.view.main.MainFeature.Action.Download
import io.mercury.coroutinesandbox.view.main.MainFeature.Action.Unload
import io.mercury.coroutinesandbox.view.main.MainFeature.NewsEvent
import io.mercury.coroutinesandbox.view.main.MainFeature.NewsEvent.Percent50
import io.mercury.coroutinesandbox.view.main.MainFeature.State
import io.mercury.coroutinesandbox.view.main.MainFeature.State.Downloaded
import io.mercury.coroutinesandbox.view.main.MainFeature.State.Downloading
import io.mercury.coroutinesandbox.view.main.MainFeature.State.Unloaded
import io.mercury.coroutinesandbox.view.theme.ThemedMaterial
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val actionPublisher = MutableSharedFlow<Action>(replay = 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val model: MainViewModel by viewModels()

        setContent { ReactiveScreen(model.feature.state) }

        lifecycleScope.launchWhenCreated { actionPublisher.collect(model.feature::onAction) }
        lifecycleScope.launchWhenCreated { model.feature.newsEvent.collect(::onNewsBroadcast) }
    }

    private fun onNewsBroadcast(newsEvent: NewsEvent) {
        when (newsEvent) {
            is Percent50 -> {
                Toast.makeText(this, getString(string.half_way_there), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun publishAction(action: Action) {
        lifecycleScope.launch {
            actionPublisher.emit(action)
        }
    }

    @Composable
    fun MainScreen(state: State) {
        ThemedMaterial {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
                    .padding(24.dp)
            ) {

                UpdateStatus(state = state, modifier = Modifier.align(Alignment.Center))

                Button(
                    onClick = state.toButtonAction(),
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Text(text = state.toButtonLabel())
                }
            }
        }
    }

    @Composable
    fun UpdateStatus(state: State, modifier: Modifier = Modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .height(60.dp)
                .wrapContentWidth()
        ) {
            Text(
                text = state.toMessage(),
                style = MaterialTheme.typography.body1,
            )

            when (state) {
                is Downloading -> {
                    val animatedProgress by animateFloatAsState(
                        targetValue = state.percentCompete * .01f,
                        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
                    )
                    LinearProgressIndicator(progress = animatedProgress)
                }
                is Downloaded -> LinearProgressIndicator(progress = 1f)
                else -> {
                    // Don't add the progress bar
                }
            }
        }
    }

    @Preview
    @Composable
    fun PreviewMainScreen() {
        MainScreen(Downloading(35))
    }

    @Composable
    fun ReactiveScreen(state: StateFlow<State>) {
        val composeState by state.collectAsState(lifecycleScope.coroutineContext)

        MainScreen(composeState)
    }

    private fun State.toMessage(): String {
        return when (this) {
            is Unloaded -> getString(string.update_available)
            is Downloading -> getString(string.download_percent_format, percentCompete)
            is Downloaded -> getString(string.download_complete)
        }
    }

    private fun State.toButtonLabel(): String {
        return when (this) {
            is Unloaded -> getString(string.download_update)
            is Downloading -> getString(string.cancel)
            is Downloaded -> getString(string.unload)
        }
    }

    private fun State.toButtonAction(): () -> Unit {
        return when (this) {
            is Unloaded -> {
                { publishAction(Download) }
            }
            is Downloading -> {
                { publishAction(Cancel) }
            }
            is Downloaded -> {
                { publishAction(Unload) }
            }
        }
    }
}