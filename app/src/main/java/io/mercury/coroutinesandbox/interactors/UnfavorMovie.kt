package io.mercury.coroutinesandbox.interactors

import io.mercury.coroutinesandbox.repos.FavoriteMoviesManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnfavorMovie @Inject constructor(private val observableFavoritesStore: FavoriteMoviesManager) {
    suspend operator fun invoke(id: String) {
        observableFavoritesStore.remove(id)
    }
}