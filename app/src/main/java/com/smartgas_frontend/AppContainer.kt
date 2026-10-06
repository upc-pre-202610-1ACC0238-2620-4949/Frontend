package com.smartgas_frontend

import android.content.Context
import androidx.compose.runtime.staticCompositionLocalOf
import com.smartgas_frontend.iam.data.remote.AuthService
import com.smartgas_frontend.iam.data.repository.AccountRepository
import com.smartgas_frontend.iam.profile.data.remote.ProfileService
import com.smartgas_frontend.iam.profile.data.repository.ProfileRepository
import com.smartgas_frontend.iam.settings.data.remote.SettingsService
import com.smartgas_frontend.iam.settings.data.repository.SettingsRepository
import com.smartgas_frontend.incidentdetection.incidents.data.remote.IncidentService
import com.smartgas_frontend.incidentdetection.incidents.data.repository.IncidentRepository
import com.smartgas_frontend.kitchenmonitoring.devices.data.remote.SensorService
import com.smartgas_frontend.kitchenmonitoring.devices.data.repository.SensorRepository
import com.smartgas_frontend.kitchenmonitoring.monitoring.data.remote.MonitoringService
import com.smartgas_frontend.kitchenmonitoring.monitoring.data.repository.MonitoringRepository
import com.smartgas_frontend.paymentmanagement.subscriptions.data.remote.SubscriptionService
import com.smartgas_frontend.paymentmanagement.subscriptions.data.repository.SubscriptionRepository
import com.smartgas_frontend.postincidentprocedures.reports.data.repository.ReportRepository
import com.smartgas_frontend.shared.dashboard.data.remote.ExternalWeatherService
import com.smartgas_frontend.shared.dashboard.data.repository.DashboardRepository
import com.smartgas_frontend.shared.data.local.AppPreferences
import com.smartgas_frontend.shared.data.local.SessionService
import com.smartgas_frontend.shared.data.remote.ApiClient

/**
 * Crea una sola vez los servicios de Retrofit y los repositorios de cada bounded context
 * (igual que el ejemplo arma service -> repository -> viewModel en MainActivity).
 */
class AppContainer(context: Context) {
    val sessionService = SessionService(context)
    val preferences = AppPreferences(context)

    private val authService: AuthService = ApiClient.create()
    private val profileService: ProfileService = ApiClient.create()
    private val settingsService: SettingsService = ApiClient.create()
    private val sensorService: SensorService = ApiClient.create()
    private val monitoringService: MonitoringService = ApiClient.create()
    private val incidentService: IncidentService = ApiClient.create()
    private val subscriptionService: SubscriptionService = ApiClient.create()
    private val weatherService: ExternalWeatherService = ApiClient.create()

    // iam
    val accountRepository = AccountRepository(authService, sessionService)
    val profileRepository = ProfileRepository(
        profileService, subscriptionService, sensorService, monitoringService, incidentService, sessionService
    )
    val settingsRepository = SettingsRepository(settingsService, monitoringService, sensorService)

    // kitchen-monitoring
    val sensorRepository = SensorRepository(sensorService, monitoringService, subscriptionService)
    val monitoringRepository = MonitoringRepository(monitoringService, sensorService, subscriptionService)

    // incident-detection / incident-prevention-notification
    val incidentRepository = IncidentRepository(incidentService)

    // post-incident-procedures
    val reportRepository = ReportRepository(incidentService, monitoringService)

    // payment-management
    val subscriptionRepository = SubscriptionRepository(subscriptionService)

    // shared
    val dashboardRepository = DashboardRepository(
        sensorService, monitoringService, incidentService, subscriptionService, weatherService
    )
}

val LocalAppContainer = staticCompositionLocalOf<AppContainer> {
    error("AppContainer not provided")
}
