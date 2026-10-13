package com.rankridge.app.ui.mentor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.EventNote
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
import com.rankridge.app.ui.common.AutoShrinkText
import com.rankridge.app.ui.common.rankridgeEnterTransition
import com.rankridge.app.ui.common.rankridgeExitTransition
import com.rankridge.app.ui.common.rankridgePopEnterTransition
import com.rankridge.app.ui.common.rankridgePopExitTransition
import com.rankridge.app.ui.components.RankridgeHomeTopBar
import com.rankridge.app.ui.components.RankridgeTopBar
import com.rankridge.app.ui.mentor.followups.MentorFollowUpsScreen
import com.rankridge.app.ui.mentor.home.MentorHomeScreen
import com.rankridge.app.ui.mentor.profile.MentorProfileScreen
import com.rankridge.app.ui.mentor.students.MentorStudentDetailScreen
import com.rankridge.app.ui.mentor.students.MentorStudentsScreen
import com.rankridge.app.ui.theme.rankridgeBarColor
import com.rankridge.app.ui.theme.rankridgeBarContentColor
import kotlinx.coroutines.launch

/** The four Mentor tabs: Home → Students → Follow-ups → Profile — deliberately simpler than the
 * Teacher dashboard, since a mentor triages a small fixed roster rather than running batches. */
private enum class MentorTab(
    val route: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    HOME("mentor_tab_home", R.string.nav_home, Icons.Filled.Home, Icons.Outlined.Home),
    STUDENTS("mentor_tab_students", R.string.nav_mentor_students, Icons.Filled.Groups, Icons.Outlined.Groups),
    FOLLOWUPS("mentor_tab_followups", R.string.nav_mentor_followups, Icons.Filled.EventNote, Icons.Outlined.EventNote),
    PROFILE("mentor_tab_profile", R.string.nav_profile, Icons.Filled.Person, Icons.Outlined.Person),
}

private const val STUDENT_DETAIL_ROUTE = "mentor_student_detail"
private const val ARG_STUDENT_ID = "studentId"
private fun studentDetailRoute(studentId: String) = "$STUDENT_DETAIL_ROUTE/$studentId"

/**
 * Mentor dashboard host: one shared [MentorDashboardViewModel] feeds all four tabs plus the
 * student-detail drill-down, switched by the same Material 3 bottom navigation chrome as the
 * Student/Parent dashboards. [onSignOut] bubbles back up to the app-level nav graph.
 */
@Composable
fun MentorDashboardScreen(
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MentorDashboardViewModel = viewModel(),
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
    val onDetailScreen = currentDestination?.route?.startsWith(STUDENT_DETAIL_ROUTE) == true
    val currentTab = MentorTab.entries.firstOrNull { tab ->
        currentDestination?.hierarchy?.any { it.route == tab.route } == true
    } ?: MentorTab.HOME
    val dashboard = uiState.dashboard

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            when {
                onDetailScreen -> {
                    val studentName = dashboard?.mentees
                        ?.firstOrNull { it.id == navBackStackEntry?.arguments?.getString(ARG_STUDENT_ID) }
                        ?.name
                    RankridgeTopBar(
                        title = studentName ?: stringResource(R.string.nav_mentor_students),
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                            }
                        },
                    )
                }
                currentTab == MentorTab.HOME && dashboard != null -> {
                    val mentor = dashboard.mentor
                    RankridgeHomeTopBar(
                        greeting = "Hi, ${mentor.name.substringBefore(" ")} 👋",
                        subtitle = "${dashboard.mentees.size} students under your mentorship",
                        initials = mentor.initials,
                        notificationCount = mentor.notifications,
                        onNotificationClick = { onAction("You have ${mentor.notifications} new notifications") },
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
                    MentorTab.entries.forEach { tab ->
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
            startDestination = MentorTab.HOME.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { rankridgeEnterTransition() },
            exitTransition = { rankridgeExitTransition() },
            popEnterTransition = { rankridgePopEnterTransition() },
            popExitTransition = { rankridgePopExitTransition() },
        ) {
            composable(MentorTab.HOME.route) {
                MentorHomeScreen(
                    dashboard = dashboard,
                    onOpenStudent = { id -> navController.navigate(studentDetailRoute(id)) },
                    onOpenStudents = { navController.navigateToTab(MentorTab.STUDENTS) },
                    onOpenFollowUps = { navController.navigateToTab(MentorTab.FOLLOWUPS) },
                    onAction = onAction,
                )
            }
            composable(MentorTab.STUDENTS.route) {
                MentorStudentsScreen(
                    dashboard = dashboard,
                    onOpenStudent = { id -> navController.navigate(studentDetailRoute(id)) },
                )
            }
            composable(MentorTab.FOLLOWUPS.route) {
                MentorFollowUpsScreen(
                    dashboard = dashboard,
                    onOpenStudent = { id -> navController.navigate(studentDetailRoute(id)) },
                    onMarkDone = { id -> viewModel.setFollowUpStatus(id, FollowUpStatus.DONE) },
                    onAction = onAction,
                )
            }
            composable(MentorTab.PROFILE.route) {
                MentorProfileScreen(dashboard = dashboard, onSignOut = onSignOut, onAction = onAction)
            }
            composable("$STUDENT_DETAIL_ROUTE/{$ARG_STUDENT_ID}") { backStackEntry ->
                val studentId = backStackEntry.arguments?.getString(ARG_STUDENT_ID).orEmpty()
                val student = dashboard.mentees.firstOrNull { it.id == studentId }
                if (student != null) {
                    MentorStudentDetailScreen(
                        student = student,
                        notes = dashboard.notesByStudent[studentId].orEmpty(),
                        onAddNote = { text -> viewModel.addNote(studentId, text) },
                        onAction = onAction,
                    )
                }
            }
        }
    }
}

private fun androidx.navigation.NavHostController.navigateToTab(tab: MentorTab) {
    navigate(tab.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
