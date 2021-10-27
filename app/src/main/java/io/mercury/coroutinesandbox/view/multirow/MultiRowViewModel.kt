package io.mercury.coroutinesandbox.view.multirow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.mercury.coroutinesandbox.interactors.GetFavoritableMovies
import io.mercury.coroutinesandbox.interactors.GetFavoritedMovies
import javax.inject.Inject

@HiltViewModel
class MultiRowViewModel @Inject constructor(
    getAllMovies: GetFavoritableMovies,
    getFavoritedMovies: GetFavoritedMovies
) : ViewModel() {
    val feature = MultiRowFeature(viewModelScope, getAllMovies, getFavoritedMovies)
}