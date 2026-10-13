package com.rankridge.app.ui.parent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Holds the Parent dashboard state for every tab. UI-only: it "loads" [ParentMockData] after a
 * short simulated delay, exactly mirroring [com.rankridge.app.ui.student.StudentDashboardViewModel].
 * The one piece of state unique to this role is [ParentDashboardUiState.selectedChildId] — every
 * tab reads [ParentDashboardUiState.selectedSnapshot], so switching the child selector on Home
 * instantly re-scopes every other tab to that child.
 */
class ParentDashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ParentDashboardUiState())
    val uiState: StateFlow<ParentDashboardUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun refresh() = load()

    fun onSelectChild(childId: String) {
        _uiState.update { it.copy(selectedChildId = childId) }
    }

    private fun load() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            delay(SIMULATED_DELAY_MS) // mimic a network fetch so the shimmer/loading state shows
            val dashboard = ParentMockData.dashboard
            _uiState.update {
                it.copy(
                    isLoading = false,
                    dashboard = dashboard,
                    selectedChildId = dashboard.children.first().id,
                )
            }
        }
    }

    private companion object {
        const val SIMULATED_DELAY_MS = 500L
    }
}

data class ParentDashboardUiState(
    val isLoading: Boolean = true,
    val dashboard: ParentDashboard? = null,
    val selectedChildId: String? = null,
) {
    val selectedChild: ChildProfile?
        get() = dashboard?.children?.firstOrNull { it.id == selectedChildId }

    val selectedSnapshot: ChildSnapshot?
        get() = dashboard?.childData?.get(selectedChildId)
}
