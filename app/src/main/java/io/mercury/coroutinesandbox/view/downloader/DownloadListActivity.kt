package io.mercury.coroutinesandbox.view.downloader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.mercury.coroutinesandbox.interactors.DownloadMovie
import io.mercury.coroutinesandbox.view.downloader.DownloadListFeature.State
import io.mercury.coroutinesandbox.view.downloader.DownloadListFeature.State.Error
import io.mercury.coroutinesandbox.view.downloader.DownloadListFeature.State.Loaded
import io.mercury.coroutinesandbox.view.downloader.DownloadListFeature.State.Loading
import io.mercury.coroutinesandbox.view.downloader.DownloadListFeature.State.Uninitialized
import io.mercury.coroutinesandbox.view.downloader.State.DownloadsAvailable
import io.mercury.coroutinesandbox.view.theme.ThemedMaterial
import javax.inject.Inject

@AndroidEntryPoint
class DownloadListActivity : ComponentActivity() {

    @Inject
    lateinit var downloadMovie: DownloadMovie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val model: DownloadListViewModel by viewModels()

        setContent {
            val composeState by model.feature.state.collectAsState(lifecycleScope.coroutineContext)
            Root(composeState, model.downloaderFeature)
        }

        model.feature.load()
    }

    @Composable
    fun Root(state: State, downloaderFeature: CheeseyDownloaderFeature) {
        ThemedMaterial {
            Box(
                Modifier
                    .background(MaterialTheme.colors.background)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (state) {
                    is Uninitialized,
                    is Loading -> Text("Loading...")
                    is Error -> Text("ERROR")
                    is Loaded -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color.LightGray)
                                    .fillMaxWidth()
                            ) {
                                Text("Downloading:")
                                LazyColumn {
                                    items(state.inProgressDownloads) { download ->
                                        Text("${download.title} - ${download.downloadProgress}%")
                                    }
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color.Cyan)
                                    .fillMaxWidth()
                            ) {
                                Text("Finished:")
                                LazyColumn {
                                    items(state.finishedDownloads) { download ->
                                        Text(download.title)
                                    }
                                }
                            }
                        }
                    }
                }

                val downloaderState = remember { downloaderFeature.state }.collectAsState()
                val remainingDownloads = downloaderState.value.let { state ->
                    if (state is DownloadsAvailable) {
                        state.downloadables.size - state.downloaded.size
                    } else {
                        0
                    }
                }
                Button(
                    onClick = { downloaderFeature.download() },
                    modifier = Modifier.align(Alignment.BottomEnd),
                    enabled = downloaderState.value is DownloadsAvailable
                ) {
                    Text("Download Something ($remainingDownloads)")
                }
            }
        }
    }
}