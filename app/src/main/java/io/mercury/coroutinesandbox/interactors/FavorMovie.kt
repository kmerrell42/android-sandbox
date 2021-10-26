package io.mercury.coroutinesandbox.interactors

import io.mercury.coroutinesandbox.repos.FavoriteMoviesManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavorMovie @Inject constructor(private val observableFavoritesStore: FavoriteMoviesManager) {
    suspend operator fun invoke(id: String) {
        observableFavoritesStore.add(id)
    }
}