package io.mercury.coroutinesandbox.repos

class DownloadedMoviesStoreInMemory : DownloadedMovieStore {
    private val ids = HashSet<String>()
    override fun add(id: String) {
        ids.add(id)
    }

    override fun getIds(): Set<String> {
        return ids
    }
}