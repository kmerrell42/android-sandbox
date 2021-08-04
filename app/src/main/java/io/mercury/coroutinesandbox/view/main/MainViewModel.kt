package io.mercury.coroutinesandbox.view.main

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import io.mercury.coroutinesandbox.usecases.GetTimeSlowly

class MainViewModel(private val getTimeSlowly: GetTimeSlowly) : ViewModel() {
    val time: MutableLiveData<Long> by lazy {
        MutableLiveData<Long>().also {
            it.value = getTimeSlowly.invoke()
        }
    }

    // TODO: See if thee is a Dagger or Hilt friendly way to create these factories
    class Factory(
        savedStateRegistryOwner: SavedStateRegistryOwner,
        defaultArgs: Bundle?,
        private val getTimeSlowly: GetTimeSlowly
    ) : AbstractSavedStateViewModelFactory(savedStateRegistryOwner, defaultArgs) {
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return MainViewModel(getTimeSlowly) as T
        }

    }
}