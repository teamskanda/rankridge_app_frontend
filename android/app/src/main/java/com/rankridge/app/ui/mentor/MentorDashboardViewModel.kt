package com.rankridge.app.ui.mentor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Holds the Mentor dashboard state for every tab. UI-only: it "loads" [MentorMockData] after a
 * short simulated delay, mirroring [com.rankridge.app.ui.parent.ParentDashboardViewModel]. Unlike
 * the read-only Parent/Student dashboards, a mentor's two core actions — logging a note and
 * closing out a parent follow-up — are real, local state mutations here, since those are the
 * actual job of this screen rather than just a status summary.
 */
class MentorDashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MentorDashboardUiState())
    val uiState: StateFlow<MentorDashboardUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun refresh() = load()

    fun addNote(studentId: String, text: String) {
        if (text.isBlank()) return
        val today = SimpleDateFormat("d MMM", Locale.getDefault()).format(Date())
        val note = MentorNote(id = "n${System.currentTimeMillis()}", dateLabel = today, text = text.trim())
        _uiState.update { state ->
            val dashboard = state.dashboard ?: return@update state
            val existing = dashboard.notesByStudent[studentId].orEmpty()
            val updatedNotes = dashboard.notesByStudent + (studentId to (listOf(note) + existing))
            state.copy(dashboard = dashboard.copy(notesByStudent = updatedNotes))
        }
    }

    fun setFollowUpStatus(followUpId: String, status: FollowUpStatus) {
        _uiState.update { state ->
            val dashboard = state.dashboard ?: return@update state
            val updated = dashboard.followUps.map {
                if (it.id == followUpId) it.copy(status = status) else it
            }
            state.copy(dashboard = dashboard.copy(followUps = updated))
        }
    }

    private fun load() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            delay(SIMULATED_DELAY_MS) // mimic a network fetch so the shimmer/loading state shows
            _uiState.update { it.copy(isLoading = false, dashboard = MentorMockData.dashboard) }
        }
    }

    private companion object {
        const val SIMULATED_DELAY_MS = 500L
    }
}

data class MentorDashboardUiState(
    val isLoading: Boolean = true,
    val dashboard: MentorDashboard? = null,
)
