package io.mercury.coroutinesandbox.view.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.mercury.coroutinesandbox.view.component.ClockFactory
import io.mercury.coroutinesandbox.view.downloader.DownloadListActivity
import io.mercury.coroutinesandbox.view.downloader.DownloaderActivity
import io.mercury.coroutinesandbox.view.ext.startActivity
import io.mercury.coroutinesandbox.view.multirow.MultiRowActivity
import io.mercury.coroutinesandbox.view.theme.ThemedMaterial
import io.mercury.coroutinesandbox.view.theming.ThemeActivity
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var clockFactory: ClockFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Root()
        }
    }

    @Composable
    fun Root() {
        ThemedMaterial {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                clockFactory.Clock(lifecycleScope.coroutineContext)

                Button(onClick = {
                    startActivity(DownloaderActivity::class.java)
                }) {
                    Text("Downloader")
                }

                Button(onClick = {
                    startActivity(DownloadListActivity::class.java)
                }) {
                    Text("Download List")
                }

                Button(onClick = {
                    startActivity(MultiRowActivity::class.java)
                }) {
                    Text("Multi-row display of movies")
                }

                Button(onClick = {
                    startActivity(ThemeActivity::class.java)
                }) {
                    Text("Dynamic Theme")
                }
            }
        }
    }

    @Preview
    @Composable
    fun PreviewRoot() {
        Root()
    }
}