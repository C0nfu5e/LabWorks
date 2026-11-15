package com.example.booktracker

import com.example.booktracker.data.BookRepository
import com.example.booktracker.model.Book
import com.example.booktracker.ui.catalog.CatalogUiState
import com.example.booktracker.ui.catalog.CatalogViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private class FakeBookRepository(
    initialBooks: List<Book>
) : BookRepository {

    private val booksFlow = MutableStateFlow(initialBooks)
    private val favoriteIdsFlow = MutableStateFlow<Set<Long>>(emptySet())

    override fun observeBooks(): Flow<List<Book>> = booksFlow.asStateFlow()

    override fun observeFavoriteIds(): Flow<Set<Long>> = favoriteIdsFlow.asStateFlow()

    override suspend fun toggleFavorite(bookId: Long): Boolean {
        var added = false
        favoriteIdsFlow.update { current ->
            if (bookId in current) {
                added = false
                current - bookId
            } else {
                added = true
                current + bookId
            }
        }
        return added
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val testBooks = listOf(
        Book(1L, "Чистый код", "Роберт Мартин", 4.8, "Программирование"),
        Book(2L, "Дюна", "Фрэнк Герберт", 4.9, "Фантастика"),
        Book(3L, "Kotlin в действии", "Дмитрий Жемеров", 4.7, "Программирование")
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `searchQuery filters books after debounce`() = runTest {
        val repository = FakeBookRepository(testBooks)
        val viewModel = CatalogViewModel(repository = repository)

        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        // Пропускаем debounce начального пустого запроса (300 мс)
        advanceTimeBy(350L)

        val initialState = viewModel.uiState.value
        assertTrue(initialState is CatalogUiState.Success)
        assertEquals(3, (initialState as CatalogUiState.Success).books.size)

        // Вводим поисковый запрос
        viewModel.onSearchQueryChanged("Дюна")
        advanceTimeBy(350L)

        val filteredState = viewModel.uiState.value as CatalogUiState.Success
        assertEquals(1, filteredState.books.size)
        assertEquals("Дюна", filteredState.books.first().title)

        // Запрос, по которому ничего не найдено
        viewModel.onSearchQueryChanged("Несуществующая книга")
        advanceTimeBy(350L)

        val emptyState = viewModel.uiState.value as CatalogUiState.Success
        assertTrue(emptyState.books.isEmpty())

        collectJob.cancel()
    }
}