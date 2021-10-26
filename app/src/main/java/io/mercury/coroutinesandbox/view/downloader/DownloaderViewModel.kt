package io.mercury.coroutinesandbox.view.downloader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.mercury.coroutinesandbox.interactors.DownloadUpdate
import javax.inject.Inject

@HiltViewModel
class DownloaderViewModel @Inject constructor(downloadUpdate: DownloadUpdate) : ViewModel() {

    val feature = DownloaderFeature(downloadUpdate, viewModelScope)
}