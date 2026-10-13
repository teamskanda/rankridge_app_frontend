package com.rankridge.app.ui.assistant

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

/**
 * The chat composer: a pill text field with a live-editable voice transcript, an attach menu
 * (camera / gallery) and a send button. Voice and image capture use real OS features — Android's
 * [SpeechRecognizer] and the system photo picker / camera — so they work with no backend; only the
 * assistant's "analysis" of what comes back is scripted.
 */
@Composable
fun AssistantComposer(
    draft: String,
    hint: String,
    isListening: Boolean,
    isResponding: Boolean,
    pendingImageUri: String?,
    onDraftChange: (String) -> Unit,
    onSend: () -> Unit,
    onAttachImage: (String) -> Unit,
    onClearAttachment: () -> Unit,
    onTranscript: (String) -> Unit,
    onListeningChange: (Boolean) -> Unit,
    onError: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val voice = rememberVoiceInputController(
        onPartial = onTranscript,
        onFinal = onTranscript,
        onStateChange = onListeningChange,
        onError = onError,
    )

    val micPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) voice.start() else onError("Microphone permission is needed for voice input.")
    }

    val galleryPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri -> uri?.let { onAttachImage(it.toString()) } }

    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    val camera = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture(),
    ) { success ->
        if (success) pendingCameraUri?.let { onAttachImage(it.toString()) }
    }
    val cameraPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            val uri = newCameraUri(context)
            pendingCameraUri = uri
            camera.launch(uri)
        } else {
            onError("Camera permission is needed to take a photo.")
        }
    }

    fun toggleVoice() {
        if (isListening) {
            voice.stop()
            return
        }
        if (!voice.isAvailable()) {
            onError("Voice input isn't available on this device.")
            return
        }
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.RECORD_AUDIO,
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) voice.start() else micPermission.launch(Manifest.permission.RECORD_AUDIO)
    }

    fun launchCamera() {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA,
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) {
            val uri = newCameraUri(context)
            pendingCameraUri = uri
            camera.launch(uri)
        } else {
            cameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        if (pendingImageUri != null) {
            AttachmentPreview(uri = pendingImageUri, onRemove = onClearAttachment)
            Spacer(Modifier.size(8.dp))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            var attachMenuOpen by remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = { attachMenuOpen = true }) {
                    Icon(
                        imageVector = Icons.Filled.PhotoCamera,
                        contentDescription = "Attach a photo",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                DropdownMenu(
                    expanded = attachMenuOpen,
                    onDismissRequest = { attachMenuOpen = false },
                ) {
                    DropdownMenuItem(
                        text = { Text("Take photo") },
                        leadingIcon = { Icon(Icons.Filled.PhotoCamera, contentDescription = null) },
                        onClick = {
                            attachMenuOpen = false
                            launchCamera()
                        },
                    )
                    DropdownMenuItem(
                        text = { Text("Choose from gallery") },
                        leadingIcon = { Icon(Icons.Filled.PhotoLibrary, contentDescription = null) },
                        onClick = {
                            attachMenuOpen = false
                            galleryPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                            )
                        },
                    )
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.weight(1f),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.weight(1f).padding(horizontal = 16.dp, vertical = 12.dp)) {
                        if (draft.isEmpty()) {
                            Text(
                                text = if (isListening) "Listening..." else hint,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        BasicTextField(
                            value = draft,
                            onValueChange = onDraftChange,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            maxLines = 5,
                            modifier = Modifier.fillMaxWidth().heightIn(max = 120.dp),
                        )
                    }
                    IconButton(onClick = ::toggleVoice) {
                        Icon(
                            imageVector = if (isListening) Icons.Filled.Stop else Icons.Filled.Mic,
                            contentDescription = if (isListening) "Stop listening" else "Speak",
                            tint = if (isListening) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            Spacer(Modifier.width(8.dp))

            val canSend = (draft.isNotBlank() || pendingImageUri != null) && !isResponding
            Surface(
                onClick = { if (canSend) onSend() },
                enabled = canSend,
                shape = CircleShape,
                color = if (canSend) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceContainerHighest,
                modifier = Modifier.size(48.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (canSend) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun AttachmentPreview(uri: String, onRemove: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            AttachedImage(
                uri = uri,
                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(10.dp)),
            )
            Text(
                text = "Photo attached",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                contentAlignment = Alignment.Center,
            ) {
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Remove attachment",
                        tint = LocalContentColor.current,
                    )
                }
            }
        }
    }
}

/** A fresh FileProvider URI in the cache dir for a camera capture. */
private fun newCameraUri(context: android.content.Context): Uri {
    val images = File(context.cacheDir, "assistant_images").apply { mkdirs() }
    val file = File(images, "capture_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}
