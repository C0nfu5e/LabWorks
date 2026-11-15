package com.example.booktracker.ui.catalog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.booktracker.data.MockBookDataSource
import com.example.booktracker.model.Book
import com.example.booktracker.ui.theme.BookTrackerTheme
import kotlinx.coroutines.launch

@Composable
fun CatalogRoute(
    onBookClick: (Book) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CatalogViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentQuery by viewModel.query.collectAsStateWithLifecycle()

    CatalogScreen(
        uiState = uiState,
        searchQuery = currentQuery,
        onBookClick = onBookClick,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onGenreSelected = viewModel::onGenreSelected,
        onSortChanged = viewModel::onSortChanged,
        onToggleOnlyFavorites = viewModel::onToggleOnlyFavorites,
        onToggleFavorite = viewModel::onToggleFavorite,
        onRetry = viewModel::retry,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    uiState: CatalogUiState,
    searchQuery: String,
    onBookClick: (Book) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onGenreSelected: (String?) -> Unit,
    onSortChanged: (SortBy) -> Unit,
    onToggleOnlyFavorites: () -> Unit,
    onToggleFavorite: (Long) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    var sortMenuExpanded by remember { mutableStateOf(false) }
    val gridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    val showScrollToTop by remember {
        derivedStateOf { gridState.firstVisibleItemIndex > 10 }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("КнигоТрекер") },
                actions = {
                    if (uiState is CatalogUiState.Success) {
                        Badge(
                            modifier = Modifier.padding(end = 16.dp),
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ) {
                            Text(
                                text = "Найдено ${uiState.books.size}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState is CatalogUiState.Success) {
                ScrollToTopButton(
                    visible = showScrollToTop,
                    onClick = {
                        coroutineScope.launch { gridState.animateScrollToItem(0) }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState) {
                is CatalogUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is CatalogUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = uiState.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = onRetry) {
                            Text("Повторить")
                        }
                    }
                }

                is CatalogUiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        SearchBar(
                            query = searchQuery, // <-- вместо uiState.filter.query
                            onQueryChange = onSearchQueryChanged,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        GenreFilterRow(
                            genres = uiState.genres,
                            selected = uiState.filter.genre,
                            onSelect = onGenreSelected,
                            onlyFavorites = uiState.onlyFavorites,
                            onToggleOnlyFavorites = onToggleOnlyFavorites
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SortMenu(
                                current = uiState.filter.sortBy,
                                onChange = onSortChanged,
                                expanded = sortMenuExpanded,
                                onExpandedChange = { sortMenuExpanded = it }
                            )
                        }

                        if (uiState.books.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Книги не найдены",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            BookGrid(
                                books = uiState.books,
                                state = gridState,
                                onBookClick = onBookClick,
                                onGenreClick = { genre ->
                                    val next = if (uiState.filter.genre == genre) null else genre
                                    onGenreSelected(next)
                                },
                                isBookFavorite = { id -> id in uiState.favoriteIds },
                                onToggleFavorite = onToggleFavorite,
                                contentPadding = PaddingValues(16.dp),
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScrollToTopButton(
    visible: Boolean,
    onClick: () -> Unit
) {
    AnimatedVisibility(visible = visible) {
        FloatingActionButton(onClick = onClick) {
            Text("↑ Наверх", modifier = Modifier.padding(horizontal = 12.dp))
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun CatalogScreenPreview() {
    BookTrackerTheme {
        CatalogScreen(
            uiState = CatalogUiState.Success(
                books = MockBookDataSource.books.take(6),
                genres = listOf("Программирование", "Фантастика", "Антиутопия")
            ),
            onBookClick = {},
            onSearchQueryChanged = {},
            onGenreSelected = {},
            onSortChanged = {},
            onToggleOnlyFavorites = {},
            onToggleFavorite = {},
            searchQuery = "",
            onRetry = {}
        )
    }
}