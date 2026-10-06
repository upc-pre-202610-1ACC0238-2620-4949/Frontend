package com.smartgas_frontend.incidentdetection.incidents.presentation.incidents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.smartgas_frontend.incidentdetection.incidents.domain.model.Incident
import com.smartgas_frontend.incidentpreventionnotification.domain.model.Notification
import com.smartgas_frontend.shared.i18n.LocalStrings
import com.smartgas_frontend.shared.i18n.Strings
import com.smartgas_frontend.shared.presentation.components.ContentPage
import com.smartgas_frontend.shared.presentation.components.DetailRow
import com.smartgas_frontend.shared.presentation.components.EmptyMessage
import com.smartgas_frontend.shared.presentation.components.LoadingState
import com.smartgas_frontend.shared.presentation.components.PageHeader
import com.smartgas_frontend.shared.presentation.components.PanelCard
import com.smartgas_frontend.shared.presentation.components.PrimaryButton
import com.smartgas_frontend.shared.presentation.components.SecondaryButton
import com.smartgas_frontend.shared.presentation.components.Tag
import com.smartgas_frontend.shared.presentation.components.ToastEffect
import com.smartgas_frontend.shared.utils.TagSeverity
import com.smartgas_frontend.shared.utils.formatDate
import com.smartgas_frontend.shared.utils.formatIncidentNotification
import com.smartgas_frontend.shared.utils.formatNumber
import com.smartgas_frontend.shared.utils.normalizeStatus
import com.smartgas_frontend.shared.utils.trIncidentType
import com.smartgas_frontend.shared.utils.trSeverity
import com.smartgas_frontend.shared.utils.trStatus
import com.smartgas_frontend.shared.utils.trZone
import com.smartgas_frontend.ui.theme.TagSuccess
import com.smartgas_frontend.ui.theme.TagWarning

/** Textos propios de la pagina (la web los define en `pageText`, fuera de los locales). */
private class PageText(es: Boolean) {
    val title = if (es) "Incidentes" else "Incidents"
    val subtitle =
        if (es) "Incidentes detectados automáticamente por el sistema IoT."
        else "Incidents automatically detected by the IoT system."
    val refresh = if (es) "Actualizar" else "Refresh"
    val activeTab = if (es) "Incidentes Activos" else "Active Incidents"
    val historyTab = if (es) "Historial" else "History"
    val notificationsTab = if (es) "Notificaciones" else "Notifications"

    val zone = if (es) "Zona" else "Zone"
    val sensor = "Sensor"
    val detectedValue = if (es) "Valor Detectado" else "Detected Value"
    val detectedAt = if (es) "Detectado" else "Detected At"

    val channel = if (es) "Canal" else "Channel"
    val read = if (es) "Leído" else "Read"
    val confirmed = if (es) "Confirmado" else "Confirmed"

    val review = if (es) "Revisar" else "Review"
    val resolve = if (es) "Resolver" else "Resolve"
    val falseAlarm = if (es) "Falsa alarma" else "False alarm"
    val markAsRead = if (es) "Marcar como leído" else "Mark as read"
    val confirmReceipt = if (es) "Confirmar recepción" else "Confirm receipt"

    val yes = if (es) "Sí" else "Yes"
    val no = "No"

    val noActiveIncidents = if (es) "Sin incidentes activos." else "No active incidents."
    val noHistoryIncidents = if (es) "No hay incidentes históricos." else "No historical incidents."
    val noNotifications = if (es) "No hay notificaciones disponibles." else "No notifications available."
}

private fun severityStyle(severity: String?): TagSeverity = when (severity.orEmpty().lowercase()) {
    "critical" -> TagSeverity.Danger
    "high" -> TagSeverity.Warning
    "medium" -> TagSeverity.Info
    else -> TagSeverity.Success
}

