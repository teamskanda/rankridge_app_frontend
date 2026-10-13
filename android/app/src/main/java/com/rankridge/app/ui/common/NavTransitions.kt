package com.rankridge.app.ui.common

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.navigation.NavBackStackEntry

/**
 * Shared "fade through" navigation motion used by every dashboard's NavHost (bottom-tab switches
 * and detail push/pop alike). A short fade plus a small vertical nudge reads as a deliberate,
 * professional transition without the directional slide that would look wrong for bottom-nav tabs,
 * which aren't spatially ordered the way a push/pop stack is.
 */
private const val NAV_TRANSITION_MS = 220

val rankridgeEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    fadeIn(tween(NAV_TRANSITION_MS)) + slideInVertically(tween(NAV_TRANSITION_MS)) { height -> height / 24 }
}

val rankridgeExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    fadeOut(tween(NAV_TRANSITION_MS))
}

val rankridgePopEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    fadeIn(tween(NAV_TRANSITION_MS)) + slideInVertically(tween(NAV_TRANSITION_MS)) { height -> -height / 24 }
}

val rankridgePopExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    fadeOut(tween(NAV_TRANSITION_MS))
}
