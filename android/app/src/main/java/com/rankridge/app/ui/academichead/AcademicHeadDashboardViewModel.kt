package com.rankridge.app.ui.academichead

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Holds the Academic Head dashboard state for every tab. UI-only: it "loads" [AcademicHeadMockData]
 * after a short simulated delay, mirroring every other role dashboard in this app. An academic
 * head's one real action here is resolving a compliance alert once it has been actioned — the rest
 * of this dashboard is oversight/reporting, read from the batch and faculty tables rather than
 * mutated directly (compliance itself is fixed by the faculty/teacher dashboards, not from here).
 */
class AcademicHeadDashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AcademicHeadDashboardUiState())
    val uiState: StateFlow<AcademicHeadDashboardUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun refresh() = load()

    fun resolveAlert(alertId: String) {
        _uiState.update { state ->
            val dashboard = state.dashboard ?: return@update state
            val updated = dashboard.alerts.map {
                if (it.id == alertId) it.copy(resolved = true) else it
            }
            state.copy(dashboard = dashboard.copy(alerts = updated))
        }
    }

    private fun load() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            delay(SIMULATED_DELAY_MS) // mimic a network fetch so the shimmer/loading state shows
            _uiState.update { it.copy(isLoading = false, dashboard = AcademicHeadMockData.dashboard) }
        }
    }

    private companion object {
        const val SIMULATED_DELAY_MS = 500L
    }
}

data class AcademicHeadDashboardUiState(
    val isLoading: Boolean = true,
    val dashboard: AcademicHeadDashboard? = null,
)
