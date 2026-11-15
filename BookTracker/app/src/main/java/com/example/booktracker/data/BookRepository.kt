package com.example.booktracker.data

import com.example.booktracker.model.Book
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface BookRepository {
    fun observeBooks(): Flow<List<Book>>
    fun observeFavoriteIds(): Flow<Set<Long>>
    suspend fun toggleFavorite(bookId: Long): Boolean
}

object InMemoryBookRepository : BookRepository {
    private val booksFlow = MutableStateFlow(MockBookDataSource.books)

    override fun observeBooks(): Flow<List<Book>> = booksFlow.asStateFlow()

    override fun observeFavoriteIds(): Flow<Set<Long>> = MockBookDataSource.favoriteIds

    override suspend fun toggleFavorite(bookId: Long): Boolean {
        return MockBookDataSource.toggleFavorite(bookId)
    }
}