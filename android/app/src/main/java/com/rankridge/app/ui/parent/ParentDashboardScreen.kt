package com.rankridge.app.ui.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rankridge.app.R
import com.rankridge.app.ui.common.AutoShrinkText
import com.rankridge.app.ui.common.rankridgeEnterTransition
import com.rankridge.app.ui.common.rankridgeExitTransition
import com.rankridge.app.ui.common.rankridgePopEnterTransition
import com.rankridge.app.ui.common.rankridgePopExitTransition
import com.rankridge.app.ui.components.RankridgeHomeTopBar
import com.rankridge.app.ui.components.RankridgeTopBar
import com.rankridge.app.ui.parent.attendance.ParentAttendanceScreen
import com.rankridge.app.ui.parent.home.ParentHomeScreen
import com.rankridge.app.ui.parent.profile.ParentProfileScreen
import com.rankridge.app.ui.parent.progress.ParentProgressScreen
import com.rankridge.app.ui.parent.updates.ParentUpdatesScreen
import com.rankridge.app.ui.theme.rankridgeBarColor
import com.rankridge.app.ui.theme.rankridgeBarContentColor
import kotlinx.coroutines.launch

/** The five Parent tabs, per the CLAUDE.md nav spec: Home → Progress → Attendance → Updates → Profile. */
private enum class ParentTab(
    val route: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    HOME("parent_tab_home", R.string.nav_home, Icons.Filled.Home, Icons.Outlined.Home),
    PROGRESS("parent_tab_progress", R.string.nav_parent_progress, Icons.Filled.Insights, Icons.Outlined.Insights),
    ATTENDANCE("parent_tab_attendance", R.string.nav_parent_attendance, Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    UPDATES("parent_tab_updates", R.string.nav_parent_updates, Icons.Filled.Campaign, Icons.Outlined.Campaign),
    PROFILE("parent_tab_profile", R.string.nav_profile, Icons.Filled.Person, Icons.Outlined.Person),
}

/**
 * Parent dashboard host: one shared [ParentDashboardViewModel] feeds all five tabs, switched by a
 * Material 3 bottom navigation bar — the same chrome pattern as the Student dashboard, so the two
 * roles feel like one consistent product. [onSignOut] bubbles back up to the app-level nav graph.
 */
@Composable
fun ParentDashboardScreen(
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ParentDashboardViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val onAction: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message)
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentTab = ParentTab.entries.firstOrNull { tab ->
        currentDestination?.hierarchy?.any { it.route == tab.route } == true
    } ?: ParentTab.HOME
    val dashboard = uiState.dashboard
    val snapshot = uiState.selectedSnapshot

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (currentTab == ParentTab.HOME && dashboard != null) {
                val parent = dashboard.parent
                RankridgeHomeTopBar(
                    greeting = "Hi, ${parent.name.substringBefore(" ")} 👋",
                    subtitle = "Here's how your family is doing",
                    initials = parent.initials,
                    notificationCount = parent.notifications,
                    onNotificationClick = { onAction("You have ${parent.notifications} new notifications") },
                )
            } else {
                RankridgeTopBar(
                    title = stringResource(currentTab.labelRes),
                    icon = currentTab.selectedIcon,
                )
            }
        },
        bottomBar = {
            val onPrimary = rankridgeBarContentColor
            val barShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            NavigationBar(
                containerColor = Color.Transparent,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .shadow(elevation = 12.dp, shape = barShape, clip = false)
                    .clip(barShape)
                    .background(rankridgeBarColor()),
            ) {
                ParentTab.entries.forEach { tab ->
                    val selected = tab == currentTab
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = null,
                            )
                        },
                        label = {
                                // A five-tab bar on a 360dp phone gives each label ~72dp, which
                                // is narrower than "Attendance" or "Schedule". Shrinking keeps
                                // the whole word readable where ellipsising would cut it off.
                                AutoShrinkText(
                                    text = stringResource(tab.labelRes),
                                    style = MaterialTheme.typography.labelMedium,
                                    minScale = 0.75f,
                                )
                            },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = onPrimary,
                            selectedTextColor = onPrimary,
                            // 0.82 rather than 0.65: on the deepened #007480 bar, 0.65 white
                            // falls to ~2.9:1 and unselected tabs read as disabled.
                            indicatorColor = onPrimary.copy(alpha = 0.26f),
                            unselectedIconColor = onPrimary.copy(alpha = 0.82f),
                            unselectedTextColor = onPrimary.copy(alpha = 0.82f),
                        ),
                    )
                }
            }
        },
    ) { innerPadding ->
        if (uiState.isLoading || dashboard == null || snapshot == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        NavHost(
            navController = navController,
            startDestination = ParentTab.HOME.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { rankridgeEnterTransition() },
            exitTransition = { rankridgeExitTransition() },
            popEnterTransition = { rankridgePopEnterTransition() },
            popExitTransition = { rankridgePopExitTransition() },
        ) {
            composable(ParentTab.HOME.route) {
                ParentHomeScreen(
                    dashboard = dashboard,
                    snapshot = snapshot,
                    selectedChildId = uiState.selectedChildId.orEmpty(),
                    onSelectChild = viewModel::onSelectChild,
                    onOpenProgress = { navController.navigateToTab(ParentTab.PROGRESS) },
                    onOpenAttendance = { navController.navigateToTab(ParentTab.ATTENDANCE) },
                    onOpenUpdates = { navController.navigateToTab(ParentTab.UPDATES) },
                    onAction = onAction,
                )
            }
            composable(ParentTab.PROGRESS.route) {
                ParentProgressScreen(snapshot = snapshot, onAction = onAction)
            }
            composable(ParentTab.ATTENDANCE.route) {
                ParentAttendanceScreen(snapshot = snapshot, onAction = onAction)
            }
            composable(ParentTab.UPDATES.route) {
                ParentUpdatesScreen(snapshot = snapshot, onAction = onAction)
            }
            composable(ParentTab.PROFILE.route) {
                ParentProfileScreen(
                    dashboard = dashboard,
                    onSignOut = onSignOut,
                    onAction = onAction,
                )
            }
        }
    }
}

private fun androidx.navigation.NavHostController.navigateToTab(tab: ParentTab) {
    navigate(tab.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
