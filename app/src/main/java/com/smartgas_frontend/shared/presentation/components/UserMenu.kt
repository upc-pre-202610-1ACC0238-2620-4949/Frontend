package com.smartgas_frontend.shared.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smartgas_frontend.LocalAppContainer
import com.smartgas_frontend.R
import com.smartgas_frontend.shared.i18n.LocalStrings
import com.smartgas_frontend.shared.presentation.navigation.Routes

@Composable
fun UserMenu(onNavigate: (String) -> Unit, onLogout: () -> Unit) {
    val strings = LocalStrings.current
    val user = LocalAppContainer.current.sessionService.getCurrentUser()
    var open by remember { mutableStateOf(false) }

    val goTo: (String) -> Unit = { path ->
        open = false
        onNavigate(path)
    }

    Box {
        IconButton(onClick = { open = !open }) {
            Icon(Icons.Outlined.Person, contentDescription = "User menu")
        }

        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            Text(
                text = user?.email?.ifEmpty { user.name } ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text(strings.t(R.string.profile)) },
                leadingIcon = { Icon(Icons.Outlined.Badge, contentDescription = null) },
                onClick = { goTo(Routes.PROFILE) }
            )
            DropdownMenuItem(
                text = { Text(strings.t(R.string.settings)) },
                leadingIcon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                onClick = { goTo(Routes.SETTINGS) }
            )
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text(strings.t(R.string.logout), color = MaterialTheme.colorScheme.error) },
                leadingIcon = {
                    Icon(
                        Icons.AutoMirrored.Outlined.Logout,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                onClick = {
                    open = false
                    onLogout()
                }
            )
        }
    }
}
