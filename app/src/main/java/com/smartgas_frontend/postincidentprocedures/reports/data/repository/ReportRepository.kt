package com.smartgas_frontend.postincidentprocedures.reports.data.repository

import com.smartgas_frontend.common.Resource
import com.smartgas_frontend.incidentdetection.incidents.data.remote.IncidentService
import com.smartgas_frontend.incidentdetection.incidents.data.remote.dto.toAlert
import com.smartgas_frontend.incidentdetection.incidents.data.remote.dto.toIncident
import com.smartgas_frontend.kitchenmonitoring.monitoring.data.remote.MonitoringService
import com.smartgas_frontend.kitchenmonitoring.monitoring.data.remote.dto.toZone
import com.smartgas_frontend.postincidentprocedures.reports.domain.model.SecurityReport
import com.smartgas_frontend.shared.data.remote.bodyOrThrow
import com.smartgas_frontend.shared.data.remote.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class ReportRepository(
    private val incidentService: IncidentService,
    private val monitoringService: MonitoringService
) {

    suspend fun getReportData(accountId: Int): Resource<SecurityReport> = withContext(Dispatchers.IO) {
        safeCall {
            coroutineScope {
                val incidents = async { incidentService.getIncidents(accountId).bodyOrThrow() }
                val alerts = async { incidentService.getAlerts(accountId).bodyOrThrow() }
                val zones = async { monitoringService.getZones(accountId).bodyOrThrow() }

                SecurityReport(
                    incidents = incidents.await().map { it.toIncident() },
                    alerts = alerts.await().map { it.toAlert() },
                    zones = zones.await().map { it.toZone() }
                )
            }
        }
    }
}
