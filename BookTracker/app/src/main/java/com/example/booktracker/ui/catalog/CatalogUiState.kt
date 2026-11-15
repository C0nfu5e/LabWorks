package com.example.booktracker.ui.catalog

import com.example.booktracker.model.Book

sealed interface CatalogUiState {
    data object Loading : CatalogUiState

    data class Success(
        val books: List<Book>,
        val genres: List<String>,
        val filter: CatalogFilter = CatalogFilter(),
        val onlyFavorites: Boolean = false,
        val favoriteIds: Set<Long> = emptySet()
    ) : CatalogUiState

    data class Error(val message: String) : CatalogUiState
}