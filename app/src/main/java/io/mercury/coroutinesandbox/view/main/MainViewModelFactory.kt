package io.mercury.coroutinesandbox.view.main

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import io.mercury.coroutinesandbox.usecases.DownloadUpdate

// TODO: See if thee is a Dagger or Hilt friendly way to create these factories
class MainViewModelFactory(
    savedStateRegistryOwner: SavedStateRegistryOwner,
    defaultArgs: Bundle?,
    private val downloadUpdate: DownloadUpdate
) : AbstractSavedStateViewModelFactory(savedStateRegistryOwner, defaultArgs) {
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return MainViewModel(downloadUpdate) as T
    }

}