package com.smartgas_frontend.kitchenmonitoring.presentation.zonedetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.GppGood
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smartgas_frontend.R
import com.smartgas_frontend.common.Resource
import com.smartgas_frontend.kitchenmonitoring.data.repository.EMPTY_ZONE_NAME
import com.smartgas_frontend.kitchenmonitoring.data.repository.ZONE_HAS_SENSORS
import com.smartgas_frontend.kitchenmonitoring.data.repository.ZoneDraft
import com.smartgas_frontend.kitchenmonitoring.domain.model.Sensor
import com.smartgas_frontend.kitchenmonitoring.domain.model.SensorReading
import com.smartgas_frontend.kitchenmonitoring.presentation.components.GasSeriesColor
import com.smartgas_frontend.kitchenmonitoring.presentation.components.LegendDot
import com.smartgas_frontend.kitchenmonitoring.presentation.components.ReadingsChart
import com.smartgas_frontend.kitchenmonitoring.presentation.components.TemperatureSeriesColor
import com.smartgas_frontend.shared.i18n.LocalStrings
import com.smartgas_frontend.shared.i18n.Strings
import com.smartgas_frontend.shared.presentation.components.AlertBanner
import com.smartgas_frontend.shared.presentation.components.ContentPage
import com.smartgas_frontend.shared.presentation.components.EmptyMessage
import com.smartgas_frontend.shared.presentation.components.IconBadge
import com.smartgas_frontend.shared.presentation.components.LoadingState
import com.smartgas_frontend.shared.presentation.components.PanelCard
import com.smartgas_frontend.shared.presentation.components.SecondaryButton
import com.smartgas_frontend.shared.presentation.components.Tag
import com.smartgas_frontend.shared.utils.formatNumber
import com.smartgas_frontend.shared.utils.statusClass
import com.smartgas_frontend.shared.utils.trSensorName
import com.smartgas_frontend.shared.utils.trSensorType
import com.smartgas_frontend.shared.utils.trStatus
import com.smartgas_frontend.shared.utils.trZone
import com.smartgas_frontend.ui.theme.TagDanger
import com.smartgas_frontend.ui.theme.TagSecondary
import com.smartgas_frontend.ui.theme.TagSuccess
import com.smartgas_frontend.ui.theme.TagWarning

/** Textos propios de la pagina (mismo patron PageText que el resto de pantallas). */
private class PageText(private val es: Boolean) {
    val back = if (es) "Volver" else "Back"
    val refresh = if (es) "Actualizar" else "Refresh"
    val zoneStatus = if (es) "Estado de la zona" else "Zone status"
    val lastHours = if (es) "Últimas 6 horas" else "Last 6 hours"
    val gas = "Gas"
    val temp = "Temp"
    val threshold = if (es) "umbral" else "threshold"
    val sensors = if (es) "Sensores" else "Sensors"
    val battery = if (es) "Batería" else "Battery"
    val noSensors = if (es) "Esta zona no tiene sensores." else "This zone has no sensors."
    val noReadings = if (es) "Aún no hay lecturas para esta zona." else "No readings for this zone yet."
    val notFound = if (es) "No se encontró la zona." else "Zone not found."
    val addSensor = if (es) "Agregar sensor" else "Add sensor"
    val edit = if (es) "Editar zona" else "Edit zone"
    val delete = if (es) "Eliminar zona" else "Delete zone"
    val name = if (es) "Nombre" else "Name"
    val description = if (es) "Descripción" else "Description"
    val save = if (es) "Guardar" else "Save"
    val cancel = if (es) "Cancelar" else "Cancel"
    val confirmDelete = if (es) "¿Eliminar esta zona? Esta acción no se puede deshacer." else "Delete this zone? This can't be undone."
    val nameRequired = if (es) "Ingresa un nombre para la zona." else "Enter a name for the zone."
    val hasSensors = if (es) "La zona todavía tiene sensores. Muévelos o elimínalos primero." else "The zone still has sensors. Move or delete them first."

    fun gasSummary(gas: String, percent: Int?, threshold: String): String {
        val base = if (es) "Gas en $gas ppm" else "Gas at $gas ppm"
        if (percent == null) return base
        return if (es) "$base · $percent % del umbral de $threshold ppm"
        else "$base · $percent % of the $threshold ppm threshold"
    }

}

private fun statusColor(status: String?): Color = when (status) {
    "Critical" -> TagDanger
    "Warning" -> TagWarning
    "Offline" -> TagSecondary
    else -> TagSuccess
}

