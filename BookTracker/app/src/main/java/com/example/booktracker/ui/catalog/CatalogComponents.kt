package com.example.booktracker.ui.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.booktracker.model.Book
import com.example.booktracker.ui.components.BookCard

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        placeholder = { Text("Поиск по названию или автору...") },
        trailingIcon = {
            if (query.isNotEmpty()) {
                TextButton(onClick = { onQueryChange("") }) {
                    Text("✕")
                }
            }
        }
    )
}

@Composable
fun GenreFilterRow(
    genres: List<String>,
    selected: String?,
    onSelect: (String?) -> Unit,
    modifier: Modifier = Modifier,
    onlyFavorites: Boolean = false,
    onToggleOnlyFavorites: (() -> Unit)? = null
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        if (onToggleOnlyFavorites != null) {
            item(key = "only_favorites") {
                FilterChip(
                    selected = onlyFavorites,
                    onClick = onToggleOnlyFavorites,
                    label = { Text("Только избранное") }
                )
            }
        }
        items(items = genres, key = { it }) { genre ->
            val isSelected = genre == selected
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(if (isSelected) null else genre) },
                label = { Text(genre) }
            )
        }
    }
}

@Composable
fun SortMenu(
    current: SortBy,
    onChange: (SortBy) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        OutlinedButton(onClick = { onExpandedChange(true) }) {
            Text(text = "Сортировка: ${current.label}")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            SortBy.entries.forEach { option ->
                DropdownMenuItem(
                    text = {
                        val prefix = if (option == current) "✓ " else ""
                        Text("$prefix${option.label}")
                    },
                    onClick = {
                        onChange(option)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}

@Composable
fun BookGrid(
    books: List<Book>,
    state: LazyGridState,
    onBookClick: (Book) -> Unit,
    modifier: Modifier = Modifier,
    onGenreClick: (String) -> Unit = {},
    isBookFavorite: (Long) -> Boolean = { false },
    onToggleFavorite: ((Long) -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(16.dp)
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        state = state,
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = books,
            key = { it.id }
        ) { book ->
            BookCard(
                book = book,
                onClick = { onBookClick(book) },
                onGenreClick = onGenreClick,
                isFavorite = isBookFavorite(book.id),
                onFavoriteClick = onToggleFavorite?.let { toggle -> { toggle(book.id) } }
            )
        }
    }
}