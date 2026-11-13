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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.smartgas_frontend.kitchenmonitoring.presentation.addsensor.AddSensorScreen
import com.smartgas_frontend.kitchenmonitoring.presentation.di.KitchenMonitoringPresentationModule
import com.smartgas_frontend.kitchenmonitoring.presentation.sensordetail.SensorDetailScreen
import com.smartgas_frontend.kitchenmonitoring.presentation.sensors.SensorsScreen
import com.smartgas_frontend.kitchenmonitoring.presentation.zonedetail.ZoneDetailScreen
import com.smartgas_frontend.kitchenmonitoring.presentation.zones.ZonesScreen
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
            tabs.filterNot { it.route == Routes.ZONES }.forEach { tab ->
                composable(tab.route) {
                    PlaceholderScreen(title = if (strings.isSpanish) tab.es else tab.en)
                }
            }
            composable(Routes.ZONES) {
                val viewModel = viewModel { KitchenMonitoringPresentationModule.getZonesViewModel() }

                ZonesScreen(
                    viewModel = viewModel,
                    onNavigate = { route -> navController.navigate(route) },
                    // navigate normal (no navigateSection) para que "atras" vuelva a Zones
                    onOpenZone = { zoneId -> navController.navigate(Routes.zoneDetail(zoneId)) }
                )
            }

            composable(
                route = Routes.ZONE_DETAIL,
                arguments = listOf(navArgument(Routes.ZONE_ID_ARG) { type = NavType.IntType })
            ) { entry ->
                val zoneId = entry.arguments?.getInt(Routes.ZONE_ID_ARG) ?: 0
                val viewModel = viewModel(key = "zone-$zoneId") {
                    KitchenMonitoringPresentationModule.getZoneDetailViewModel(zoneId)
                }

                ZoneDetailScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onOpenSensor = { sensorId -> navController.navigate(Routes.sensorDetail(sensorId)) },
                    onAddSensor = { id -> navController.navigate(Routes.addSensor(id)) }
                )
            }

            composable(
                route = Routes.SENSOR_DETAIL,
                arguments = listOf(navArgument(Routes.SENSOR_ID_ARG) { type = NavType.IntType })
            ) { entry ->
                val sensorId = entry.arguments?.getInt(Routes.SENSOR_ID_ARG) ?: 0
                val viewModel = viewModel(key = "sensor-$sensorId") {
                    KitchenMonitoringPresentationModule.getSensorDetailViewModel(sensorId)
                }

                SensorDetailScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onEdit = { id -> navController.navigate(Routes.editSensor(id)) }
                )
            }

            composable(
                route = Routes.EDIT_SENSOR,
                arguments = listOf(navArgument(Routes.SENSOR_ID_ARG) { type = NavType.IntType })
            ) { entry ->
                val sensorId = entry.arguments?.getInt(Routes.SENSOR_ID_ARG) ?: 0
                val viewModel = viewModel(key = "edit-sensor-$sensorId") {
                    KitchenMonitoringPresentationModule.getEditSensorViewModel(sensorId)
                }

                // Misma pantalla que Add sensor, en modo edicion
                AddSensorScreen(viewModel = viewModel, onClose = { navController.popBackStack() })
            }

            composable(
                route = Routes.ADD_SENSOR,
                arguments = listOf(
                    // -1 = sin zona preseleccionada (los argumentos Int de Navigation no pueden ser null)
                    navArgument(Routes.ZONE_ID_ARG) {
                        type = NavType.IntType
                        defaultValue = -1
                    },
                    navArgument(Routes.SCAN_ARG) {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) { entry ->
                val zoneId = entry.arguments?.getInt(Routes.ZONE_ID_ARG)?.takeIf { it >= 0 }
                val startWithScan = entry.arguments?.getBoolean(Routes.SCAN_ARG) == true
                val viewModel = viewModel(key = "add-sensor-$zoneId") {
                    KitchenMonitoringPresentationModule.getAddSensorViewModel(zoneId)
                }

                AddSensorScreen(
                    viewModel = viewModel,
                    onClose = { navController.popBackStack() },
                    startWithScan = startWithScan
                )
            }

            composable(Routes.SENSORS) {
                val viewModel = viewModel { KitchenMonitoringPresentationModule.getSensorsViewModel() }
                SensorsScreen(viewModel = viewModel)
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
