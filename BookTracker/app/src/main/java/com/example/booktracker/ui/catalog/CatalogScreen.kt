package com.example.booktracker.ui.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.booktracker.model.Book
import com.example.booktracker.ui.components.BookCard
import com.example.booktracker.ui.theme.BookTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    books: List<Book>,
    onBookClick: (Book) -> Unit = {},
    onGenreClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val layoutDirection = LocalLayoutDirection.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "КнигоТрекер") }
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = innerPadding.calculateStartPadding(layoutDirection) + 16.dp,
                top = innerPadding.calculateTopPadding() + 12.dp,
                end = innerPadding.calculateEndPadding(layoutDirection) + 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp
            ),
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
                    onGenreClick = onGenreClick
                )
            }
        }
    }
}

private val previewBooks = listOf(
    Book(1L, "Чистый код", "Роберт Мартин", 4.8, "Программирование"),
    Book(2L, "Дюна", "Фрэнк Герберт", 4.9, "Фантастика"),
    Book(3L, "Kotlin в действии", "Дмитрий Жемеров", 4.7, "Программирование"),
    Book(4L, "1984", "Джордж Оруэлл", 4.85, "Антиутопия"),
    Book(5L, "Солярис", "Станислав Лем", 4.6, "Фантастика"),
    Book(6L, "О дивный новый мир", "Олдос Хаксли", 4.5, "Антиутопия")
)

@Preview(name = "Catalog Screen", showSystemUi = true)
@Composable
private fun CatalogScreenPreview() {
    BookTrackerTheme {
        CatalogScreen(books = previewBooks)
    }
}