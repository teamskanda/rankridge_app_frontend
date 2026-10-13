package com.rankridge.app.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.MenuBook
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
import kotlinx.coroutines.launch
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rankridge.app.R
import com.rankridge.app.ui.common.rankridgeEnterTransition
import com.rankridge.app.ui.common.rankridgeExitTransition
import com.rankridge.app.ui.common.rankridgePopEnterTransition
import com.rankridge.app.ui.common.rankridgePopExitTransition
import com.rankridge.app.ui.components.RankridgeHomeTopBar
import com.rankridge.app.ui.components.RankridgeTopBar
import com.rankridge.app.ui.theme.rankridgeBarColor
import com.rankridge.app.ui.theme.rankridgeBarContentColor
import com.rankridge.app.ui.student.home.StudentHomeScreen
import com.rankridge.app.ui.student.learn.StudentLearnScreen
import com.rankridge.app.ui.student.performance.StudentPerformanceScreen
import com.rankridge.app.ui.student.profile.StudentProfileScreen
import com.rankridge.app.ui.student.tests.StudentTestsScreen

/** The five Student tabs, per the CLAUDE.md nav spec: Home → Learn → Tests → Performance → Profile. */
private enum class StudentTab(
    val route: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    HOME("student_home", R.string.nav_home, Icons.Filled.Home, Icons.Outlined.Home),
    LEARN("student_learn", R.string.nav_learn, Icons.AutoMirrored.Filled.MenuBook, Icons.AutoMirrored.Outlined.MenuBook),
    TESTS("student_tests", R.string.nav_tests, Icons.Filled.Assignment, Icons.Outlined.Assignment),
    PERFORMANCE("student_performance", R.string.nav_performance, Icons.Filled.Insights, Icons.Outlined.Insights),
    PROFILE("student_profile", R.string.nav_profile, Icons.Filled.Person, Icons.Outlined.Person),
}

/**
 * Student dashboard host: one shared [StudentDashboardViewModel] feeds all five tabs, switched by a
 * Material 3 bottom navigation bar. [onSignOut] bubbles back up to the app-level nav graph.
 */
@Composable
fun StudentDashboardScreen(
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StudentDashboardViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    // Every button/card without a real backend reports back through this, so nothing feels dead.
    val onAction: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message)
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentTab = StudentTab.entries.firstOrNull { tab ->
        currentDestination?.hierarchy?.any { it.route == tab.route } == true
    } ?: StudentTab.HOME
    val dashboard = uiState.dashboard

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (currentTab == StudentTab.HOME && dashboard != null) {
                val profile = dashboard.profile
                RankridgeHomeTopBar(
                    greeting = "Hi, ${profile.name.substringBefore(" ")} 👋",
                    subtitle = "${profile.classYear} · ${profile.batch} · ${profile.goal}",
                    initials = profile.initials,
                    notificationCount = profile.notifications,
                    onNotificationClick = { onAction("You have ${profile.notifications} new notifications") },
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
                // Same light aqua tint and rounded-shadow "floating card" treatment as the top
                // bar, mirrored onto the top edge here, for a consistent, airy brand chrome.
                containerColor = Color.Transparent,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .shadow(elevation = 12.dp, shape = barShape, clip = false)
                    .clip(barShape)
                    .background(rankridgeBarColor()),
            ) {
                StudentTab.entries.forEach { tab ->
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
                        label = { Text(stringResource(tab.labelRes)) },
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
        if (uiState.isLoading || dashboard == null) {
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
            startDestination = StudentTab.HOME.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { rankridgeEnterTransition() },
            exitTransition = { rankridgeExitTransition() },
            popEnterTransition = { rankridgePopEnterTransition() },
            popExitTransition = { rankridgePopExitTransition() },
        ) {
            composable(StudentTab.HOME.route) {
                StudentHomeScreen(
                    dashboard = dashboard,
                    onOpenTests = { navController.navigateToTab(StudentTab.TESTS) },
                    onOpenPerformance = { navController.navigateToTab(StudentTab.PERFORMANCE) },
                    onOpenLearn = { navController.navigateToTab(StudentTab.LEARN) },
                    onAction = onAction,
                )
            }
            composable(StudentTab.LEARN.route) {
                StudentLearnScreen(dashboard = dashboard, onAction = onAction)
            }
            composable(StudentTab.TESTS.route) {
                StudentTestsScreen(dashboard = dashboard, onAction = onAction)
            }
            composable(StudentTab.PERFORMANCE.route) {
                StudentPerformanceScreen(dashboard = dashboard, onAction = onAction)
            }
            composable(StudentTab.PROFILE.route) {
                StudentProfileScreen(dashboard = dashboard, onSignOut = onSignOut, onAction = onAction)
            }
        }
    }
}

private fun androidx.navigation.NavHostController.navigateToTab(tab: StudentTab) {
    navigate(tab.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