private fun statusStyle(status: String?): TagSeverity = when (status.orEmpty().lowercase()) {
    "active" -> TagSeverity.Danger
    "reviewed" -> TagSeverity.Warning
    "resolved" -> TagSeverity.Success
    "falsealarm", "false alarm" -> TagSeverity.Info
    else -> TagSeverity.Secondary
}

private val gasRegex = Regex("Gas:\\s*([\\d.]+)", RegexOption.IGNORE_CASE)
private val tempRegex = Regex("Temp:\\s*([\\d.]+)", RegexOption.IGNORE_CASE)
private val incidentMessageRegex =
    Regex("^Sensor (.+?) detected a (.+?) event in zone (.+?)\\.$", RegexOption.IGNORE_CASE)

private fun incidentValueLines(incident: Incident): List<String> {
    val type = normalizeStatus(incident.type)
    val rawValue = incident.detectedValue

    val gas = incident.gasLevel?.let(::formatNumber) ?: gasRegex.find(rawValue)?.groupValues?.get(1)
    val temperature = incident.temperature?.let(::formatNumber) ?: tempRegex.find(rawValue)?.groupValues?.get(1)

    val fallback = listOf(rawValue.ifEmpty { "—" })

    return when (type) {
        "gasleak" -> if (gas != null) listOf("Gas: $gas ppm") else fallback
        "hightemperature" -> if (temperature != null) listOf("Temperatura: $temperature °C") else fallback
        else -> {
            val lines = mutableListOf<String>()

            if (gas != null) lines.add("Gas: $gas ppm")
            if (temperature != null) lines.add("Temperatura: $temperature °C")

            lines.ifEmpty { fallback }
        }
    }
}

private fun notificationMessage(strings: Strings, notification: Notification): String {
    val translated = formatIncidentNotification(strings, notification)

    if (translated.isNotEmpty() && translated != notification.message) {
        return translated
    }

    val match = incidentMessageRegex.find(notification.message)

    if (match != null) {
        val sensor = match.groupValues[1]
        val type = trIncidentType(strings, match.groupValues[2])
        val zone = trZone(strings, match.groupValues[3])

        return if (strings.isSpanish) {
            "El sensor $sensor detectó un evento de $type en la zona $zone."
        } else {
            "Sensor $sensor detected a $type event in zone $zone."
        }
    }

    return notification.message.ifEmpty { "—" }
}

@Composable
fun IncidentsScreen(viewModel: IncidentsViewModel, initialTab: String? = null) {
    val strings = LocalStrings.current
    val pageText = PageText(strings.isSpanish)
    val state = viewModel.state.value
    val data = state.data ?: IncidentsData()
    val loading = state.isLoading
    val activeTab = viewModel.activeTab.value

    LaunchedEffect(initialTab) {
        if (initialTab != null) viewModel.onTabChanged(initialTab)
    }

    ToastEffect(viewModel.toasts)

    ContentPage {
        PageHeader(title = pageText.title, subtitle = pageText.subtitle) {
            SecondaryButton(
                label = pageText.refresh,
                icon = Icons.Outlined.Refresh,
                loading = loading,
                onClick = viewModel::loadData
            )
        }

        val tabs = listOf(
            TAB_ACTIVE to pageText.activeTab,
            TAB_HISTORY to pageText.historyTab,
            TAB_NOTIFICATIONS to pageText.notificationsTab
        )

        PrimaryTabRow(selectedTabIndex = tabs.indexOfFirst { it.first == activeTab }.coerceAtLeast(0)) {
            tabs.forEach { (tab, label) ->
                Tab(
                    selected = activeTab == tab,
                    onClick = { viewModel.onTabChanged(tab) },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(label, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            if (tab == TAB_NOTIFICATIONS && data.unreadNotificationsCount > 0) {
                                Badge { Text(data.unreadNotificationsCount.toString()) }
                            }
                        }
                    }
                )
            }
        }

        if (loading) {
            LoadingState()
        }

        when (activeTab) {
            TAB_ACTIVE -> {
                data.activeIncidents.forEach { incident ->
                    IncidentCard(strings, pageText, incident) {
                        IncidentActions(
                            pageText = pageText,
                            incident = incident,
                            onReview = { viewModel.reviewIncident(incident) },
                            onResolve = { viewModel.resolveIncident(incident) },
                            onFalseAlarm = { viewModel.markFalseAlarm(incident) }
                        )
                    }
                }

                if (!loading && data.activeIncidents.isEmpty()) {
                    PanelCard { EmptyMessage(pageText.noActiveIncidents) }
                }
            }

            TAB_HISTORY -> {
                data.historyIncidents.forEach { incident ->
                    IncidentCard(strings, pageText, incident)
                }

                if (!loading && data.historyIncidents.isEmpty()) {
                    PanelCard { EmptyMessage(pageText.noHistoryIncidents) }
                }
            }

            else -> {
                data.notifications.forEach { notification ->
                    NotificationCard(
                        strings = strings,
                        pageText = pageText,
                        notification = notification,
                        onMarkRead = { viewModel.markNotificationRead(notification) },
                        onConfirm = { viewModel.confirmNotification(notification) }
                    )
                }

                if (!loading && data.notifications.isEmpty()) {
                    PanelCard { EmptyMessage(pageText.noNotifications) }
                }
            }
        }
    }
}

