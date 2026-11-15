package com.example.booktracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.booktracker.data.MockBookDataSource
import com.example.booktracker.ui.catalog.CatalogScreen
import com.example.booktracker.ui.detail.BookDetailRoute
import com.example.booktracker.ui.theme.BookTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BookTrackerTheme {
                BookTrackerContent()
            }
        }
    }
}

@Composable
fun BookTrackerContent() {
    var selectedBookId by rememberSaveable { mutableStateOf<Long?>(null) }

    val currentBookId = selectedBookId
    if (currentBookId == null) {
        CatalogScreen(
            books = MockBookDataSource.books,
            onBookClick = { book -> selectedBookId = book.id }
        )
    } else {
        BookDetailRoute(
            bookId = currentBookId,
            onBack = { selectedBookId = null }
        )
    }
}