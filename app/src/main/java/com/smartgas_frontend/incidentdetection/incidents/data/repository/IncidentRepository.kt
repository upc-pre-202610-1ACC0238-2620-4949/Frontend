package com.smartgas_frontend.incidentdetection.incidents.data.repository

import com.smartgas_frontend.common.Resource
import com.smartgas_frontend.incidentdetection.incidents.data.remote.IncidentService
import com.smartgas_frontend.incidentdetection.incidents.data.remote.dto.toAlert
import com.smartgas_frontend.incidentdetection.incidents.data.remote.dto.toIncident
import com.smartgas_frontend.incidentdetection.incidents.data.remote.dto.toNotification
import com.smartgas_frontend.incidentdetection.incidents.domain.model.Incident
import com.smartgas_frontend.incidentpreventionnotification.domain.model.Alert
import com.smartgas_frontend.incidentpreventionnotification.domain.model.Notification
import com.smartgas_frontend.shared.data.remote.bodyOrThrow
import com.smartgas_frontend.shared.data.remote.safeCall
import com.smartgas_frontend.shared.utils.dateMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IncidentRepository(private val service: IncidentService) {

    suspend fun getIncidents(accountId: Int): Resource<List<Incident>> = withContext(Dispatchers.IO) {
        safeCall { service.getIncidents(accountId).bodyOrThrow().map { it.toIncident() } }
    }

    suspend fun getAlerts(accountId: Int): Resource<List<Alert>> = withContext(Dispatchers.IO) {
        safeCall { service.getAlerts(accountId).bodyOrThrow().map { it.toAlert() } }
    }

    suspend fun getNotifications(accountId: Int): Resource<List<Notification>> = withContext(Dispatchers.IO) {
        safeCall {
            service.getNotifications(accountId).bodyOrThrow()
                .map { it.toNotification() }
                .sortedByDescending { dateMillis(it.createdAt) }
        }
    }

    suspend fun markReviewed(incidentId: Int): Resource<Incident> = withContext(Dispatchers.IO) {
        safeCall { service.reviewIncident(incidentId).bodyOrThrow().toIncident() }
    }

    suspend fun markResolved(incidentId: Int): Resource<Incident> = withContext(Dispatchers.IO) {
        safeCall { service.resolveIncident(incidentId).bodyOrThrow().toIncident() }
    }

    suspend fun markFalseAlarm(incidentId: Int): Resource<Incident> = withContext(Dispatchers.IO) {
        safeCall { service.markFalseAlarm(incidentId).bodyOrThrow().toIncident() }
    }

    suspend fun markNotificationRead(notificationId: Int): Resource<Notification> = withContext(Dispatchers.IO) {
        safeCall { service.markNotificationRead(notificationId).bodyOrThrow().toNotification() }
    }

    suspend fun confirmNotification(notificationId: Int): Resource<Notification> = withContext(Dispatchers.IO) {
        safeCall { service.confirmNotification(notificationId).bodyOrThrow().toNotification() }
    }

    suspend fun addNote(incidentId: Int, note: String): Resource<Incident> =
        Resource.Error("Incident notes are not available in the current backend.")
}
