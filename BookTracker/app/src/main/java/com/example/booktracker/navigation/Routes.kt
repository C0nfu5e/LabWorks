package com.example.booktracker.navigation

import kotlinx.serialization.Serializable

@Serializable
data object CatalogRoute

@Serializable
data object FavoritesRoute

@Serializable
data class BookDetailRoute(val bookId: Long)