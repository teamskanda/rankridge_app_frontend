package com.rankridge.app.ui.academichead

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.rankridge.app.ui.academichead.batches.AcademicHeadBatchesScreen
import com.rankridge.app.ui.academichead.batches.BatchDetailScreen
import com.rankridge.app.ui.academichead.faculty.AcademicHeadFacultyScreen
import com.rankridge.app.ui.academichead.faculty.FacultyDetailScreen
import com.rankridge.app.ui.academichead.home.AcademicHeadHomeScreen
import com.rankridge.app.ui.academichead.profile.AcademicHeadProfileScreen
import com.rankridge.app.ui.common.rankridgeEnterTransition
import com.rankridge.app.ui.common.rankridgeExitTransition
import com.rankridge.app.ui.common.rankridgePopEnterTransition
import com.rankridge.app.ui.common.rankridgePopExitTransition
import com.rankridge.app.ui.components.RankridgeHomeTopBar
import com.rankridge.app.ui.components.RankridgeTopBar
import com.rankridge.app.ui.theme.rankridgeBarColor
import com.rankridge.app.ui.theme.rankridgeBarContentColor
import kotlinx.coroutines.launch

/** The four Academic Head tabs: Home → Faculty → Batches → Profile — branch-level oversight
 * (daily ops, compliance, risk) rather than a single classroom or a single mentee's file. */
private enum class AcademicHeadTab(
    val route: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    HOME("head_tab_home", R.string.nav_home, Icons.Filled.Home, Icons.Outlined.Home),
    FACULTY("head_tab_faculty", R.string.nav_academichead_faculty, Icons.Filled.School, Icons.Outlined.School),
    BATCHES("head_tab_batches", R.string.nav_academichead_batches, Icons.Filled.Groups, Icons.Outlined.Groups),
    PROFILE("head_tab_profile", R.string.nav_profile, Icons.Filled.Person, Icons.Outlined.Person),
}

private const val FACULTY_DETAIL_ROUTE = "head_faculty_detail"
private const val ARG_FACULTY_ID = "facultyId"
private fun facultyDetailRoute(id: String) = "$FACULTY_DETAIL_ROUTE/$id"

private const val BATCH_DETAIL_ROUTE = "head_batch_detail"
private const val ARG_BATCH_ID = "batchId"
private fun batchDetailRoute(id: String) = "$BATCH_DETAIL_ROUTE/$id"

/**
 * Academic Head dashboard host: one shared [AcademicHeadDashboardViewModel] feeds all four tabs
 * plus the faculty- and batch-detail drill-downs, switched by the same Material 3 bottom
 * navigation chrome as the Student/Parent/Mentor/Teacher/Counsellor dashboards. [onSignOut]
 * bubbles back up to the app-level nav graph.
 */
@Composable
fun AcademicHeadDashboardScreen(
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AcademicHeadDashboardViewModel = viewModel(),
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
    val onFacultyDetail = currentDestination?.route?.startsWith(FACULTY_DETAIL_ROUTE) == true
    val onBatchDetail = currentDestination?.route?.startsWith(BATCH_DETAIL_ROUTE) == true
    val onDetailScreen = onFacultyDetail || onBatchDetail
    val currentTab = AcademicHeadTab.entries.firstOrNull { tab ->
        currentDestination?.hierarchy?.any { it.route == tab.route } == true
    } ?: AcademicHeadTab.HOME
    val dashboard = uiState.dashboard

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            when {
                onFacultyDetail -> {
                    val facultyName = dashboard?.faculty
                        ?.firstOrNull { it.id == navBackStackEntry?.arguments?.getString(ARG_FACULTY_ID) }
                        ?.name
                    RankridgeTopBar(
                        title = facultyName ?: stringResource(R.string.nav_academichead_faculty),
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                            }
                        },
                    )
                }
                onBatchDetail -> {
                    val batchName = dashboard?.batches
                        ?.firstOrNull { it.id == navBackStackEntry?.arguments?.getString(ARG_BATCH_ID) }
                        ?.name
                    RankridgeTopBar(
                        title = batchName ?: stringResource(R.string.nav_academichead_batches),
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                            }
                        },
                    )
                }
                currentTab == AcademicHeadTab.HOME && dashboard != null -> {
                    val head = dashboard.head
                    RankridgeHomeTopBar(
                        greeting = "Hi, ${head.name.substringBefore(" ")} 👋",
                        subtitle = head.branch,
                        initials = head.initials,
                        notificationCount = head.notifications,
                        onNotificationClick = { onAction("You have ${head.notifications} new notifications") },
                    )
                }
                else -> {
                    RankridgeTopBar(
                        title = stringResource(currentTab.labelRes),
                        icon = currentTab.selectedIcon,
                    )
                }
            }
        },
        bottomBar = {
            if (!onDetailScreen) {
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
                    AcademicHeadTab.entries.forEach { tab ->
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
            startDestination = AcademicHeadTab.HOME.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { rankridgeEnterTransition() },
            exitTransition = { rankridgeExitTransition() },
            popEnterTransition = { rankridgePopEnterTransition() },
            popExitTransition = { rankridgePopExitTransition() },
        ) {
            composable(AcademicHeadTab.HOME.route) {
                AcademicHeadHomeScreen(
                    dashboard = dashboard,
                    onOpenFaculty = { navController.navigateToTab(AcademicHeadTab.FACULTY) },
                    onOpenBatches = { navController.navigateToTab(AcademicHeadTab.BATCHES) },
                    onResolveAlert = { id -> viewModel.resolveAlert(id) },
                    onAction = onAction,
                )
            }
            composable(AcademicHeadTab.FACULTY.route) {
                AcademicHeadFacultyScreen(
                    dashboard = dashboard,
                    onOpenFaculty = { id -> navController.navigate(facultyDetailRoute(id)) },
                )
            }
            composable(AcademicHeadTab.BATCHES.route) {
                AcademicHeadBatchesScreen(
                    dashboard = dashboard,
                    onOpenBatch = { id -> navController.navigate(batchDetailRoute(id)) },
                )
            }
            composable(AcademicHeadTab.PROFILE.route) {
                AcademicHeadProfileScreen(dashboard = dashboard, onSignOut = onSignOut, onAction = onAction)
            }
            composable("$FACULTY_DETAIL_ROUTE/{$ARG_FACULTY_ID}") { backStackEntry ->
                val facultyId = backStackEntry.arguments?.getString(ARG_FACULTY_ID).orEmpty()
                val faculty = dashboard.faculty.firstOrNull { it.id == facultyId }
                if (faculty != null) {
                    FacultyDetailScreen(faculty = faculty)
                }
            }
            composable("$BATCH_DETAIL_ROUTE/{$ARG_BATCH_ID}") { backStackEntry ->
                val batchId = backStackEntry.arguments?.getString(ARG_BATCH_ID).orEmpty()
                val batch = dashboard.batches.firstOrNull { it.id == batchId }
                if (batch != null) {
                    BatchDetailScreen(batch = batch)
                }
            }
        }
    }
}

private fun androidx.navigation.NavHostController.navigateToTab(tab: AcademicHeadTab) {
    navigate(tab.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