@Composable
private fun IncidentCard(
    strings: Strings,
    pageText: PageText,
    incident: Incident,
    actions: (@Composable () -> Unit)? = null
) {
    PanelCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(incident.code, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    trIncidentType(strings, incident.type),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Tag(value = trSeverity(strings, incident.severity), severity = severityStyle(incident.severity))
                Tag(value = trStatus(strings, incident.status), severity = statusStyle(incident.status))
            }
        }

        Column {
            DetailRow(pageText.zone, trZone(strings, incident.zoneName))
            DetailRow(pageText.sensor, incident.sensorCode.ifEmpty { "Sensor ${incident.sensorId ?: "—"}" })
            DetailRow(pageText.detectedValue, incidentValueLines(incident).joinToString("\n"))
            DetailRow(pageText.detectedAt, formatDate(incident.detectedAt))
        }

        actions?.invoke()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IncidentActions(
    pageText: PageText,
    incident: Incident,
    onReview: () -> Unit,
    onResolve: () -> Unit,
    onFalseAlarm: () -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (canReview(incident)) {
            SecondaryButton(label = pageText.review, onClick = onReview)
        }
        if (canClose(incident)) {
            PrimaryButton(label = pageText.resolve, onClick = onResolve, containerColor = TagSuccess)
            PrimaryButton(label = pageText.falseAlarm, onClick = onFalseAlarm, containerColor = TagWarning)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NotificationCard(
    strings: Strings,
    pageText: PageText,
    notification: Notification,
    onMarkRead: () -> Unit,
    onConfirm: () -> Unit
) {
    PanelCard {
        Text(notificationMessage(strings, notification), color = MaterialTheme.colorScheme.onSurface)

        Column {
            DetailRow(pageText.channel, notification.channel)
            DetailRow(pageText.read) {
                Tag(
                    value = if (notification.read) pageText.yes else pageText.no,
                    severity = if (notification.read) TagSeverity.Success else TagSeverity.Danger
                )
            }
            DetailRow(pageText.confirmed) {
                Tag(
                    value = if (notification.confirmed) pageText.yes else pageText.no,
                    severity = if (notification.confirmed) TagSeverity.Success else TagSeverity.Secondary
                )
            }
            DetailRow(pageText.detectedAt, formatDate(notification.createdAt))
        }

        if (!notification.read || !notification.confirmed) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!notification.read) {
                    SecondaryButton(label = pageText.markAsRead, onClick = onMarkRead)
                }
                if (!notification.confirmed) {
                    PrimaryButton(label = pageText.confirmReceipt, onClick = onConfirm, containerColor = TagSuccess)
                }
            }
        }
    }
}
