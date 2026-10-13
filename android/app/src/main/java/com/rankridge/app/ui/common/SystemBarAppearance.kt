package com.rankridge.app.ui.common

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Declares what the system status/navigation bar icons should look like for the screen currently
 * on top.
 *
 * The window is edge-to-edge, so those icons are drawn over whatever the screen puts underneath
 * them — and that differs by screen: the dashboards and the OTP/sign-up screens run the deep teal
 * Aqua Peacock chrome up under the status bar and need light icons, while sign-in has no bar at
 * all and shows the pale page background, which needs dark ones.
 *
 * Setting this once in the Activity cannot satisfy both, so each screen (in practice, each top-bar
 * component) states its own requirement and the last composition to run wins — which is the one on
 * screen.
 *
 * @param lightIcons true for white icons (dark background behind the bars).
 */
@Composable
fun SystemBarAppearance(lightIcons: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return
    val window = (view.context as? Activity)?.window ?: return
    SideEffect {
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = !lightIcons
            isAppearanceLightNavigationBars = !lightIcons
        }
    }
}
