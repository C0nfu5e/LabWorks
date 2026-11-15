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
        Book(6L, "О дивный новый мир", "Олдос Хаксли", 4.5, "Антиутопия"),
        Book(7L, "Совершенный код", "Стив Макконнелл", 4.9, "Программирование"),
        Book(8L, "Автостопом по галактике", "Дуглас Адамс", 4.7, "Фантастика"),
        Book(9L, "451 градус по Фаренгейту", "Рэй Брэдбери", 4.75, "Антиутопия"),
        Book(10L, "Рефакторинг", "Мартин Фаулер", 4.8, "Программирование"),
        Book(11L, "Основание", "Айзек Азимов", 4.85, "Фантастика"),
        Book(12L, "Мы", "Евгений Замятин", 4.4, "Антиутопия"),
        Book(13L, "Паттерны проектирования", "Эрик Фримен", 4.65, "Программирование"),
        Book(14L, "Гиперион", "Дэн Симмонс", 4.8, "Фантастика"),
        Book(15L, "Скотный двор", "Джордж Оруэлл", 4.7, "Антиутопия"),
        Book(16L, "Грокаем алгоритмы", "Адитья Бхаргава", 4.75, "Программирование"),
        Book(17L, "Пикник на обочине", "Аркадий и Борис Стругацкие", 4.9, "Фантастика"),
        Book(18L, "Атлант расправил плечи", "Айн Рэнд", 4.3, "Роман"),
        Book(19L, "Мастер и Маргарита", "Михаил Булгаков", 4.95, "Роман"),
        Book(20L, "Преступление и наказание", "Фёдор Достоевский", 4.85, "Роман"),
        Book(21L, "Война и мир", "Лев Толстой", 4.7, "Роман"),
        Book(22L, "Три товарища", "Эрих Мария Ремарк", 4.9, "Роман"),
        Book(23L, "Задача трёх тел", "Лю Цысинь", 4.75, "Фантастика"),
        Book(24L, "Прагматичный программист", "Эндрю Хант", 4.85, "Программирование")
    )

    CatalogScreen(books = sampleBooks)
}