@Composable
fun ZoneDetailScreen(
    viewModel: ZoneDetailViewModel,
    onBack: () -> Unit,
    onOpenSensor: (Int) -> Unit,
    onAddSensor: (Int) -> Unit
) {
    val strings = LocalStrings.current
    val pageText = PageText(strings.isSpanish)
    val state = viewModel.state.value
    val data = state.data ?: ZoneDetailData()
    val zone = data.zone
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var actionError by remember { mutableStateOf("") }

    fun errorText(code: String?): String = when (code) {
        ZONE_HAS_SENSORS -> pageText.hasSensors
        EMPTY_ZONE_NAME -> pageText.nameRequired
        else -> strings.t(R.string.apiError)
    }

    // Se recarga cada vez que la pantalla vuelve a mostrarse (ej. al regresar de Add sensor),
    // por eso la carga esta aqui y no en el init del ViewModel.
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    ContentPage {
        // Barra superior del diseno 06: flecha atras + nombre de la zona
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = pageText.back)
            }
            Text(
                text = zone?.let { trZone(strings, it.name) } ?: "",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .weight(1f)
                    .semantics { heading() }
            )
            IconButton(onClick = viewModel::loadData, enabled = !state.isLoading) {
                Icon(Icons.Outlined.Refresh, contentDescription = pageText.refresh)
            }
            if (zone != null) {
                IconButton(onClick = { showEditDialog = true }) {
                    Icon(Icons.Outlined.Edit, contentDescription = pageText.edit)
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Outlined.Delete, contentDescription = pageText.delete)
                }
            }
        }

        if (actionError.isNotEmpty()) {
            AlertBanner(actionError)
        }

        if (showEditDialog && zone != null) {
            var name by remember { mutableStateOf(zone.name) }
            var description by remember { mutableStateOf(zone.description) }

            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text(pageText.edit) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text(pageText.name) },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text(pageText.description) }
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        showEditDialog = false
                        viewModel.updateZone(
                            ZoneDraft(name = name, description = description, sensitivity = zone.sensitivity)
                        ) { result ->
                            actionError = if (result is Resource.Error) errorText(result.message) else ""
                        }
                    }) {
                        Text(pageText.save)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) {
                        Text(pageText.cancel)
                    }
                }
            )
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text(pageText.delete) },
                text = { Text(pageText.confirmDelete) },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        viewModel.deleteZone { result ->
                            if (result is Resource.Success) {
                                onBack()
                            } else {
                                actionError = errorText(result.message)
                            }
                        }
                    }) {
                        Text(pageText.delete, color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text(pageText.cancel)
                    }
                }
            )
        }

        if (state.isLoading && zone == null) {
            LoadingState()
        }

        if (state.message.isNotEmpty()) {
            AlertBanner(if (zone == null) pageText.notFound else strings.t(R.string.apiError))
        }

        if (zone != null) {
            ZoneStatusCard(
                pageText = pageText,
                statusLabel = trStatus(strings, zone.status),
                status = zone.status,
                summary = pageText.gasSummary(
                    gas = formatNumber(zone.gasLevel),
                    percent = data.gasPercentOfThreshold,
                    threshold = formatNumber(data.gasThreshold)
                )
            )

            PanelCard(
                title = pageText.lastHours,
                headerAction = { ChartLegend(pageText) }
            ) {
                val chartReadings = data.chartReadings

                if (chartReadings.size < 2) {
                    EmptyMessage(pageText.noReadings)
                } else {
                    ReadingsChart(
                        readings = chartReadings,
                        threshold = data.gasThreshold,
                        thresholdLabel = "${pageText.threshold} ${formatNumber(data.gasThreshold)}"
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    pageText.sensors,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .weight(1f)
                        .semantics { heading() }
                )
                SecondaryButton(
                    label = pageText.addSensor,
                    icon = Icons.Outlined.Add,
                    onClick = { onAddSensor(zone.id) }
                )
            }

            PanelCard {
                if (data.sensors.isEmpty()) {
                    EmptyMessage(pageText.noSensors)
                }

                data.sensors.forEachIndexed { index, sensor ->
                    if (index > 0) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                    SensorRow(strings, pageText, sensor, data.latestReadingFor(sensor)) { onOpenSensor(sensor.id) }
                }
            }
        }
    }
}

/** Tarjeta de color del diseno: "Zone status · Safe · Gas at ...". */
@Composable
private fun ZoneStatusCard(pageText: PageText, statusLabel: String, status: String, summary: String) {
    val color = statusColor(status)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "${pageText.zoneStatus}: $statusLabel. $summary" },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(pageText.zoneStatus, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                Text(
                    statusLabel,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(summary, color = Color.White, style = MaterialTheme.typography.bodyMedium)
            }
            Icon(
                imageVector = if (status == "Safe") Icons.Outlined.GppGood else Icons.Outlined.WarningAmber,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
private fun ChartLegend(pageText: PageText) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
        LegendDot(GasSeriesColor, pageText.gas)
        LegendDot(TemperatureSeriesColor, pageText.temp)
    }
}

/** Fila de sensor del diseno: icono, codigo · tipo, bateria, valor actual y estado. */
@Composable
private fun SensorRow(
    strings: Strings,
    pageText: PageText,
    sensor: Sensor,
    latest: SensorReading?,
    onClick: () -> Unit
) {
    val type = sensor.type.lowercase()
    val value = when {
        latest == null -> sensor.lastReading.ifBlank { "—" }
        type.contains("temp") -> "${formatNumber(latest.temperatureValue)} °C"
        type.contains("gas") -> "${formatNumber(latest.gasValue)} ppm"
        else -> listOfNotNull(
            latest.gasValue?.let { "${formatNumber(it)} ppm" },
            latest.temperatureValue?.let { "${formatNumber(it)} °C" }
        ).joinToString(" · ").ifEmpty { "—" }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconBadge(Icons.Outlined.Sensors)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                "${sensor.code} · ${trSensorName(strings, sensor.name).ifBlank { trSensorType(strings, sensor.type) }}",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "${pageText.battery} ${sensor.battery} %",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(value, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Tag(value = trStatus(strings, sensor.status), severity = statusClass(sensor.status))
        }
    }
}
