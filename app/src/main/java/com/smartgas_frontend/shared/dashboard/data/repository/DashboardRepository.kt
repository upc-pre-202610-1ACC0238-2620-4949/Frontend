package com.smartgas_frontend.shared.dashboard.data.repository

import com.smartgas_frontend.common.Constants
import com.smartgas_frontend.common.Resource
import com.smartgas_frontend.incidentdetection.incidents.data.remote.IncidentService
import com.smartgas_frontend.incidentdetection.incidents.data.remote.dto.toAlert
import com.smartgas_frontend.incidentdetection.incidents.data.remote.dto.toIncident
import com.smartgas_frontend.kitchenmonitoring.devices.data.remote.SensorService
import com.smartgas_frontend.kitchenmonitoring.devices.data.remote.dto.toSensor
import com.smartgas_frontend.kitchenmonitoring.monitoring.data.remote.MonitoringService
import com.smartgas_frontend.kitchenmonitoring.monitoring.data.remote.dto.toSensorReading
import com.smartgas_frontend.kitchenmonitoring.monitoring.data.remote.dto.toZone
import com.smartgas_frontend.paymentmanagement.subscriptions.data.remote.SubscriptionService
import com.smartgas_frontend.paymentmanagement.subscriptions.data.remote.dto.matchPlan
import com.smartgas_frontend.paymentmanagement.subscriptions.data.remote.dto.toPlan
import com.smartgas_frontend.paymentmanagement.subscriptions.data.remote.dto.toSubscription
import com.smartgas_frontend.paymentmanagement.subscriptions.domain.model.Plan
import com.smartgas_frontend.shared.dashboard.data.remote.ExternalWeatherService
import com.smartgas_frontend.shared.dashboard.data.remote.toWeather
import com.smartgas_frontend.shared.dashboard.domain.model.DashboardSummary
import com.smartgas_frontend.shared.dashboard.domain.model.Weather
import com.smartgas_frontend.shared.data.remote.bodyOrThrow
import com.smartgas_frontend.shared.data.remote.safeCall
import com.smartgas_frontend.shared.utils.dateMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class DashboardRepository(
    private val sensorService: SensorService,
    private val monitoringService: MonitoringService,
    private val incidentService: IncidentService,
    private val subscriptionService: SubscriptionService,
    private val weatherService: ExternalWeatherService
) {

    private fun normalizeStatus(status: String): String = if (status == "Active") "Warning" else status

    suspend fun getDashboardData(accountId: Int): Resource<DashboardSummary> = withContext(Dispatchers.IO) {
        safeCall {
            coroutineScope {
                val sensorsResponse = async { sensorService.getSensors(accountId).bodyOrThrow() }
                val zonesResponse = async { monitoringService.getZones(accountId).bodyOrThrow() }
                val incidentsResponse = async { incidentService.getIncidents(accountId).bodyOrThrow() }
                val alertsResponse = async { incidentService.getAlerts(accountId).bodyOrThrow() }
                val readingsResponse = async { monitoringService.getSensorReadings(accountId).bodyOrThrow() }
                val subscriptionResponse = async { subscriptionService.getCurrentSubscription(accountId).bodyOrThrow() }
                val plansResponse = async { subscriptionService.getPlans().bodyOrThrow() }

                val sensors = sensorsResponse.await().map { it.toSensor() }
                val incidents = incidentsResponse.await().map { it.toIncident() }
                val alerts = alertsResponse.await().map { it.toAlert() }
                val sensorReadings = readingsResponse.await()
                    .map { it.toSensorReading() }
                    .sortedByDescending { dateMillis(it.timestamp) }

                val latestReadingByZoneId = sensorReadings
                    .filter { it.zoneId != null }
                    .groupBy { it.zoneId }
                    .mapValues { (_, readings) -> readings.first() }

                val zones = zonesResponse.await().map { dto ->
                    val zone = dto.toZone()
                    val latestReading = latestReadingByZoneId[zone.id]

                    zone.copy(
                        gasLevel = latestReading?.gasValue ?: zone.gasLevel,
                        temperature = latestReading?.temperatureValue ?: zone.temperature,
                        status = normalizeStatus(zone.status)
                    )
                }

                val subscriptionDto = subscriptionResponse.await()
                val plans = plansResponse.await().map { it.toPlan() }
                val subscription = subscriptionDto.toSubscription(plans)

                val activeIncidents = incidents.filter { it.status == "Active" || it.status == "Reviewed" }
                val pendingAlerts = alerts.filter { it.status == "Active" || it.status == "Pending" }

                val overallStatus = when {
                    activeIncidents.any { it.severity == "Critical" } -> "Critical"
                    activeIncidents.isNotEmpty() || pendingAlerts.isNotEmpty() -> "Warning"
                    else -> "Safe"
                }

                val plan = subscriptionDto.matchPlan(plans) ?: Plan(
                    id = subscription.planId,
                    name = subscription.planName,
                    price = subscription.price,
                    maxZones = subscription.maxZones,
                    maxSensors = subscription.maxSensors
                )

                DashboardSummary(
                    sensors = sensors,
                    zones = zones,
                    activeIncidents = activeIncidents,
                    pendingAlerts = pendingAlerts,
                    overallStatus = overallStatus,
                    plan = plan,
                    subscription = subscription,
                    lastReading = sensorReadings.firstOrNull(),
                    criticalZone = zones.find { it.status == "Critical" } ?: zones.find { it.status == "Warning" }
                )
            }
        }
    }

    suspend fun getCurrentWeather(
        latitude: Double = Constants.DEFAULT_LATITUDE,
        longitude: Double = Constants.DEFAULT_LONGITUDE
    ): Resource<Weather> = withContext(Dispatchers.IO) {
        safeCall { weatherService.getCurrentWeather(latitude, longitude).bodyOrThrow().toWeather() }
    }
}
