package com.rankridge.app.ui.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Holds the Student dashboard state for every tab. UI-only: it "loads" [StudentMockData] after a
 * short simulated delay so the loading state is exercised, exactly mirroring the auth view models'
 * pattern. Swap the body of [load] for a repository call when the backend lands — the rest of the
 * UI is already written against [StudentDashboard].
 */
class StudentDashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(StudentDashboardUiState())
    val uiState: StateFlow<StudentDashboardUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun refresh() = load()

    private fun load() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            delay(SIMULATED_DELAY_MS) // mimic a network fetch so the shimmer/loading state shows
            _uiState.update {
                it.copy(isLoading = false, dashboard = StudentMockData.dashboard)
            }
        }
    }

    private companion object {
        const val SIMULATED_DELAY_MS = 500L
    }
}

data class StudentDashboardUiState(
    val isLoading: Boolean = true,
    val dashboard: StudentDashboard? = null,
)
