package io.mercury.coroutinesandbox.interactors

import android.util.Log
import io.mercury.coroutinesandbox.interactors.GetDownloadedStatuses.DownloadState
import io.mercury.coroutinesandbox.interactors.GetDownloadedStatuses.DownloadState.NotDownloaded
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetDownloadedStatus @Inject constructor(private val getDownloadedStatuses: GetDownloadedStatuses) {
    operator fun invoke(id: String): Flow<DownloadState> {
        return getDownloadedStatuses().map { statusMap ->
            Log.d("DownloadStatus", "Status for $id: ${statusMap[id]}")
            statusMap.getOrElse(id) { NotDownloaded }
        }
    }
}