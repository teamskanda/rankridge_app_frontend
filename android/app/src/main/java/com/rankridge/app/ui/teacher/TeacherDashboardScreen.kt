package com.rankridge.app.ui.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
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
import com.rankridge.app.ui.common.rankridgeEnterTransition
import com.rankridge.app.ui.common.rankridgeExitTransition
import com.rankridge.app.ui.common.rankridgePopEnterTransition
import com.rankridge.app.ui.common.rankridgePopExitTransition
import com.rankridge.app.ui.components.RankridgeHomeTopBar
import com.rankridge.app.ui.components.RankridgeTopBar
import com.rankridge.app.ui.teacher.batches.TeacherBatchDetailScreen
import com.rankridge.app.ui.teacher.batches.TeacherBatchesScreen
import com.rankridge.app.ui.teacher.home.TeacherHomeScreen
import com.rankridge.app.ui.teacher.profile.TeacherProfileScreen
import com.rankridge.app.ui.teacher.schedule.TeacherScheduleScreen
import com.rankridge.app.ui.teacher.students.TeacherStudentDetailScreen
import com.rankridge.app.ui.teacher.students.TeacherStudentsScreen
import com.rankridge.app.ui.theme.rankridgeBarColor
import com.rankridge.app.ui.theme.rankridgeBarContentColor
import kotlinx.coroutines.launch

