package io.mercury.coroutinesandbox.repos

class FavoriteMovieIdsStoreInMemory : FavoriteMovieIdsStore {
    private val ids = HashSet<String>()

    override fun getFavoriteMovies(): Set<String> {
        return ids
    }

    override fun addFavoriteMovie(id: String) {
        ids.add(id)
    }

    override fun removeFavoriteMovie(id: String) {
        ids.remove(id)
    }
}