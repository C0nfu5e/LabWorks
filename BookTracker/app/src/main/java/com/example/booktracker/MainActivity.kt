package com.example.booktracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.example.booktracker.model.Book
import com.example.booktracker.ui.catalog.CatalogScreen
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
    val sampleBooks = listOf(
        Book(1L, "Чистый код", "Роберт Мартин", 4.8, "Программирование"),
        Book(2L, "Дюна", "Фрэнк Герберт", 4.9, "Фантастика"),
        Book(3L, "Kotlin в действии", "Дмитрий Жемеров", 4.7, "Программирование"),
        Book(4L, "1984", "Джордж Оруэлл", 4.85, "Антиутопия"),
        Book(5L, "Солярис", "Станислав Лем", 4.6, "Фантастика"),
        Book(6L, "О дивный новый мир", "Олдос Хаксли", 4.5, "Антиутопия")
    )

    CatalogScreen(books = sampleBooks)
}