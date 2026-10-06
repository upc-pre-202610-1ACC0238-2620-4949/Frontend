package com.smartgas_frontend.postincidentprocedures.reports.domain.model

import com.smartgas_frontend.incidentdetection.incidents.domain.model.Incident
import com.smartgas_frontend.incidentpreventionnotification.domain.model.Alert
import com.smartgas_frontend.kitchenmonitoring.monitoring.domain.model.Zone

data class SecurityReport(
    val incidents: List<Incident> = emptyList(),
    val alerts: List<Alert> = emptyList(),
    val zones: List<Zone> = emptyList()
)

data class GeneratedReport(
    val zoneLabel: String,
    val periodLabel: String,
    val total: Int,
    val critical: Int,
    val warning: Int,
    val resolved: Int,
    val pending: Int,
    val mostAffectedZone: String
)
