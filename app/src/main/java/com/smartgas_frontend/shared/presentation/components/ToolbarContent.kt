package com.smartgas_frontend.shared.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.smartgas_frontend.R
import com.smartgas_frontend.shared.i18n.LocalStrings
import com.smartgas_frontend.shared.presentation.navigation.Routes
import com.smartgas_frontend.shared.utils.formatDate
import com.smartgas_frontend.shared.utils.formatIncidentNotification

private data class NavItem(val label: String, val icon: ImageVector, val path: String)

fun isActiveRoute(currentRoute: String?, path: String): Boolean =
    currentRoute == path || currentRoute?.startsWith("$path/") == true || currentRoute?.startsWith("$path?") == true

/** Menu lateral (.sidebar de la web) mostrado dentro del drawer. */
@Composable
fun SidebarContent(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    val strings = LocalStrings.current

    val navItems = listOf(
        NavItem(strings.t(R.string.dashboard), Icons.Outlined.Dashboard, Routes.DASHBOARD),
        NavItem(strings.t(R.string.monitoring), Icons.Outlined.Visibility, Routes.MONITORING),
        NavItem(strings.t(R.string.devices), Icons.Outlined.Wifi, Routes.DEVICES),
        NavItem(strings.t(R.string.incidents), Icons.Outlined.WarningAmber, Routes.INCIDENTS),
        NavItem(strings.t(R.string.reports), Icons.Outlined.BarChart, Routes.REPORTS),
        NavItem(strings.t(R.string.subscription), Icons.Outlined.CreditCard, Routes.SUBSCRIPTION)
    )

    val accountItems = listOf(
        NavItem(strings.t(R.string.profile), Icons.Outlined.Badge, Routes.PROFILE),
        NavItem(strings.t(R.string.settings), Icons.Outlined.Settings, Routes.SETTINGS)
    )

    ModalDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.surface) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(role = Role.Button, onClickLabel = "SmartGas dashboard") { onNavigate(Routes.DASHBOARD) }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = "SmartGas logo",
                    modifier = Modifier.size(36.dp)
                )
                Text(
                    "SmartGas",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            SidebarSectionLabel(strings.t(R.string.mainPanel))
            navItems.forEach { item -> SidebarLink(item, isActiveRoute(currentRoute, item.path), onNavigate) }

            SidebarSectionLabel(strings.t(R.string.accountOverview))
            accountItems.forEach { item -> SidebarLink(item, isActiveRoute(currentRoute, item.path), onNavigate) }

            Spacer(Modifier.weight(1f))
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            NavigationDrawerItem(
                label = { Text(strings.t(R.string.logout)) },
                icon = { Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null) },
                selected = false,
                onClick = onLogout,
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedTextColor = MaterialTheme.colorScheme.error,
                    unselectedIconColor = MaterialTheme.colorScheme.error
                )
            )
        }
    }
}

@Composable
private fun SidebarSectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 12.dp, top = 16.dp, bottom = 6.dp)
    )
}

@Composable
private fun SidebarLink(item: NavItem, active: Boolean, onNavigate: (String) -> Unit) {
    NavigationDrawerItem(
        label = { Text(item.label) },
        icon = { Icon(item.icon, contentDescription = null) },
        selected = active,
        onClick = { onNavigate(item.path) },
        modifier = Modifier.padding(vertical = 2.dp)
    )
}

/** Barra superior (.app-topbar): idioma, centro de notificaciones y menu de usuario. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    viewModel: ToolbarViewModel,
    onOpenDrawer: () -> Unit,
    onNavigate: (String) -> Unit,
    onViewAllNotifications: () -> Unit,
    onLogout: () -> Unit
) {
    val strings = LocalStrings.current
    val notificationOpen = viewModel.notificationOpen.value
    val unreadNotifications = viewModel.unreadNotifications
    val latestNotifications = viewModel.latestNotifications

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(Icons.Outlined.Menu, contentDescription = strings.t(R.string.mainPanel))
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(
                    Icons.Outlined.Shield,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    strings.t(R.string.topbarContext),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        actions = {
            LanguageSwitcher()

            Box {
                IconButton(onClick = viewModel::toggleNotifications) {
                    BadgedBox(badge = { if (unreadNotifications.isNotEmpty()) Badge() }) {
                        Icon(Icons.Outlined.Notifications, contentDescription = strings.t(R.string.notificationCenter))
                    }
                }

                DropdownMenu(expanded = notificationOpen, onDismissRequest = viewModel::closeNotifications) {
                    Column(
                        modifier = Modifier
                            .width(320.dp)
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column {
                            Text(
                                strings.t(R.string.latestNotifications),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "${unreadNotifications.size} ${strings.t(R.string.pendingAlerts).lowercase()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        HorizontalDivider()

                        if (latestNotifications.isNotEmpty()) {
                            latestNotifications.forEach { notification ->
                                val unread = !notification.read || !notification.confirmed

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (unread) MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                                            else MaterialTheme.colorScheme.surface
                                        )
                                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        formatIncidentNotification(strings, notification),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        formatDate(notification.createdAt),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Row {
                                        if (!notification.read) {
                                            TextButton(onClick = { viewModel.markRead(notification) }) {
                                                Text(strings.t(R.string.markRead), style = MaterialTheme.typography.bodySmall)
                                            }
                                        }
                                        if (!notification.confirmed) {
                                            TextButton(onClick = { viewModel.confirmReception(notification) }) {
                                                Text(strings.t(R.string.confirmReception), style = MaterialTheme.typography.bodySmall)
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Text(
                                strings.t(R.string.noUnreadNotifications),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        TextButton(
                            onClick = {
                                viewModel.closeNotifications()
                                onViewAllNotifications()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(strings.t(R.string.viewAllNotifications))
                        }
                    }
                }
            }

            UserMenu(onNavigate = onNavigate, onLogout = onLogout)
        }
    )
}
