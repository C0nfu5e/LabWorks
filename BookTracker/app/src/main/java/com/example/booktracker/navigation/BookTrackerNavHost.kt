package com.example.booktracker.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.booktracker.ui.favorites.FavoritesScreen
import com.example.booktracker.ui.catalog.CatalogRoute as CatalogScreenRoute
import com.example.booktracker.ui.detail.BookDetailRoute as BookDetailScreenRoute

@Composable
fun BookTrackerNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val showBottomBar = currentDestination?.hasRoute<CatalogRoute>() == true ||
            currentDestination?.hasRoute<FavoritesRoute>() == true

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentDestination?.hasRoute<CatalogRoute>() == true,
                        onClick = {
                            navController.navigate(CatalogRoute) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Text("📚") },
                        label = { Text("Каталог") }
                    )
                    NavigationBarItem(
                        selected = currentDestination?.hasRoute<FavoritesRoute>() == true,
                        onClick = {
                            navController.navigate(FavoritesRoute) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Text("♥") },
                        label = { Text("Избранное") }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = CatalogRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<CatalogRoute> {
                CatalogScreenRoute(
                    onBookClick = { book ->
                        navController.navigate(BookDetailRoute(bookId = book.id))
                    }
                )
            }

            composable<FavoritesRoute> {
                FavoritesScreen(
                    onBookClick = { book ->
                        navController.navigate(BookDetailRoute(bookId = book.id))
                    }
                )
            }

            composable<BookDetailRoute> {
                BookDetailScreenRoute(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}