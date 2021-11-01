package io.mercury.coroutinesandbox.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.mercury.coroutinesandbox.api.MoviesService
import io.mercury.coroutinesandbox.repos.DownloadedMovieStore
import io.mercury.coroutinesandbox.repos.DownloadedMoviesStoreInMemory
import io.mercury.coroutinesandbox.repos.FavoriteMovieIdsStore
import io.mercury.coroutinesandbox.repos.FavoriteMovieIdsStoreInMemory
import io.mercury.coroutinesandbox.repos.MoviesStoreImpl
import io.mercury.domain.repos.MoviesStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {

    @Qualifier
    annotation class ApplicationCoroutineScope

    @Provides
    @Singleton
    @ApplicationCoroutineScope
    fun providesApplicationScope(): CoroutineScope {
        return MainScope()
    }

    @Qualifier
    annotation class BackgroundDispatcher

    @Provides
    @Singleton
    @BackgroundDispatcher
    fun providesBackgroundDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }

    @Provides
    @Singleton
    fun providesRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/MercuryIntermedia/Sample_Json_Movies/fb174395029c4d3768e013a88e28f909b660551f/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providesMoviesStore(retrofit: Retrofit): MoviesStore {
        return MoviesStoreImpl(retrofit.create(MoviesService::class.java))
    }

    @Provides
    @Singleton
    fun providesFavoritesMovieStore(): FavoriteMovieIdsStore {
        return FavoriteMovieIdsStoreInMemory()
    }

    @Provides
    @Singleton
    fun providesDownloadedMoviesStore() : DownloadedMovieStore {
        return DownloadedMoviesStoreInMemory()
    }
}