package io.mercury.coroutinesandbox.repos

interface FavoriteMovieIdsStore {
    fun getFavoriteMovies(): Set<String>
    fun addFavoriteMovie(id: String)
    fun removeFavoriteMovie(id: String)
}