package io.mercury.coroutinesandbox.view.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import io.mercury.coroutinesandbox.interactors.GetMovieSummaries.MovieSummary

@Composable
fun RowHeader(text: String) {
    Text(text, style = MaterialTheme.typography.h5, color = MaterialTheme.colors.onBackground)
}

@Composable
fun MoviesRow(
    headerText: String,
    movies: List<MovieSummary>,
    movieCardFactory: MovieCardFactory,
    favoriteActionHandler: (String, Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        RowHeader(headerText)

        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            items(movies, { it.id }) { movie ->
                movieCardFactory.MovieCard(
                    movie, favoriteActionHandler
                )
            }
        }
    }
}





