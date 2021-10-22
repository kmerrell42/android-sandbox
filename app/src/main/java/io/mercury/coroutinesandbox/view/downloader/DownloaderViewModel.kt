package io.mercury.coroutinesandbox.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.mercury.coroutinesandbox.usecases.DownloadUpdate
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(downloadUpdate: DownloadUpdate) : ViewModel() {

    val feature = MainFeature(downloadUpdate, viewModelScope)

}