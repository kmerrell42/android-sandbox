package io.mercury.coroutinesandbox.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.mercury.coroutinesandbox.usecases.GetTimeSlowly

class MainViewModel(getTimeSlowly: GetTimeSlowly) : ViewModel() {

    val feature = MainFeature(getTimeSlowly, viewModelScope)

}