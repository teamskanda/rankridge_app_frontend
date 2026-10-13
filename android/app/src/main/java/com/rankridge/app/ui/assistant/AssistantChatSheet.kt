package com.rankridge.app.ui.assistant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay

/**
 * The assistant conversation, presented as a tall modal bottom sheet so the student keeps the
 * dashboard behind it. Material guidance is that a FAB only launches the assistant; the
 * conversation itself lives in a surface like this, not the FAB.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantChatSheet(viewModel: AssistantViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val profile = viewModel.profile
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val listState = rememberLazyListState()

    // Keep the newest message (and the typing bubble) in view.
    LaunchedEffect(state.messages.size, state.isResponding) {
        val target = state.messages.size // typing bubble sits after the last message
        if (target > 0) listState.animateScrollToItem(target)
    }

    // Auto-dismiss transient errors so a one-off STT/permission hiccup doesn't linger.
    LaunchedEffect(state.error) {
        if (state.error != null) {
            delay(4000)
            viewModel.setError(null)
        }
    }

    ModalBottomSheet(
        onDismissRequest = viewModel::close,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.9f)
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding(),
        ) {
            Header(name = profile.assistantName, tagline = profile.tagline, onClose = viewModel::close)
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            state.error?.let { ErrorBanner(it) }

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(items = state.messages, key = { it.id }) { message ->
                    ChatBubble(message)
                }
                if (state.isResponding) {
                    item(key = "typing") { TypingBubble() }
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                    if (profile.quickReplies.isNotEmpty()) {
                        QuickReplyChips(
                            quickReplies = profile.quickReplies,
                            enabled = !state.isResponding,
                            onClick = viewModel::sendQuickReply,
                        )
                        Spacer(Modifier.size(10.dp))
                    }
                    AssistantComposer(
                        draft = state.draft,
                        hint = profile.inputHint,
                        isListening = state.isListening,
                        isResponding = state.isResponding,
                        pendingImageUri = state.pendingImageUri,
                        onDraftChange = viewModel::updateDraft,
                        onSend = viewModel::send,
                        onAttachImage = viewModel::attachImage,
                        onClearAttachment = viewModel::clearAttachment,
                        onTranscript = viewModel::applyTranscript,
                        onListeningChange = viewModel::setListening,
                        onError = viewModel::setError,
                    )
                }
            }
        }
    }
}

@Composable
private fun Header(name: String, tagline: String, onClose: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, top = 4.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AssistantAvatar(size = 40)
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.size(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text(
                        text = "AI",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                }
            }
            Text(
                text = tagline,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(onClick = onClose) {
            Icon(Icons.Filled.Close, contentDescription = "Close assistant")
        }
    }
}

@Composable
private fun ErrorBanner(message: String) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}
