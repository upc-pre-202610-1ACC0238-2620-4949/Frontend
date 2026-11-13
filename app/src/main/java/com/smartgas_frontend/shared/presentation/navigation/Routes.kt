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

    // Cuelgan de app/zones/ para que la pestana Zones quede marcada (como en el diseno 07)
    const val SENSOR_ID_ARG = "sensorId"
    const val SENSOR_DETAIL = "app/zones/sensor/{$SENSOR_ID_ARG}"
    fun sensorDetail(sensorId: Int) = "app/zones/sensor/$sensorId"
    const val EDIT_SENSOR = "app/zones/sensor/{$SENSOR_ID_ARG}/edit"
    fun editSensor(sensorId: Int) = "app/zones/sensor/$sensorId/edit"

    // zoneId es opcional: desde Zone detail llega preseleccionada, desde el + de Zones no.
    // scan=true abre directamente el escaner QR (icono QR del Home, diseno 09).
    const val SCAN_ARG = "scan"
    const val ADD_SENSOR = "app/zones/new/sensor?$ZONE_ID_ARG={$ZONE_ID_ARG}&$SCAN_ARG={$SCAN_ARG}"
    fun addSensor(zoneId: Int? = null, scan: Boolean = false): String {
        val params = listOfNotNull(
            zoneId?.let { "$ZONE_ID_ARG=$it" },
            if (scan) "$SCAN_ARG=true" else null
        )
        return if (params.isEmpty()) "app/zones/new/sensor" else "app/zones/new/sensor?" + params.joinToString("&")
    }

    const val SENSORS = "app/sensors"

    const val NOT_FOUND = "not-found"
}
