package com.example.booktracker.ui.detail

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.booktracker.data.MockBookDataSource
import com.example.booktracker.model.Book
import com.example.booktracker.ui.theme.BookTrackerTheme
import kotlinx.coroutines.launch

@Composable
fun BookDetailRoute(
    bookId: Long,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var book by remember { mutableStateOf<Book?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isFavorite by remember { mutableStateOf(false) }
    var isFavoriteUpdating by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var reloadTrigger by remember { mutableIntStateOf(0) }
    var simulateErrorNextLoad by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // 5. Очистка callback API (системная кнопка "Назад") через DisposableEffect
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val currentOnBack by rememberUpdatedState(onBack)
    DisposableEffect(backDispatcher) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                currentOnBack()
            }
        }
        backDispatcher?.addCallback(callback)
        onDispose {
            callback.remove()
        }
    }

    // 2. Загрузка книги через LaunchedEffect(bookId)
    LaunchedEffect(bookId, reloadTrigger) {
        isLoading = true
        try {
            val loaded = MockBookDataSource.getBookById(
                bookId = bookId,
                simulateError = simulateErrorNextLoad
            )
            simulateErrorNextLoad = false
            book = loaded
            isFavorite = MockBookDataSource.isFavorite(bookId)
        } catch (e: Exception) {
            simulateErrorNextLoad = false
            errorMessage = e.message ?: "Неизвестная ошибка загрузки"
        } finally {
            isLoading = false
        }
    }

    // 3. Отображение ошибки через Snackbar и очистка после показа
    LaunchedEffect(errorMessage) {
        val message = errorMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            errorMessage = null
        }
    }

    BookDetailScreen(
        book = book,
        isLoading = isLoading,
        isFavorite = isFavorite,
        isFavoriteUpdating = isFavoriteUpdating,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        // 4. Кнопка "В избранное" запускает suspend-операцию через rememberCoroutineScope
        onToggleFavorite = {
            if (!isFavoriteUpdating) {
                coroutineScope.launch {
                    isFavoriteUpdating = true
                    try {
                        val updatedState = MockBookDataSource.toggleFavorite(bookId)
                        isFavorite = updatedState
                        snackbarHostState.showSnackbar(
                            if (updatedState) "Добавлено в избранное" else "Удалено из избранного"
                        )
                    } catch (e: Exception) {
                        errorMessage = e.message ?: "Ошибка обновления избранного"
                    } finally {
                        isFavoriteUpdating = false
                    }
                }
            }
        },
        onRetry = { reloadTrigger++ },
        onSimulateError = {
            simulateErrorNextLoad = true
            reloadTrigger++
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    book: Book?,
    isLoading: Boolean,
    isFavorite: Boolean,
    isFavoriteUpdating: Boolean,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onRetry: () -> Unit,
    onSimulateError: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = book?.title ?: "О книге") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("← Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                book == null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Не удалось загрузить книгу",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Button(onClick = onRetry) {
                            Text("Повторить")
                        }
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = book.title,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = book.author,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "★ ${book.rating}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            AssistChip(
                                onClick = {},
                                label = { Text(book.genre) }
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Описание",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = book.description,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onToggleFavorite,
                            enabled = !isFavoriteUpdating,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isFavorite) "♥ В избранном" else "♡ В избранное"
                            )
                        }

                        OutlinedButton(
                            onClick = onSimulateError,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Проверить ошибку (Snackbar)")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun BookDetailScreenPreview() {
    BookTrackerTheme {
        BookDetailScreen(
            book = MockBookDataSource.books.first(),
            isLoading = false,
            isFavorite = true,
            isFavoriteUpdating = false,
            snackbarHostState = remember { SnackbarHostState() },
            onBack = {},
            onToggleFavorite = {},
            onRetry = {},
            onSimulateError = {}
        )
    }
}