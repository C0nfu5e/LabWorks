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
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateSet
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.booktracker.model.Book
import com.example.booktracker.ui.theme.BookTrackerTheme
import kotlinx.coroutines.launch

private val FavoriteIdsSaver = listSaver<SnapshotStateSet<Long>, Long>(
    save = { it.toList() },
    restore = { restored -> mutableStateSetOf<Long>().apply { addAll(restored) } }
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    books: List<Book>,
    onBookClick: (Book) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var filter by rememberSaveable(stateSaver = CatalogFilterSaver) {
        mutableStateOf(CatalogFilter())
    }
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var onlyFavorites by rememberSaveable { mutableStateOf(false) }
    val favoriteIds = rememberSaveable(saver = FavoriteIdsSaver) { mutableStateSetOf<Long>() }

    val gridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    val genres = remember(books) {
        books.map { it.genre }.distinct().sorted()
    }

    val filteredBooks by remember(books) {
        derivedStateOf {
            val base = books.apply(filter)
            if (onlyFavorites) base.filter { it.id in favoriteIds } else base
        }
    }

    val resultsCount by remember {
        derivedStateOf { filteredBooks.size }
    }

    val showScrollToTop by remember {
        derivedStateOf { gridState.firstVisibleItemIndex > 10 }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("КнигоТрекер") },
                actions = {
                    Badge(
                        modifier = Modifier.padding(end = 16.dp),
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Text(
                            text = "Найдено $resultsCount",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ScrollToTopButton(
                visible = showScrollToTop,
                onClick = {
                    coroutineScope.launch { gridState.animateScrollToItem(0) }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SearchBar(
                query = filter.query,
                onQueryChange = { newQuery -> filter = filter.copy(query = newQuery) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            GenreFilterRow(
                genres = genres,
                selected = filter.genre,
                onSelect = { newGenre -> filter = filter.copy(genre = newGenre) },
                onlyFavorites = onlyFavorites,
                onToggleOnlyFavorites = { onlyFavorites = !onlyFavorites }
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
                    current = filter.sortBy,
                    onChange = { newSort -> filter = filter.copy(sortBy = newSort) },
                    expanded = sortMenuExpanded,
                    onExpandedChange = { sortMenuExpanded = it }
                )
            }

            if (filteredBooks.isEmpty()) {
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
                    books = filteredBooks,
                    state = gridState,
                    onBookClick = onBookClick,
                    onGenreClick = { genre ->
                        filter = filter.copy(genre = if (filter.genre == genre) null else genre)
                    },
                    isBookFavorite = { id -> id in favoriteIds },
                    onToggleFavorite = { id ->
                        if (id in favoriteIds) favoriteIds.remove(id) else favoriteIds.add(id)
                    },
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxSize()
                )
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

@Preview(name = "Filterable Catalog", showSystemUi = true)
@Composable
private fun CatalogScreenPreview() {
    val sample = (1L..16L).map { i ->
        Book(i, "Книга №$i", "Автор $i", 4.0 + (i % 10) * 0.1, if (i % 2 == 0L) "Фантастика" else "Программирование")
    }
    BookTrackerTheme {
        CatalogScreen(books = sample)
    }
}