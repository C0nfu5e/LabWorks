package com.example.booktracker.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booktracker.data.BookRepository
import com.example.booktracker.data.InMemoryBookRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private data class FilterOptions(
    val genre: String? = null,
    val sortBy: SortBy = SortBy.Title,
    val onlyFavorites: Boolean = false
)

@OptIn(FlowPreview::class)
class CatalogViewModel(
    private val repository: BookRepository = InMemoryBookRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    val query: StateFlow<String> = searchQuery.asStateFlow()

    private val filterOptions = MutableStateFlow(FilterOptions())

    private val debouncedQuery = searchQuery
        .debounce(300L)
        .distinctUntilChanged()

    val uiState: StateFlow<CatalogUiState> = combine(
        repository.observeBooks(),
        debouncedQuery,
        filterOptions,
        repository.observeFavoriteIds()
    ) { books, queryText, options, favoriteIds ->
        val currentFilter = CatalogFilter(
            query = queryText,
            genre = options.genre,
            sortBy = options.sortBy
        )

        val base = books.apply(currentFilter)
        val filtered = if (options.onlyFavorites) {
            base.filter { it.id in favoriteIds }
        } else {
            base
        }

        CatalogUiState.Success(
            books = filtered,
            genres = books.map { it.genre }.distinct().sorted(),
            filter = currentFilter,
            onlyFavorites = options.onlyFavorites,
            favoriteIds = favoriteIds
        ) as CatalogUiState
    }
        .catch { e ->
            emit(CatalogUiState.Error(e.message ?: "Ошибка загрузки каталога"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CatalogUiState.Loading
        )

    fun onSearchQueryChanged(newQuery: String) {
        searchQuery.value = newQuery
    }

    fun onGenreSelected(genre: String?) {
        filterOptions.update { it.copy(genre = genre) }
    }

    fun onSortChanged(sortBy: SortBy) {
        filterOptions.update { it.copy(sortBy = sortBy) }
    }

    fun onToggleOnlyFavorites() {
        filterOptions.update { it.copy(onlyFavorites = !it.onlyFavorites) }
    }

    fun onToggleFavorite(bookId: Long) {
        viewModelScope.launch {
            repository.toggleFavorite(bookId)
        }
    }

    fun retry() {
        // Реактивный поток обновляется автоматически, можно сбросить поиск при необходимости
        searchQuery.value = ""
    }
}