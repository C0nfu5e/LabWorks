package com.example.booktracker.ui.catalog

import androidx.compose.runtime.saveable.listSaver
import com.example.booktracker.model.Book

enum class SortBy(val label: String) {
    Title("По названию"),
    Rating("По рейтингу"),
    Recent("Сначала новые")
}

data class CatalogFilter(
    val query: String = "",
    val genre: String? = null,
    val sortBy: SortBy = SortBy.Title
)

val CatalogFilterSaver = listSaver<CatalogFilter, Any?>(
    save = { listOf(it.query, it.genre, it.sortBy.name) },
    restore = {
        CatalogFilter(
            query = it[0] as String,
            genre = it[1] as String?,
            sortBy = SortBy.valueOf(it[2] as String)
        )
    }
)
// ... дальше функция apply(filter: CatalogFilter) без изменений

fun List<Book>.apply(filter: CatalogFilter): List<Book> {
    val trimmedQuery = filter.query.trim()

    val filtered = filter { book ->
        val matchesQuery = trimmedQuery.isEmpty() ||
                book.title.contains(trimmedQuery, ignoreCase = true) ||
                book.author.contains(trimmedQuery, ignoreCase = true)

        val matchesGenre = filter.genre == null || book.genre == filter.genre

        matchesQuery && matchesGenre
    }

    return when (filter.sortBy) {
        SortBy.Title -> filtered.sortedBy { it.title.lowercase() }
        SortBy.Rating -> filtered.sortedByDescending { it.rating }
        SortBy.Recent -> filtered.sortedByDescending { it.id }
    }
}