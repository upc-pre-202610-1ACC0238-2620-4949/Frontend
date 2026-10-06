package com.smartgas_frontend.shared.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

/** .app-shell: menu lateral + barra superior + contenido de la ruta activa (router-view). */
@Composable
fun LayoutComponent(
    toolbarViewModel: ToolbarViewModel,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onViewAllNotifications: () -> Unit,
    onLogout: () -> Unit,
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ToastEffect(toolbarViewModel.toasts)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SidebarContent(
                currentRoute = currentRoute,
                onNavigate = { path ->
                    scope.launch { drawerState.close() }
                    onNavigate(path)
                },
                onLogout = onLogout
            )
        }
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                AppTopBar(
                    viewModel = toolbarViewModel,
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onNavigate = onNavigate,
                    onViewAllNotifications = onViewAllNotifications,
                    onLogout = onLogout
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .imePadding()
            ) {
                content()
            }
        }
    }
}
