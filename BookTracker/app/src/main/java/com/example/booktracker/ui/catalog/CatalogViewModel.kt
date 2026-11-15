package com.example.booktracker.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booktracker.data.MockBookDataSource
import com.example.booktracker.model.Book
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CatalogViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<CatalogUiState>(CatalogUiState.Loading)
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    private var allBooks: List<Book> = emptyList()
    private var genres: List<String> = emptyList()
    private var filter: CatalogFilter = CatalogFilter()
    private var onlyFavorites: Boolean = false
    private val favoriteIds = mutableSetOf<Long>()

    init {
        loadBooks()
        viewModelScope.launch {
            MockBookDataSource.favoriteIds.collect { ids ->
                favoriteIds.clear()
                favoriteIds.addAll(ids)
                if (allBooks.isNotEmpty()) emitSuccess()
            }
        }
    }

    fun loadBooks() {
        viewModelScope.launch {
            _uiState.value = CatalogUiState.Loading
            try {
                delay(400) // имитация загрузки
                allBooks = MockBookDataSource.books
                genres = allBooks.map { it.genre }.distinct().sorted()
                emitSuccess()
            } catch (e: Exception) {
                _uiState.value = CatalogUiState.Error(
                    e.message ?: "Не удалось загрузить каталог"
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        filter = filter.copy(query = query)
        emitSuccess()
    }

    fun onGenreSelected(genre: String?) {
        filter = filter.copy(genre = genre)
        emitSuccess()
    }

    fun onSortChanged(sortBy: SortBy) {
        filter = filter.copy(sortBy = sortBy)
        emitSuccess()
    }

    fun onToggleOnlyFavorites() {
        onlyFavorites = !onlyFavorites
        emitSuccess()
    }
    fun onToggleFavorite(bookId: Long) {
        viewModelScope.launch {
            MockBookDataSource.toggleFavorite(bookId)
        }
    }
    private fun emitSuccess() {
        val base = allBooks.apply(filter)
        val result = if (onlyFavorites) {
            base.filter { it.id in favoriteIds }
        } else {
            base
        }

        _uiState.value = CatalogUiState.Success(
            books = result,
            genres = genres,
            filter = filter,
            onlyFavorites = onlyFavorites,
            favoriteIds = favoriteIds.toSet()
        )
    }
}