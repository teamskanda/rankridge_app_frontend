package com.rankridge.app.ui.update

import android.app.Application
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rankridge.app.core.update.UpdateChecker
import com.rankridge.app.core.update.UpdateCheckResult
import com.rankridge.app.core.update.UpdateInstaller
import com.rankridge.app.core.update.UpdateManifest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Drives the in-app update flow: check once per launch, present the prompt, and on confirmation
 * download and install. Backend-free — the only remote thing is a public version.json (see
 * [com.rankridge.app.core.update.UpdateConfig]). Any failure is swallowed so the app is never
 * blocked by the update check.
 */
class UpdateViewModel(application: Application) : AndroidViewModel(application) {

    private val installer = UpdateInstaller(application)
    private val _uiState = MutableStateFlow(UpdateUiState())
    val uiState: StateFlow<UpdateUiState> = _uiState.asStateFlow()

    private var checked = false
    private var downloadId: Long = -1L
    private var pendingManifest: UpdateManifest? = null

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            if (id != downloadId) return
            if (installer.isDownloadSuccessful(id)) {
                installer.install()
                // The OS installer now takes over; collapse our own UI.
                _uiState.update { UpdateUiState() }
            } else {
                _uiState.update { it.copy(phase = UpdatePhase.ERROR) }
            }
        }
    }

    init {
        ContextCompat.registerReceiver(
            application,
            downloadReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            ContextCompat.RECEIVER_EXPORTED,
        )
    }

    /** Run the check a single time per process. Safe to call from a LaunchedEffect on every entry. */
    fun checkForUpdate() {
        if (checked) return
        checked = true
        viewModelScope.launch {
            when (val result = UpdateChecker.check(getApplication())) {
                is UpdateCheckResult.Available -> {
                    pendingManifest = result.manifest
                    _uiState.update {
                        it.copy(phase = UpdatePhase.AVAILABLE, manifest = result.manifest)
                    }
                }
                UpdateCheckResult.UpToDate,
                UpdateCheckResult.Unknown -> Unit // nothing to show
            }
        }
    }

    /** User tapped "Update now". */
    fun startUpdate() {
        val manifest = pendingManifest ?: return
        if (!installer.canInstall()) {
            _uiState.update { it.copy(phase = UpdatePhase.NEEDS_PERMISSION) }
            return
        }
        downloadId = installer.enqueue(manifest.apkUrl)
        _uiState.update { it.copy(phase = UpdatePhase.DOWNLOADING) }
    }

    /** User returned from the "install unknown apps" settings screen; try again. */
    fun onPermissionSettingsReturned() {
        if (_uiState.value.phase == UpdatePhase.NEEDS_PERMISSION) startUpdate()
    }

    fun openInstallPermissionSettings() = installer.requestInstallPermission()

    /** Dismiss the prompt for this session (not offered again until next launch). */
    fun dismiss() {
        if (pendingManifest?.mandatory == true) return // a mandatory update can't be skipped
        _uiState.update { UpdateUiState() }
    }

    override fun onCleared() {
        getApplication<Application>().unregisterReceiver(downloadReceiver)
        super.onCleared()
    }
}

enum class UpdatePhase { NONE, AVAILABLE, NEEDS_PERMISSION, DOWNLOADING, ERROR }

data class UpdateUiState(
    val phase: UpdatePhase = UpdatePhase.NONE,
    val manifest: UpdateManifest? = null,
)
