package com.example.booktracker.ui.favorites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.booktracker.data.MockBookDataSource
import com.example.booktracker.model.Book
import com.example.booktracker.ui.catalog.BookGrid
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onBookClick: (Book) -> Unit,
    modifier: Modifier = Modifier
) {
    val favoriteIds by MockBookDataSource.favoriteIds.collectAsStateWithLifecycle()
    val favoriteBooks = MockBookDataSource.books.filter { it.id in favoriteIds }
    val gridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text("Избранное") })
        }
    ) { innerPadding ->
        if (favoriteBooks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "В избранном пока пусто",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            BookGrid(
                books = favoriteBooks,
                state = gridState,
                onBookClick = onBookClick,
                isBookFavorite = { id -> id in favoriteIds },
                onToggleFavorite = { id ->
                    scope.launch { MockBookDataSource.toggleFavorite(id) }
                },
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}