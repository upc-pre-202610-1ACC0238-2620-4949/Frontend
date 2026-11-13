package com.smartgas_frontend.shared.presentation.navigation

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val APP = "app"

    const val HOME = "app/home"
    const val ZONES = "app/zones"
    const val ALERTS = "app/alerts"
    const val HISTORY = "app/history"
    const val SETTINGS = "app/settings"

    const val ZONE_ID_ARG = "zoneId"
    const val ZONE_DETAIL = "app/zones/{$ZONE_ID_ARG}"   // se abre al tocar una zona en Zones
    fun zoneDetail(zoneId: Int) = "app/zones/$zoneId"

    const val SENSOR_ID_ARG = "sensorId"
    const val SENSOR_DETAIL = "app/zones/sensor/{$SENSOR_ID_ARG}"
    fun sensorDetail(sensorId: Int) = "app/zones/sensor/$sensorId"
    const val EDIT_SENSOR = "app/zones/sensor/{$SENSOR_ID_ARG}/edit"
    fun editSensor(sensorId: Int) = "app/zones/sensor/$sensorId/edit"

    const val SCAN_ARG = "scan"
    const val ADD_SENSOR = "app/zones/new/sensor?$ZONE_ID_ARG={$ZONE_ID_ARG}&$SCAN_ARG={$SCAN_ARG}"
    fun addSensor(zoneId: Int? = null, scan: Boolean = false): String {
        val params = listOfNotNull(
            zoneId?.let { "$ZONE_ID_ARG=$it" },
            if (scan) "$SCAN_ARG=true" else null
        )
        return if (params.isEmpty()) "app/zones/new/sensor" else "app/zones/new/sensor?" + params.joinToString("&")
    }

    const val NOTIFICATION_ID_ARG = "notificationId"
    const val ALERT_DETAIL = "app/alerts/{$NOTIFICATION_ID_ARG}"   // diseno 12
    fun alertDetail(notificationId: Int) = "app/alerts/$notificationId"
    const val INCIDENT_ID_ARG = "incidentId"
    const val INCIDENT_DETAIL = "app/history/{$INCIDENT_ID_ARG}"   // diseno 13
    fun incidentDetail(incidentId: Int) = "app/history/$incidentId"

    const val SENSORS = "app/sensors"     // lista de sensores (desde Settings)
    const val PROFILE = "app/profile"     // se abre con el icono de usuario de Home
    const val PLANS = "app/plans"
    const val REPORTS = "app/reports"
    const val NOT_FOUND = "not-found"
}
