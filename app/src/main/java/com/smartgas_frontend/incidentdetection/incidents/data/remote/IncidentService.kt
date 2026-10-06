package com.smartgas_frontend.incidentdetection.incidents.data.remote

import com.smartgas_frontend.incidentdetection.incidents.data.remote.dto.AlertDto
import com.smartgas_frontend.incidentdetection.incidents.data.remote.dto.IncidentDto
import com.smartgas_frontend.incidentdetection.incidents.data.remote.dto.NotificationDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface IncidentService {
    @GET("incidents")
    suspend fun getIncidents(@Query("accountId") accountId: Int): Response<List<IncidentDto>>

    @GET("alerts")
    suspend fun getAlerts(@Query("accountId") accountId: Int): Response<List<AlertDto>>

    @GET("notifications")
    suspend fun getNotifications(@Query("accountId") accountId: Int): Response<List<NotificationDto>>

    @PATCH("incidents/{id}/review")
    suspend fun reviewIncident(@Path("id") incidentId: Int): Response<IncidentDto>

    @PATCH("incidents/{id}/resolve")
    suspend fun resolveIncident(@Path("id") incidentId: Int): Response<IncidentDto>

    @PATCH("incidents/{id}/false-alarm")
    suspend fun markFalseAlarm(@Path("id") incidentId: Int): Response<IncidentDto>

    @PATCH("notifications/{id}/read")
    suspend fun markNotificationRead(@Path("id") notificationId: Int): Response<NotificationDto>

    @PATCH("notifications/{id}/confirm")
    suspend fun confirmNotification(@Path("id") notificationId: Int): Response<NotificationDto>
}
