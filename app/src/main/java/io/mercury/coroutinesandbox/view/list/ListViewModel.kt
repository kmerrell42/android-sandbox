package io.mercury.coroutinesandbox.view.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(getMovies: io.mercury.domain.interactors.GetMovies) :
    ViewModel() {
    val feature = ListFeature(viewModelScope, getMovies)
}