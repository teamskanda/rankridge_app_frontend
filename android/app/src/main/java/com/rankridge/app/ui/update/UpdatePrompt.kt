package com.rankridge.app.ui.update

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SystemUpdate

/**
 * App-level gate for the in-app updater. Drop this once at the root (over the nav host): it checks
 * for a new build on launch and shows the update prompt over whatever screen is visible, so it
 * reaches every role. Shows nothing when up to date or when the check can't reach the manifest.
 */
@Composable
fun UpdateGate(viewModel: UpdateViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.checkForUpdate()
        // If we sent the user to grant "install unknown apps", retry on return.
        viewModel.onPermissionSettingsReturned()
    }

    when (state.phase) {
        UpdatePhase.AVAILABLE -> AvailableDialog(
            versionName = state.manifest?.versionName.orEmpty(),
            notes = state.manifest?.notes.orEmpty(),
            mandatory = state.manifest?.mandatory == true,
            onUpdate = viewModel::startUpdate,
            onLater = viewModel::dismiss,
        )
        UpdatePhase.NEEDS_PERMISSION -> NeedsPermissionDialog(
            onOpenSettings = viewModel::openInstallPermissionSettings,
            onCancel = viewModel::dismiss,
        )
        UpdatePhase.DOWNLOADING -> DownloadingDialog()
        UpdatePhase.ERROR -> ErrorDialog(onClose = viewModel::dismiss)
        UpdatePhase.NONE -> Unit
    }
}

@Composable
private fun AvailableDialog(
    versionName: String,
    notes: List<String>,
    mandatory: Boolean,
    onUpdate: () -> Unit,
    onLater: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { if (!mandatory) onLater() },
        icon = { Icon(Icons.Filled.SystemUpdate, contentDescription = null) },
        title = { Text("Update available") },
        text = {
            Column {
                Text(
                    text = if (versionName.isNotBlank()) {
                        "A new version ($versionName) of Rankridge is ready."
                    } else {
                        "A new version of Rankridge is ready."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                )
                if (notes.isNotEmpty()) {
                    Spacer(Modifier.size(12.dp))
                    Text("What's new", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.size(4.dp))
                    notes.forEach { note ->
                        Row {
                            Text("• ", style = MaterialTheme.typography.bodyMedium)
                            Text(note, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onUpdate) { Text("Update now") } },
        dismissButton = if (mandatory) null else {
            { TextButton(onClick = onLater) { Text("Later") } }
        },
    )
}

@Composable
private fun NeedsPermissionDialog(onOpenSettings: () -> Unit, onCancel: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Allow installs") },
        text = {
            Text(
                "To install the update, allow Rankridge to install apps from this source. " +
                    "You'll be taken to a settings screen to turn it on, then the update continues.",
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        confirmButton = { TextButton(onClick = onOpenSettings) { Text("Open settings") } },
        dismissButton = { TextButton(onClick = onCancel) { Text("Cancel") } },
    )
}

@Composable
private fun DownloadingDialog() {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Downloading update") },
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
                Spacer(Modifier.width(16.dp))
                Text(
                    "Downloading in the background. We'll open the installer when it's ready.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        },
        confirmButton = {},
    )
}

@Composable
private fun ErrorDialog(onClose: () -> Unit) {
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("Update failed") },
        text = {
            Text(
                "The update couldn't be downloaded. Please check your connection and try again later.",
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        confirmButton = { TextButton(onClick = onClose) { Text("Close") } },
    )
}
