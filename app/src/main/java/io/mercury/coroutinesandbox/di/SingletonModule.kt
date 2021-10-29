package io.mercury.coroutinesandbox.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.mercury.coroutinesandbox.api.MoviesService
import io.mercury.coroutinesandbox.repos.FavoriteMovieIdsStore
import io.mercury.coroutinesandbox.repos.FavoriteMovieIdsStoreInMemory
import io.mercury.coroutinesandbox.repos.MoviesStoreImpl
import io.mercury.domain.repos.MoviesStore
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit
import javax.inject.Singleton

// This is a property just to shut the compiler warnings up
private val json = Json {
    ignoreUnknownKeys = true
}

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {
    @ExperimentalSerializationApi
    @Provides
    @Singleton
    fun providesRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/MercuryIntermedia/Sample_Json_Movies/fb174395029c4d3768e013a88e28f909b660551f/")
            .run {
                json.asConverterFactory(MediaType.parse("application/json")!!)
                    .let { jsonConverterFactory ->
                    this.addConverterFactory(jsonConverterFactory)
                }
            }.build()
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
}