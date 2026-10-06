package com.smartgas_frontend.shared.presentation.navigation

/** Mismas rutas que app/router/index.js de la web. */
object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val APP = "app"

    const val DASHBOARD = "app/dashboard"
    const val MONITORING = "app/monitoring"
    const val DEVICES = "app/devices"
    const val INCIDENTS = "app/incidents"
    const val REPORTS = "app/reports"
    const val SUBSCRIPTION = "app/subscription"
    const val PROFILE = "app/profile"
    const val SETTINGS = "app/settings"
    const val NOT_FOUND = "not-found"

    const val INCIDENTS_TAB_ARG = "tab"
    const val INCIDENTS_PATTERN = "$INCIDENTS?$INCIDENTS_TAB_ARG={$INCIDENTS_TAB_ARG}"

    fun incidents(tab: String) = "$INCIDENTS?$INCIDENTS_TAB_ARG=$tab"
}
