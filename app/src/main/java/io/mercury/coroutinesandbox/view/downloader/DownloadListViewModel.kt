package io.mercury.coroutinesandbox.view.downloader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.mercury.coroutinesandbox.interactors.DownloadMovie
import io.mercury.coroutinesandbox.interactors.GetDownloadedMovies
import io.mercury.coroutinesandbox.interactors.GetInProgressDownloads
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class DownloadListViewModel @Inject constructor(
    getDownloadedMovies: GetDownloadedMovies,
    getInProgressDownloads: GetInProgressDownloads,
    downloadMovie: DownloadMovie
) : ViewModel() {
    val feature = DownloadListFeature(viewModelScope, getInProgressDownloads, getDownloadedMovies)
    val downloaderFeature = CheeseyDownloaderFeature(viewModelScope, Dispatchers.IO, downloadMovie)
}