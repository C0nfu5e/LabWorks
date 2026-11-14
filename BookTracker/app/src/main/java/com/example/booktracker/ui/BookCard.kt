package com.example.booktracker.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
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
            Text(
                text = "★ ${book.rating}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
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

private val previewBook = Book(
    id = 1L,
    title = "Чистый код",
    author = "Роберт Мартин",
    rating = 4.8,
    genre = "Программирование"
)

@Preview(name = "BookCard Light", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun BookCardLightPreview() {
    BookTrackerTheme(darkTheme = false) {
        BookCard(
            book = previewBook,
            onClick = {},
            onGenreClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(
    name = "BookCard Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun BookCardDarkPreview() {
    BookTrackerTheme(darkTheme = true) {
        BookCard(
            book = previewBook,
            onClick = {},
            onGenreClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}