/** The five Teacher tabs, per the CLAUDE.md nav spec: Home → Batches → Schedule → Students → Profile. */
private enum class TeacherTab(
    val route: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    HOME("teacher_tab_home", R.string.nav_home, Icons.Filled.Home, Icons.Outlined.Home),
    BATCHES("teacher_tab_batches", R.string.nav_teacher_batches, Icons.AutoMirrored.Filled.MenuBook, Icons.AutoMirrored.Outlined.MenuBook),
    SCHEDULE("teacher_tab_schedule", R.string.nav_teacher_schedule, Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    STUDENTS("teacher_tab_students", R.string.nav_teacher_students, Icons.Filled.Groups, Icons.Outlined.Groups),
    PROFILE("teacher_tab_profile", R.string.nav_profile, Icons.Filled.Person, Icons.Outlined.Person),
}

private const val BATCH_DETAIL_ROUTE = "teacher_batch_detail"
private const val STUDENT_DETAIL_ROUTE = "teacher_student_detail"
private const val ARG_BATCH_ID = "batchId"
private const val ARG_STUDENT_ID = "studentId"
private fun batchDetailRoute(batchId: String) = "$BATCH_DETAIL_ROUTE/$batchId"
private fun studentDetailRoute(studentId: String) = "$STUDENT_DETAIL_ROUTE/$studentId"

/**
 * Teacher dashboard host: one shared [TeacherDashboardViewModel] feeds all five tabs plus the
 * batch and student drill-downs, switched by the same Material 3 bottom navigation chrome as the
 * Student/Parent/Mentor dashboards. [onSignOut] bubbles back up to the app-level nav graph.
 */
@Composable
fun TeacherDashboardScreen(
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TeacherDashboardViewModel = viewModel(),
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
    val currentRoute = currentDestination?.route
    val onBatchDetail = currentRoute?.startsWith(BATCH_DETAIL_ROUTE) == true
    val onStudentDetail = currentRoute?.startsWith(STUDENT_DETAIL_ROUTE) == true
    val onDetailScreen = onBatchDetail || onStudentDetail
    val currentTab = TeacherTab.entries.firstOrNull { tab ->
        currentDestination?.hierarchy?.any { it.route == tab.route } == true
    } ?: TeacherTab.HOME
    val dashboard = uiState.dashboard

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            when {
                onBatchDetail -> {
                    val batchName = dashboard?.batches
                        ?.firstOrNull { it.id == navBackStackEntry?.arguments?.getString(ARG_BATCH_ID) }
                        ?.name
                    RankridgeTopBar(
                        title = batchName ?: stringResource(R.string.nav_teacher_batches),
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                            }
                        },
                    )
                }
                onStudentDetail -> {
                    val studentName = dashboard?.students
                        ?.firstOrNull { it.id == navBackStackEntry?.arguments?.getString(ARG_STUDENT_ID) }
                        ?.name
                    RankridgeTopBar(
                        title = studentName ?: stringResource(R.string.nav_teacher_students),
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                            }
                        },
                    )
                }
                currentTab == TeacherTab.HOME && dashboard != null -> {
                    val teacher = dashboard.teacher
                    RankridgeHomeTopBar(
                        greeting = "Hi, ${teacher.name.substringAfter(". ").substringBefore(" ")} 👋",
                        subtitle = "${teacher.subject} · ${dashboard.batches.size} batches",
                        initials = teacher.initials,
                        notificationCount = teacher.notifications,
                        onNotificationClick = { onAction("You have ${teacher.notifications} new notifications") },
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
                    TeacherTab.entries.forEach { tab ->
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
            startDestination = TeacherTab.HOME.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { rankridgeEnterTransition() },
            exitTransition = { rankridgeExitTransition() },
            popEnterTransition = { rankridgePopEnterTransition() },
            popExitTransition = { rankridgePopExitTransition() },
        ) {
            composable(TeacherTab.HOME.route) {
                TeacherHomeScreen(
                    dashboard = dashboard,
                    onOpenStudent = { id -> navController.navigate(studentDetailRoute(id)) },
                    onOpenStudents = { navController.navigateToTab(TeacherTab.STUDENTS) },
                    onOpenSchedule = { navController.navigateToTab(TeacherTab.SCHEDULE) },
                    onMarkAttendance = { id -> viewModel.markAttendance(id) },
                    onResolveDoubt = { id -> viewModel.resolveDoubt(id) },
                    onAction = onAction,
                )
            }
            composable(TeacherTab.BATCHES.route) {
                TeacherBatchesScreen(
                    dashboard = dashboard,
                    onOpenBatch = { id -> navController.navigate(batchDetailRoute(id)) },
                )
            }
            composable(TeacherTab.SCHEDULE.route) {
                TeacherScheduleScreen(
                    dashboard = dashboard,
                    onMarkAttendance = { id -> viewModel.markAttendance(id) },
                    onAction = onAction,
                )
            }
            composable(TeacherTab.STUDENTS.route) {
                TeacherStudentsScreen(
                    dashboard = dashboard,
                    onOpenStudent = { id -> navController.navigate(studentDetailRoute(id)) },
                )
            }
            composable(TeacherTab.PROFILE.route) {
                TeacherProfileScreen(dashboard = dashboard, onSignOut = onSignOut, onAction = onAction)
            }
            composable("$BATCH_DETAIL_ROUTE/{$ARG_BATCH_ID}") { backStackEntry ->
                val batchId = backStackEntry.arguments?.getString(ARG_BATCH_ID).orEmpty()
                val batch = dashboard.batches.firstOrNull { it.id == batchId }
                if (batch != null) {
                    TeacherBatchDetailScreen(
                        batch = batch,
                        syllabusItems = dashboard.syllabusItems.filter { it.batchId == batchId },
                        tests = dashboard.tests.filter { it.batchId == batchId },
                        assignments = dashboard.assignments.filter { it.batchId == batchId },
                        onMarkSyllabusTaught = { id -> viewModel.markSyllabusTaught(id) },
                        onAction = onAction,
                    )
                }
            }
            composable("$STUDENT_DETAIL_ROUTE/{$ARG_STUDENT_ID}") { backStackEntry ->
                val studentId = backStackEntry.arguments?.getString(ARG_STUDENT_ID).orEmpty()
                val student = dashboard.students.firstOrNull { it.id == studentId }
                if (student != null) {
                    TeacherStudentDetailScreen(
                        student = student,
                        remarks = dashboard.remarksByStudent[studentId].orEmpty(),
                        onAddRemark = { text -> viewModel.addRemark(studentId, text) },
                        onAction = onAction,
                    )
                }
            }
        }
    }
}

private fun androidx.navigation.NavHostController.navigateToTab(tab: TeacherTab) {
    navigate(tab.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
