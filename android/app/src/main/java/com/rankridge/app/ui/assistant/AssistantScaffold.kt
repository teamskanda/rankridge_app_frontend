package com.rankridge.app.ui.assistant

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rankridge.app.ui.auth.UserRole

/**
 * Wraps a role's dashboard with the floating AI assistant: a branded FAB launcher docked above the
 * bottom navigation, and the chat bottom sheet it opens. One line wraps each dashboard, so there is
 * no per-screen duplication and the assistant behaves identically everywhere.
 *
 * Not used for Admin — that console is a web dashboard placeholder, so it has no assistant.
 */
@Composable
fun AssistantScaffold(
    role: UserRole,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val viewModel: AssistantViewModel = viewModel(
        key = "assistant_${role.name}",
        factory = AssistantViewModel.Factory(role),
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        content()

        AssistantFab(
            onClick = viewModel::open,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                // Clear the bottom navigation bar (approx 80dp) so the FAB never sits on a tab.
                .padding(end = 16.dp, bottom = 84.dp),
        )

        if (state.isOpen) {
            AssistantChatSheet(viewModel)
        }
    }
}

@Composable
private fun AssistantFab(onClick: () -> Unit, modifier: Modifier = Modifier) {
    // A slow, gentle halo pulse signals "I'm here" without the continuous motion that reads as a
    // laggy or broken animation.
    val transition = rememberInfiniteTransition(label = "fab-pulse")
    val scale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "halo-scale",
    )
    val haloAlpha by transition.animateFloat(
        initialValue = 0.28f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "halo-alpha",
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .scale(scale)
                .alpha(haloAlpha)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
        )
        FloatingActionButton(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape,
        ) {
            Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = "Ask $ASSISTANT_NAME",
            )
        }
    }
}
