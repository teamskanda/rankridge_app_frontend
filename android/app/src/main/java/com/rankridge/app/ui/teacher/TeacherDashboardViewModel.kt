package com.rankridge.app.ui.teacher

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
 * Holds the Teacher dashboard state for every tab. UI-only: it "loads" [TeacherMockData] after a
 * short simulated delay, mirroring [com.rankridge.app.ui.mentor.MentorDashboardViewModel]. Four
 * real, local state mutations back the screen's core actions — marking attendance, marking a
 * syllabus topic taught, resolving a doubt, and logging a remark — since those are what a teacher
 * actually does here, not just a read-only summary.
 */
class TeacherDashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TeacherDashboardUiState())
    val uiState: StateFlow<TeacherDashboardUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun refresh() = load()

    fun markAttendance(eventId: String) {
        _uiState.update { state ->
            val dashboard = state.dashboard ?: return@update state
            val updated = dashboard.schedule.map {
                if (it.id == eventId) it.copy(attendanceMarked = true) else it
            }
            state.copy(dashboard = dashboard.copy(schedule = updated))
        }
    }

    fun markSyllabusTaught(syllabusItemId: String) {
        _uiState.update { state ->
            val dashboard = state.dashboard ?: return@update state
            val updated = dashboard.syllabusItems.map {
                if (it.id == syllabusItemId) it.copy(status = SyllabusStatus.ON_TRACK) else it
            }
            state.copy(dashboard = dashboard.copy(syllabusItems = updated))
        }
    }

    fun resolveDoubt(doubtId: String) {
        _uiState.update { state ->
            val dashboard = state.dashboard ?: return@update state
            val updated = dashboard.doubts.map {
                if (it.id == doubtId) it.copy(status = DoubtStatus.RESOLVED) else it
            }
            state.copy(dashboard = dashboard.copy(doubts = updated))
        }
    }

    fun addRemark(studentId: String, text: String) {
        if (text.isBlank()) return
        val today = SimpleDateFormat("d MMM", Locale.getDefault()).format(Date())
        val remark = TeacherRemark(id = "r${System.currentTimeMillis()}", dateLabel = today, text = text.trim())
        _uiState.update { state ->
            val dashboard = state.dashboard ?: return@update state
            val existing = dashboard.remarksByStudent[studentId].orEmpty()
            val updatedRemarks = dashboard.remarksByStudent + (studentId to (listOf(remark) + existing))
            state.copy(dashboard = dashboard.copy(remarksByStudent = updatedRemarks))
        }
    }

    private fun load() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            delay(SIMULATED_DELAY_MS) // mimic a network fetch so the shimmer/loading state shows
            _uiState.update { it.copy(isLoading = false, dashboard = TeacherMockData.dashboard) }
        }
    }

    private companion object {
        const val SIMULATED_DELAY_MS = 500L
    }
}

data class TeacherDashboardUiState(
    val isLoading: Boolean = true,
    val dashboard: TeacherDashboard? = null,
)
