package com.smartgas_frontend.shared.presentation.components

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.smartgas_frontend.R
import com.smartgas_frontend.common.Resource
import com.smartgas_frontend.incidentdetection.incidents.data.repository.IncidentRepository
import com.smartgas_frontend.incidentpreventionnotification.domain.model.Notification
import com.smartgas_frontend.shared.data.local.SessionService
import kotlinx.coroutines.launch

/** Estado del centro de notificaciones de la barra superior (toolbar-content de la web). */
class ToolbarViewModel(
    private val repository: IncidentRepository,
    private val sessionService: SessionService
) : ToastViewModel() {

    private val _notifications = mutableStateOf<List<Notification>>(emptyList())
    val notifications: State<List<Notification>> get() = _notifications

    private val _notificationOpen = mutableStateOf(false)
    val notificationOpen: State<Boolean> get() = _notificationOpen

    val unreadNotifications: List<Notification> get() = _notifications.value.filter { !it.read || !it.confirmed }
    val latestNotifications: List<Notification> get() = _notifications.value.take(4)

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        val user = sessionService.getCurrentUser() ?: return

        viewModelScope.launch {
            val result = repository.getNotifications(user.id)

            _notifications.value = if (result is Resource.Success) result.data.orEmpty() else emptyList()
        }
    }

    fun toggleNotifications() {
        _notificationOpen.value = !_notificationOpen.value

        if (_notificationOpen.value) loadNotifications()
    }

    fun closeNotifications() {
        _notificationOpen.value = false
    }

    fun markRead(notification: Notification) {
        viewModelScope.launch {
            if (repository.markNotificationRead(notification.id) is Resource.Success) {
                loadNotifications()
                toast(ToastSeverity.Info, res(R.string.notifRead))
            } else {
                toast(ToastSeverity.Error, res(R.string.errorSaving))
            }
        }
    }

    fun confirmReception(notification: Notification) {
        viewModelScope.launch {
            if (repository.confirmNotification(notification.id) is Resource.Success) {
                loadNotifications()
                toast(ToastSeverity.Success, res(R.string.notifConfirmed))
            } else {
                toast(ToastSeverity.Error, res(R.string.errorSaving))
            }
        }
    }
}
