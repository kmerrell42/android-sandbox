package io.mercury.coroutinesandbox.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.mercury.coroutinesandbox.usecases.DownloadUpdate

class MainViewModel(downloadUpdate: DownloadUpdate) : ViewModel() {

    val feature = MainFeature(downloadUpdate, viewModelScope)

}