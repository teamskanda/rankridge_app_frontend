package com.rankridge.app.core.update

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Where the app looks to learn whether a newer build exists.
 *
 * IMPORTANT: this must be a PUBLICLY reachable raw JSON, with no auth. A shipped app cannot carry a
 * GitHub token safely, so a private repo will not work here. Keep the app's source in the private
 * repo, but host this one small file (and the APK it points at) in a PUBLIC place — the simplest is
 * a dedicated public "releases" repo, with version.json at its root and each APK attached to a
 * GitHub Release. Point [MANIFEST_URL] at that file's raw URL.
 */
object UpdateConfig {
    const val MANIFEST_URL =
        "https://raw.githubusercontent.com/teamskanda/rankridge-updates/main/version.json"
}

/**
 * The published description of the latest available build. Mirrors the fields in version.json.
 */
data class UpdateManifest(
    val versionCode: Long,
    val versionName: String,
    val apkUrl: String,
    val notes: List<String>,
    val mandatory: Boolean,
)

/** Outcome of a check. [Unknown] means we couldn't reach or parse the manifest — stay silent. */
sealed interface UpdateCheckResult {
    data class Available(val manifest: UpdateManifest) : UpdateCheckResult
    data object UpToDate : UpdateCheckResult
    data object Unknown : UpdateCheckResult
}

object UpdateChecker {

    /**
     * Fetch the manifest and compare it to the installed build. Any failure (offline, 404 because
     * hosting isn't set up yet, malformed JSON) returns [UpdateCheckResult.Unknown] so the app is
     * never blocked or crashed by the update check.
     */
    suspend fun check(context: Context): UpdateCheckResult = withContext(Dispatchers.IO) {
        runCatching {
            val manifest = parse(fetch(UpdateConfig.MANIFEST_URL))
            if (manifest.versionCode > installedVersionCode(context)) {
                UpdateCheckResult.Available(manifest)
            } else {
                UpdateCheckResult.UpToDate
            }
        }.getOrDefault(UpdateCheckResult.Unknown)
    }

    private fun parse(body: String): UpdateManifest {
        val json = JSONObject(body)
        val notesArray = json.optJSONArray("notes")
        val notes = buildList {
            if (notesArray != null) for (i in 0 until notesArray.length()) add(notesArray.getString(i))
        }
        return UpdateManifest(
            versionCode = json.getLong("versionCode"),
            versionName = json.getString("versionName"),
            apkUrl = json.getString("apkUrl"),
            notes = notes,
            mandatory = json.optBoolean("mandatory", false),
        )
    }

    private fun fetch(url: String): String {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 8_000
            readTimeout = 8_000
            requestMethod = "GET"
            setRequestProperty("Accept", "application/json")
        }
        try {
            if (connection.responseCode !in 200..299) error("HTTP ${connection.responseCode}")
            return connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    fun installedVersionCode(context: Context): Long {
        val info = context.packageManager.getPackageInfo(context.packageName, 0)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            info.longVersionCode
        } else {
            @Suppress("DEPRECATION") info.versionCode.toLong()
        }
    }
}
