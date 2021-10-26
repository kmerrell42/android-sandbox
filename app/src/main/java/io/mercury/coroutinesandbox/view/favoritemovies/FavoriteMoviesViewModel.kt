package io.mercury.coroutinesandbox.view.favoritemovies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.mercury.coroutinesandbox.interactors.GetFavoritedMovies
import javax.inject.Inject

@HiltViewModel
class FavoriteMoviesViewModel @Inject constructor(getFavoritedMovies: GetFavoritedMovies) :
    ViewModel() {

    val feature = FavoriteMoviesFeature(viewModelScope, getFavoritedMovies)
}