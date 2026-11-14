package com.example.booktracker.model

data class Book(
    val id: Long,
    val title: String,
    val author: String,
    val rating: Double,
    val genre: String
)