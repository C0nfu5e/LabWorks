import java.util.Locale

// 1. Модель книги
data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val rating: Double,
    val genre: String,
    val review: String? = null
)

// 2. Герметичный класс результата поиска
sealed class SearchResult {
    data class Found(val books: List<Book>) : SearchResult()
    data class NotFound(val query: String) : SearchResult()
    data class InvalidQuery(val reason: String) : SearchResult()
}

// 3. Поиск по названию
fun searchByTitle(books: List<Book>, query: String): SearchResult {
    val trimmed = query.trim()
    if (trimmed.length < 2) {
        return SearchResult.InvalidQuery("Запрос \"$query\" слишком короткий (минимум 2 символа)")
    }

    val matched = books.filter { it.title.contains(trimmed, ignoreCase = true) }
    return if (matched.isNotEmpty()) {
        SearchResult.Found(matched)
    } else {
        SearchResult.NotFound(trimmed)
    }
}

// 4. Расширение: топ-N книг с наивысшим рейтингом
fun List<Book>.topRated(n: Int): List<Book> {
    if (n <= 0) return emptyList()
    return sortedByDescending { it.rating }.take(n)
}

// 5. Расширение: средний рейтинг
fun List<Book>.averageRating(): Double {
    if (isEmpty()) return 0.0
    return map { it.rating }.average()
}

// 6. Группировка по жанрам
fun groupByGenre(books: List<Book>): Map<String, List<Book>> {
    return books.groupBy { it.genre }
}

// Доп. задание: сортировка по нескольким критериям (сначала по жанру, затем по названию)
fun sortBooksByGenreAndTitle(books: List<Book>): List<Book> {
    return books.sortedWith(compareBy({ it.genre }, { it.title }))
}

// Доп. задание: формирование многострочного отчёта через """ и trimIndent()
fun exportReport(books: List<Book>): String {
    val sorted = sortBooksByGenreAndTitle(books)
    val avgFormatted = String.format(Locale.US, "%.2f", books.averageRating())

    val booksBlock = sorted.joinToString(separator = "\n") { book ->
        val reviewText = book.review ?: "без отзыва"
        "• [${book.genre}] \"${book.title}\" — ${book.author} (★ ${book.rating}) | Отзыв: $reviewText"
    }

    return """
        =========================================
        ОТЧЁТ ПО КАТАЛОГУ КНИГ
        Всего книг: ${books.size}
        Средний рейтинг: $avgFormatted
        -----------------------------------------
        Список книг (отсортировано по жанру и названию):
        $booksBlock
    """.trimIndent()
}

fun printSearchResult(query: String, result: SearchResult) {
    println("Поиск по запросу \"$query\":")
    when (result) {
        is SearchResult.Found -> {
            println("  Найдено книг: ${result.books.size}")
            result.books.forEach { println("   - ${it.title} (${it.author}, ★ ${it.rating})") }
        }
        is SearchResult.NotFound -> {
            println("  По запросу \"${result.query}\" ничего не найдено.")
        }
        is SearchResult.InvalidQuery -> {
            println("  Ошибка запроса: ${result.reason}")
        }
    }
}

fun main() {
    val catalog = listOf(
        Book(1, "Чистый код", "Роберт Мартин", 4.8, "Программирование", "Настольная книга разработчика"),
        Book(2, "Дюна", "Фрэнк Герберт", 4.9, "Фантастика", "Глубокий мир и политика"),
        Book(3, "Kotlin в действии", "Дмитрий Жемеров", 4.7, "Программирование", null),
        Book(4, "1984", "Джордж Оруэлл", 4.85, "Антиутопия", "Актуально всегда"),
        Book(5, "Солярис", "Станислав Лем", 4.6, "Фантастика", null),
        Book(6, "О дивный новый мир", "Олдос Хаксли", 4.5, "Антиутопия", "Классика жанра")
    )

    // 7. Обработка всех трёх веток SearchResult через when
    val queries = listOf("кот", "Гарри Поттер", "а")
    for (q in queries) {
        val result = searchByTitle(catalog, q)
        printSearchResult(q, result)
    }

    // Топ-3 книги
    println("\nТоп-3 книги по рейтингу:")
    catalog.topRated(3).forEachIndexed { index, book ->
        println("  ${index + 1}. ${book.title} — ★ ${book.rating}")
    }

    // Средний рейтинг
    val avg = String.format(Locale.US, "%.2f", catalog.averageRating())
    println("\nСредний рейтинг каталога: $avg")

    // Группировка по жанрам
    println("\nГруппировка по жанрам:")
    groupByGenre(catalog).forEach { (genre, booksInGenre) ->
        val titles = booksInGenre.joinToString { it.title }
        println("  $genre (${booksInGenre.size}): $titles")
    }

    // Доп. задание: отчёт и множественная сортировка
    println("\n${exportReport(catalog)}")
}