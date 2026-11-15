package com.example.booktracker.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.booktracker.data.MockBookDataSource
import com.example.booktracker.model.Book
import com.example.booktracker.navigation.BookDetailRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookDetailUiState(
    val book: Book? = null,
    val isLoading: Boolean = true,
    val isFavorite: Boolean = false,
    val isFavoriteUpdating: Boolean = false,
    val errorMessage: String? = null
)

class BookDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val route = savedStateHandle.toRoute<BookDetailRoute>()
    private val bookId: Long = route.bookId

    private val _uiState = MutableStateFlow(BookDetailUiState())
    val uiState: StateFlow<BookDetailUiState> = _uiState.asStateFlow()

    init {
        loadBook()
    }

    fun loadBook(simulateError: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val loaded = MockBookDataSource.getBookById(bookId, simulateError)
                _uiState.update {
                    it.copy(
                        book = loaded,
                        isLoading = false,
                        isFavorite = MockBookDataSource.isFavorite(bookId)
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Неизвестная ошибка загрузки"
                    )
                }
            }
        }
    }

    fun onToggleFavorite() {
        if (_uiState.value.isFavoriteUpdating) return
        viewModelScope.launch {
            _uiState.update { it.copy(isFavoriteUpdating = true) }
            try {
                val updated = MockBookDataSource.toggleFavorite(bookId)
                _uiState.update { it.copy(isFavorite = updated, isFavoriteUpdating = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isFavoriteUpdating = false,
                        errorMessage = e.message ?: "Ошибка обновления избранного"
                    )
                }
            }
        }
    }

    fun onErrorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}