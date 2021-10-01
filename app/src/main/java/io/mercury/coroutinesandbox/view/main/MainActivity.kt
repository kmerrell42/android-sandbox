package io.mercury.coroutinesandbox.view.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.mercury.coroutinesandbox.R.string
import io.mercury.coroutinesandbox.view.ThemedMaterial
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
    fun MainScreen(msg: String, btnLabel: String, btnAction: () -> Unit) {
        ThemedMaterial {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    text = msg,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.align(Companion.Center)
                )

                Button(onClick = btnAction,
                    modifier = Modifier
                        .align(Companion.BottomCenter)
                        .offset(0.dp, (-24).dp)) {
                    Text(text = btnLabel)
                }
            }
        }
    }

    @Preview
    @Composable
    fun PreviewMainScreen() {
        MainScreen(msg = "Message", btnLabel = "Button Label") {}
    }

    @Composable
    fun ReactiveScreen(state: StateFlow<State>) {
        val composeState by state.collectAsState(lifecycleScope.coroutineContext)

        composeState.toRenderModel().let { renderModel ->
            MainScreen(renderModel.msg, renderModel.btnLabel, renderModel.btnAction)
        }
    }

    private fun State.toRenderModel() : RenderModel {
        return when (this) {
            is Unloaded -> {
                RenderModel(msg = getString(string.update_available),
                    btnLabel = getString(string.download_update),
                    btnAction = { publishAction(Download) })
            }
            is Downloading -> {
                RenderModel(msg = getString(
                    string.download_percent_format,
                    percentCompete
                ),
                    btnLabel = getString(string.cancel),
                    btnAction = { publishAction(Cancel) })
            }
            is Downloaded -> {
                RenderModel(msg = getString(string.download_complete),
                    btnLabel = getString(string.unload),
                    btnAction = { publishAction(Unload) })
            }
        }
    }

    private data class RenderModel(val msg: String, val btnLabel: String, val btnAction: () -> Unit)
}