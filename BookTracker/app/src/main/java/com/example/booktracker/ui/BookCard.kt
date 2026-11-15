package com.example.booktracker.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.booktracker.model.Book
import com.example.booktracker.ui.theme.BookTrackerTheme

@Composable
fun BookCard(
    book: Book,
    onClick: () -> Unit,
    onGenreClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onFavoriteClick: (() -> Unit)? = null
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = book.author,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "★ ${book.rating}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                if (onFavoriteClick != null) {
                    TextButton(onClick = onFavoriteClick) {
                        Text(text = if (isFavorite) "♥" else "♡")
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            AssistChip(
                onClick = { onGenreClick(book.genre) },
                label = {
                    Text(
                        text = book.genre,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }
}

private val previewBook = Book(1L, "Чистый код", "Роберт Мартин", 4.8, "Программирование")

@Preview(name = "BookCard Light", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun BookCardLightPreview() {
    BookTrackerTheme(darkTheme = false) {
        BookCard(
            book = previewBook,
            onClick = {},
            onGenreClick = {},
            isFavorite = true,
            onFavoriteClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "BookCard Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BookCardDarkPreview() {
    BookTrackerTheme(darkTheme = true) {
        BookCard(
            book = previewBook,
            onClick = {},
            onGenreClick = {},
            isFavorite = false,
            onFavoriteClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}