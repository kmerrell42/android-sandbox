package io.mercury.coroutinesandbox.view.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.mercury.coroutinesandbox.interactors.FavorMovie
import io.mercury.coroutinesandbox.interactors.GetFavoritableMovies
import io.mercury.coroutinesandbox.interactors.UnfavorMovie
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    getFavoritableMovies: GetFavoritableMovies,
    favorMovie: FavorMovie,
    unfavorMovie: UnfavorMovie
) :
    ViewModel() {
    val feature = ListFeature(viewModelScope, getFavoritableMovies, favorMovie, unfavorMovie)
}