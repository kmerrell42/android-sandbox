package io.mercury.coroutinesandbox.view.downloader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.mercury.coroutinesandbox.interactors.GetDownloadedMovies
import io.mercury.coroutinesandbox.interactors.GetInProgressDownloads
import javax.inject.Inject

@HiltViewModel
class DownloadListViewModel @Inject constructor(
    getDownloadedMovies: GetDownloadedMovies,
    getInProgressDownloads: GetInProgressDownloads
) : ViewModel() {
    val feature = DownloadListFeature(viewModelScope, getInProgressDownloads, getDownloadedMovies)
}