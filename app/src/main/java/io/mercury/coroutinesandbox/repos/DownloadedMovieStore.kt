package io.mercury.coroutinesandbox.repos

interface DownloadedMovieStore {
    fun add(id: String)
    fun getIds(): Set<String>
}