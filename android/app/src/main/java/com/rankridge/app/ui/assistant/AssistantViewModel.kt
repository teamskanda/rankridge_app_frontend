package com.rankridge.app.ui.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rankridge.app.ui.auth.UserRole
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * State for the floating assistant, scoped to one role's dashboard. UI-only: replies come from
 * [MockAssistantEngine] after a short "thinking" delay so the conversation feels alive without any
 * backend. The whole UI is written against this state, so swapping in a real streaming client later
 * is a change to [send] alone.
 */
class AssistantViewModel(private val role: UserRole) : ViewModel() {

    val profile: AssistantProfile = assistantProfileFor(role)

    private val _uiState = MutableStateFlow(
        AssistantUiState(
            messages = listOf(
                ChatMessage(id = nextId(), author = ChatAuthor.ASSISTANT, text = profile.greeting),
            ),
        ),
    )
    val uiState: StateFlow<AssistantUiState> = _uiState.asStateFlow()

    fun open() = _uiState.update { it.copy(isOpen = true) }

    fun close() = _uiState.update { it.copy(isOpen = false, isListening = false) }

    fun updateDraft(text: String) = _uiState.update { it.copy(draft = text) }

    fun attachImage(uri: String) = _uiState.update { it.copy(pendingImageUri = uri) }

    fun clearAttachment() = _uiState.update { it.copy(pendingImageUri = null) }

    fun setListening(listening: Boolean) = _uiState.update { it.copy(isListening = listening) }

    /** Live transcript from voice: replace the draft so the user sees words appear as they speak. */
    fun applyTranscript(text: String) = _uiState.update { it.copy(draft = text) }

    fun setError(message: String?) = _uiState.update { it.copy(error = message) }

    /** Send whatever is in the composer (text and/or an attached image). */
    fun send() {
        val current = _uiState.value
        val text = current.draft.trim()
        val image = current.pendingImageUri
        if (text.isEmpty() && image == null) return
        dispatch(userText = text, image = image, engineKey = text)
    }

    /** Send a tapped suggestion chip. Its label shows as the user's message; its id drives the reply. */
    fun sendQuickReply(quickReply: QuickReply) {
        if (_uiState.value.isResponding) return
        dispatch(userText = quickReply.label, image = null, engineKey = quickReply.id)
    }

    private fun dispatch(userText: String, image: String?, engineKey: String) {
        val userMessage = ChatMessage(
            id = nextId(),
            author = ChatAuthor.USER,
            text = userText,
            imageUri = image,
        )
        _uiState.update {
            it.copy(
                messages = it.messages + userMessage,
                draft = "",
                pendingImageUri = null,
                isResponding = true,
                isListening = false,
            )
        }
        viewModelScope.launch {
            // A visible "thinking" pause: without it, an instant canned reply reads as fake.
            delay(THINKING_MIN_MS + Random.nextLong(THINKING_JITTER_MS))
            val answer = MockAssistantEngine.reply(role, engineKey, hasImage = image != null)
            val reply = ChatMessage(id = nextId(), author = ChatAuthor.ASSISTANT, text = answer)
            _uiState.update {
                it.copy(messages = it.messages + reply, isResponding = false)
            }
        }
    }

    private companion object {
        const val THINKING_MIN_MS = 600L
        const val THINKING_JITTER_MS = 700L
        private var counter = 0L
        fun nextId(): Long = counter++
    }

    class Factory(private val role: UserRole) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AssistantViewModel(role) as T
    }
}

data class AssistantUiState(
    val isOpen: Boolean = false,
    val messages: List<ChatMessage> = emptyList(),
    val draft: String = "",
    val isResponding: Boolean = false,
    val pendingImageUri: String? = null,
    val isListening: Boolean = false,
    val error: String? = null,
)
