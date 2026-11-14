package com.example.booktracker.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.booktracker.ui.theme.BookTrackerTheme

@Composable
fun BookCard(
    title: String,
    author: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = author,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }   
}

@Preview(name = "Light Theme", showBackground = true)
@Composable
private fun BookCardLightPreview() {
    BookTrackerTheme(darkTheme = false) {
        BookCard(
            title = "Чистый код",
            author = "Роберт Мартин",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(
    name = "Dark Theme",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun BookCardDarkPreview() {
    BookTrackerTheme(darkTheme = true) {
        BookCard(
            title = "Чистый код",
            author = "Роберт Мартин",
            modifier = Modifier.padding(16.dp)
        )
    }
}