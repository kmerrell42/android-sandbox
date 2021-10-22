package io.mercury.coroutinesandbox.view.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.mercury.coroutinesandbox.usecases.GetMovies
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(getMovies: GetMovies) : ViewModel() {
    val feature = ListFeature(viewModelScope, getMovies)
}