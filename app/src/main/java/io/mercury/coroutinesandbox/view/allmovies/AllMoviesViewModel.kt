package io.mercury.coroutinesandbox.view.allmovies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.mercury.coroutinesandbox.interactors.FavorMovie
import io.mercury.coroutinesandbox.interactors.GetAllMovies
import io.mercury.coroutinesandbox.interactors.UnfavorMovie
import javax.inject.Inject

@HiltViewModel
class AllMoviesViewModel @Inject constructor(
    getFavoritableMovies: GetAllMovies
) :
    ViewModel() {
    val feature = AllMoviesFeature(viewModelScope, getFavoritableMovies)
}