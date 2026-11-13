package com.smartgas_frontend.shared.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smartgas_frontend.shared.i18n.LocalStrings
import com.smartgas_frontend.shared.presentation.pages.NotFoundScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.APP) {
        composable(Routes.APP) {
            Home()
        }
    }
}

private data class Tab(val route: String, val icon: ImageVector, val en: String, val es: String)

private val tabs = listOf(
    Tab(Routes.HOME, Icons.Outlined.Home, "Home", "Inicio"),
    Tab(Routes.ZONES, Icons.Outlined.GridView, "Zones", "Zonas"),
    Tab(Routes.ALERTS, Icons.Outlined.Notifications, "Alerts", "Alertas"),
    Tab(Routes.HISTORY, Icons.Outlined.History, "History", "Historial"),
    Tab(Routes.SETTINGS, Icons.Outlined.Settings, "Settings", "Ajustes")
)

private fun NavHostController.navigateSection(route: String) {
    navigate(route) {
        popUpTo(Routes.HOME) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun Home() {
    val strings = LocalStrings.current
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEach { tab ->

                    val selected = currentRoute == tab.route || currentRoute?.startsWith(tab.route + "/") == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = { navController.navigateSection(tab.route) },
                        icon = { Icon(tab.icon, contentDescription = null) },
                        label = { Text(if (strings.isSpanish) tab.es else tab.en) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding)
        ) {
            tabs.forEach { tab ->
                composable(tab.route) {
                    PlaceholderScreen(title = if (strings.isSpanish) tab.es else tab.en)
                }
            }
            composable(Routes.NOT_FOUND) {
                NotFoundScreen(onGoHome = { navController.navigateSection(Routes.HOME) })
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(title, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
    }
}
