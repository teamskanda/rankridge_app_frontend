package com.rankridge.app.ui.counsellor

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
 * Holds the Counsellor dashboard state for every tab. UI-only: it "loads" [CounsellorMockData]
 * after a short simulated delay, mirroring [com.rankridge.app.ui.mentor.MentorDashboardViewModel].
 * A counsellor's two core actions — logging a lead note and marking a lead contacted — are real,
 * local state mutations, since working the pipeline is the actual job of this screen.
 */
class CounsellorDashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CounsellorDashboardUiState())
    val uiState: StateFlow<CounsellorDashboardUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun refresh() = load()

    fun addNote(leadId: String, text: String) {
        if (text.isBlank()) return
        val today = SimpleDateFormat("d MMM", Locale.getDefault()).format(Date())
        val note = LeadNote(id = "n${System.currentTimeMillis()}", dateLabel = today, text = text.trim())
        _uiState.update { state ->
            val dashboard = state.dashboard ?: return@update state
            val existing = dashboard.notesByLead[leadId].orEmpty()
            val updatedNotes = dashboard.notesByLead + (leadId to (listOf(note) + existing))
            state.copy(dashboard = dashboard.copy(notesByLead = updatedNotes))
        }
    }

    fun markContacted(leadId: String) {
        _uiState.update { state ->
            val dashboard = state.dashboard ?: return@update state
            val updatedLeads = dashboard.leads.map { lead ->
                if (lead.id != leadId) return@map lead
                lead.copy(
                    status = if (lead.status == LeadStatus.NEW) LeadStatus.CONTACTED else lead.status,
                    lastContactedLabel = "Today",
                    nextFollowUpLabel = "In 3 days",
                )
            }
            state.copy(dashboard = dashboard.copy(leads = updatedLeads))
        }
    }

    private fun load() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            delay(SIMULATED_DELAY_MS) // mimic a network fetch so the shimmer/loading state shows
            _uiState.update { it.copy(isLoading = false, dashboard = CounsellorMockData.dashboard) }
        }
    }

    private companion object {
        const val SIMULATED_DELAY_MS = 500L
    }
}

data class CounsellorDashboardUiState(
    val isLoading: Boolean = true,
    val dashboard: CounsellorDashboard? = null,
)
