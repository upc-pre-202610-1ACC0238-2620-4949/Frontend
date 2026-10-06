package com.smartgas_frontend.shared.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.smartgas_frontend.LocalAppContainer
import com.smartgas_frontend.iam.presentation.login.LoginScreen
import com.smartgas_frontend.iam.presentation.login.LoginViewModel
import com.smartgas_frontend.iam.presentation.register.RegisterScreen
import com.smartgas_frontend.iam.presentation.register.RegisterViewModel
import com.smartgas_frontend.iam.profile.presentation.profile.ProfileScreen
import com.smartgas_frontend.iam.profile.presentation.profile.ProfileViewModel
import com.smartgas_frontend.iam.settings.presentation.settings.SettingsScreen
import com.smartgas_frontend.iam.settings.presentation.settings.SettingsViewModel
import com.smartgas_frontend.incidentdetection.incidents.presentation.incidents.IncidentsScreen
import com.smartgas_frontend.incidentdetection.incidents.presentation.incidents.IncidentsViewModel
import com.smartgas_frontend.incidentdetection.incidents.presentation.incidents.TAB_NOTIFICATIONS
import com.smartgas_frontend.kitchenmonitoring.devices.presentation.devices.DevicesScreen
import com.smartgas_frontend.kitchenmonitoring.devices.presentation.devices.DevicesViewModel
import com.smartgas_frontend.kitchenmonitoring.monitoring.presentation.monitoring.MonitoringScreen
import com.smartgas_frontend.kitchenmonitoring.monitoring.presentation.monitoring.MonitoringViewModel
import com.smartgas_frontend.paymentmanagement.subscriptions.presentation.subscription.SubscriptionScreen
import com.smartgas_frontend.paymentmanagement.subscriptions.presentation.subscription.SubscriptionViewModel
import com.smartgas_frontend.postincidentprocedures.reports.presentation.reports.ReportsScreen
import com.smartgas_frontend.postincidentprocedures.reports.presentation.reports.ReportsViewModel
import com.smartgas_frontend.shared.dashboard.presentation.dashboard.DashboardScreen
import com.smartgas_frontend.shared.dashboard.presentation.dashboard.DashboardViewModel
import com.smartgas_frontend.shared.presentation.components.LayoutComponent
import com.smartgas_frontend.shared.presentation.components.ToolbarViewModel
import com.smartgas_frontend.shared.presentation.pages.NotFoundScreen

/**
 * Router principal: /login, /register y /app (que requiere sesion, como `meta.requiresAuth`).
 */
@Composable
fun AppNavigation() {
    val container = LocalAppContainer.current
    val navController = rememberNavController()

    // Mismo guard que router.beforeEach: con sesion entra directo a la app, sin sesion va al login.
    val startDestination = remember { if (container.sessionService.isLoggedIn()) Routes.APP else Routes.LOGIN }

    val goToApp: () -> Unit = {
        navController.navigate(Routes.APP) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    val logout: () -> Unit = {
        container.sessionService.clear()
        navController.navigate(Routes.LOGIN) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LOGIN) {
            val viewModel = viewModel { LoginViewModel(container.accountRepository) }

            LoginScreen(
                viewModel = viewModel,
                onLoggedIn = goToApp,
                onRegister = { navController.navigate(Routes.REGISTER) }
            )
        }

        composable(Routes.REGISTER) {
            val viewModel = viewModel { RegisterViewModel(container.accountRepository) }

            RegisterScreen(
                viewModel = viewModel,
                onGoToDashboard = goToApp,
                onLogin = { navController.navigate(Routes.LOGIN) { popUpTo(Routes.LOGIN) { inclusive = true } } }
            )
        }

        composable(Routes.APP) {
            AppShell(onLogout = logout)
        }
    }
}

private fun NavHostController.navigateSection(route: String) {
    navigate(route) {
        popUpTo(Routes.DASHBOARD)
        launchSingleTop = true
    }
}

/** Rutas hijas de /app dentro del layout compartido. */
@Composable
private fun AppShell(onLogout: () -> Unit) {
    val container = LocalAppContainer.current
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val toolbarViewModel = viewModel { ToolbarViewModel(container.incidentRepository, container.sessionService) }
    val navigate: (String) -> Unit = { route -> navController.navigateSection(route) }

    LayoutComponent(
        toolbarViewModel = toolbarViewModel,
        currentRoute = currentRoute,
        onNavigate = navigate,
        onViewAllNotifications = { navigate(Routes.incidents(TAB_NOTIFICATIONS)) },
        onLogout = onLogout
    ) {
        NavHost(navController = navController, startDestination = Routes.DASHBOARD) {
            composable(Routes.DASHBOARD) {
                val viewModel = viewModel { DashboardViewModel(container.dashboardRepository, container.sessionService) }

                DashboardScreen(viewModel = viewModel, onNavigate = navigate)
            }

            composable(Routes.MONITORING) {
                val viewModel = viewModel { MonitoringViewModel(container.monitoringRepository, container.sessionService) }

                MonitoringScreen(viewModel = viewModel, onNavigate = navigate)
            }

            composable(Routes.DEVICES) {
                val viewModel = viewModel { DevicesViewModel(container.sensorRepository, container.sessionService) }

                DevicesScreen(viewModel = viewModel)
            }

            composable(
                route = Routes.INCIDENTS_PATTERN,
                arguments = listOf(
                    navArgument(Routes.INCIDENTS_TAB_ARG) {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { entry ->
                val viewModel = viewModel { IncidentsViewModel(container.incidentRepository, container.sessionService) }

                IncidentsScreen(
                    viewModel = viewModel,
                    initialTab = entry.arguments?.getString(Routes.INCIDENTS_TAB_ARG)
                )
            }

            composable(Routes.REPORTS) {
                val viewModel = viewModel { ReportsViewModel(container.reportRepository, container.sessionService) }

                ReportsScreen(viewModel = viewModel)
            }

            composable(Routes.SUBSCRIPTION) {
                val viewModel = viewModel { SubscriptionViewModel(container.subscriptionRepository, container.sessionService) }

                SubscriptionScreen(viewModel = viewModel)
            }

            composable(Routes.PROFILE) {
                val viewModel = viewModel { ProfileViewModel(container.profileRepository, container.sessionService) }

                ProfileScreen(viewModel = viewModel, onNavigate = navigate, onLogout = onLogout)
            }

            composable(Routes.SETTINGS) {
                val viewModel = viewModel {
                    SettingsViewModel(container.settingsRepository, container.sessionService, container.preferences)
                }

                SettingsScreen(viewModel = viewModel, onNavigate = navigate)
            }

            composable(Routes.NOT_FOUND) {
                NotFoundScreen(onGoHome = { navigate(Routes.DASHBOARD) })
            }
        }
    }
}
