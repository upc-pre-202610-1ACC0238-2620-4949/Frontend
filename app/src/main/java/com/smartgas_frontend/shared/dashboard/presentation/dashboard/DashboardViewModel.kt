package com.smartgas_frontend.shared.dashboard.presentation.dashboard

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartgas_frontend.common.Resource
import com.smartgas_frontend.common.UIState
import com.smartgas_frontend.shared.dashboard.data.repository.DashboardRepository
import com.smartgas_frontend.shared.dashboard.domain.model.DashboardSummary
import com.smartgas_frontend.shared.dashboard.domain.model.Weather
import com.smartgas_frontend.shared.data.local.SessionService
import com.smartgas_frontend.shared.utils.normalizeStatus
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: DashboardRepository,
    private val sessionService: SessionService
) : ViewModel() {

    private val _state = mutableStateOf(UIState<DashboardSummary>(isLoading = true))
    val state: State<UIState<DashboardSummary>> get() = _state

    private val _weatherState = mutableStateOf(UIState<Weather>())
    val weatherState: State<UIState<Weather>> get() = _weatherState

    init {
        loadData()
    }

    private fun loadWeather() {
        _weatherState.value = UIState(isLoading = true)

        viewModelScope.launch {
            val result = repository.getCurrentWeather()

            if (result is Resource.Success) {
                _weatherState.value = UIState(data = result.data)
            } else {
                _weatherState.value = UIState(message = result.message ?: "An error occurred")
            }
        }
    }

    fun loadData() {
        _state.value = UIState(isLoading = true, data = _state.value.data)
        loadWeather()

        viewModelScope.launch {
            val result = repository.getDashboardData(sessionService.getAccountId())

            if (result is Resource.Success) {
                _state.value = UIState(data = result.data)
            } else {
                _state.value = UIState(data = _state.value.data, message = result.message ?: "An error occurred")
            }
        }
    }
}

fun effectiveOverallStatus(data: DashboardSummary): String {
    val zoneStatuses = data.zones.map { normalizeStatus(it.status) }

    return when {
        "critical" in zoneStatuses -> "Critical"
        "warning" in zoneStatuses -> "Warning"
        data.activeIncidents.isNotEmpty() || data.pendingAlerts.isNotEmpty() -> "Warning"
        else -> data.overallStatus.ifEmpty { "Safe" }
    }
}
