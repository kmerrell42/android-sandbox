package io.mercury.coroutinesandbox.view.component

import android.util.Log
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import dagger.hilt.android.scopes.ActivityScoped
import io.mercury.coroutinesandbox.interactors.GetDownloadedStatus
import io.mercury.coroutinesandbox.interactors.GetDownloadedStatuses.DownloadState
import io.mercury.coroutinesandbox.interactors.GetDownloadedStatuses.DownloadState.Downloaded
import io.mercury.coroutinesandbox.interactors.GetDownloadedStatuses.DownloadState.Downloading
import io.mercury.coroutinesandbox.interactors.GetDownloadedStatuses.DownloadState.NotDownloaded
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ActivityScoped
class DownloadIndicatorFactory @Inject constructor(
    private val getDownloadedStatus: GetDownloadedStatus
) {

    private fun getDownloadStatusUpdates(
        id: String,
        coroutineScope: CoroutineScope
    ): StateFlow<DownloadState> {
        val publisher = MutableStateFlow<DownloadState>(NotDownloaded)

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                getDownloadedStatus(id)
            }.collect {
                publisher.emit(it)
            }
        }
        return publisher
    }

    @Composable
    fun DownloadIndicator(
        id: String,
        modifier: Modifier = Modifier
    ) {
        val coroutineScope = rememberCoroutineScope()
        val state = remember {
            getDownloadStatusUpdates(id, coroutineScope)
        }.collectAsState()

        state.value.run {
            return when (this) {
                is Downloaded -> "Downloaded"
                is NotDownloaded -> "Not Downloaded"
                is Downloading -> "Downloading: $percent"
            }.let { msg ->
                Log.d("Composing Download", "$id - $msg")
                Text(
                    text = msg,
                    color = MaterialTheme.colors.primary,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = modifier
                )
            }
        }
    }
}

