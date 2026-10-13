package com.rankridge.app.core.update

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.content.FileProvider
import java.io.File

/**
 * Downloads the new APK with the system [DownloadManager] (which shows its own progress
 * notification) and then hands it to the OS package installer. Android never silently installs a
 * sideloaded APK, so the user confirms the install once — that is a platform rule, not a gap here.
 */
class UpdateInstaller(private val context: Context) {

    private val downloadManager: DownloadManager
        get() = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    /** From Android 8, installing an APK needs the per-app "install unknown apps" permission. */
    fun canInstall(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.O ||
            context.packageManager.canRequestPackageInstalls()

    /** Send the user to the system screen where they grant this app permission to install apps. */
    fun requestInstallPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val intent = Intent(
            Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
            Uri.parse("package:${context.packageName}"),
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /** Queue the download; returns the DownloadManager id to watch for completion. */
    fun enqueue(apkUrl: String): Long {
        targetFile().takeIf { it.exists() }?.delete()
        val request = DownloadManager.Request(Uri.parse(apkUrl))
            .setTitle("Rankridge update")
            .setDescription("Downloading the new version")
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, FILE_NAME)
            .setMimeType(MIME_TYPE)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        return downloadManager.enqueue(request)
    }

    /** True when the given download finished successfully. */
    fun isDownloadSuccessful(downloadId: Long): Boolean {
        downloadManager.query(DownloadManager.Query().setFilterById(downloadId)).use { cursor ->
            if (!cursor.moveToFirst()) return false
            val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
            return status == DownloadManager.STATUS_SUCCESSFUL
        }
    }

    /** Launch the OS installer for the downloaded APK. */
    fun install() {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            targetFile(),
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, MIME_TYPE)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    private fun targetFile(): File =
        File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), FILE_NAME)

    private companion object {
        const val FILE_NAME = "rankridge-update.apk"
        const val MIME_TYPE = "application/vnd.android.package-archive"
    }
}
