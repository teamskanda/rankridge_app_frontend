package com.rankridge.app.ui.counsellor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Assignment
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
import com.rankridge.app.ui.counsellor.admissions.CounsellorAdmissionsScreen
import com.rankridge.app.ui.counsellor.home.CounsellorHomeScreen
import com.rankridge.app.ui.counsellor.leads.CounsellorLeadDetailScreen
import com.rankridge.app.ui.counsellor.leads.CounsellorLeadsScreen
import com.rankridge.app.ui.counsellor.profile.CounsellorProfileScreen
import com.rankridge.app.ui.theme.rankridgeBarColor
import com.rankridge.app.ui.theme.rankridgeBarContentColor
import kotlinx.coroutines.launch

/** The four Counsellor tabs: Home → Leads → Admissions → Profile — a lead-and-application
 * pipeline, deliberately as compact as the Mentor dashboard since a counsellor triages inbound
 * leads rather than running batches or academic programs. */
private enum class CounsellorTab(
    val route: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    HOME("counsellor_tab_home", R.string.nav_home, Icons.Filled.Home, Icons.Outlined.Home),
    LEADS("counsellor_tab_leads", R.string.nav_counsellor_leads, Icons.Filled.Groups, Icons.Outlined.Groups),
    ADMISSIONS("counsellor_tab_admissions", R.string.nav_counsellor_admissions, Icons.Filled.Assignment, Icons.Outlined.Assignment),
    PROFILE("counsellor_tab_profile", R.string.nav_profile, Icons.Filled.Person, Icons.Outlined.Person),
}

private const val LEAD_DETAIL_ROUTE = "counsellor_lead_detail"
private const val ARG_LEAD_ID = "leadId"
private fun leadDetailRoute(leadId: String) = "$LEAD_DETAIL_ROUTE/$leadId"

/**
 * Counsellor dashboard host: one shared [CounsellorDashboardViewModel] feeds all four tabs plus
 * the lead-detail drill-down, switched by the same Material 3 bottom navigation chrome as the
 * Student/Parent/Mentor/Teacher dashboards. [onSignOut] bubbles back up to the app-level nav graph.
 */
@Composable
fun CounsellorDashboardScreen(
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CounsellorDashboardViewModel = viewModel(),
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
    val onDetailScreen = currentDestination?.route?.startsWith(LEAD_DETAIL_ROUTE) == true
    val currentTab = CounsellorTab.entries.firstOrNull { tab ->
        currentDestination?.hierarchy?.any { it.route == tab.route } == true
    } ?: CounsellorTab.HOME
    val dashboard = uiState.dashboard

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            when {
                onDetailScreen -> {
                    val leadName = dashboard?.leads
                        ?.firstOrNull { it.id == navBackStackEntry?.arguments?.getString(ARG_LEAD_ID) }
                        ?.name
                    RankridgeTopBar(
                        title = leadName ?: stringResource(R.string.nav_counsellor_leads),
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                            }
                        },
                    )
                }
                currentTab == CounsellorTab.HOME && dashboard != null -> {
                    val counsellor = dashboard.counsellor
                    RankridgeHomeTopBar(
                        greeting = "Hi, ${counsellor.name.substringBefore(" ")} 👋",
                        subtitle = "${dashboard.leads.size} leads in your pipeline",
                        initials = counsellor.initials,
                        notificationCount = counsellor.notifications,
                        onNotificationClick = { onAction("You have ${counsellor.notifications} new notifications") },
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
                    CounsellorTab.entries.forEach { tab ->
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
            startDestination = CounsellorTab.HOME.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { rankridgeEnterTransition() },
            exitTransition = { rankridgeExitTransition() },
            popEnterTransition = { rankridgePopEnterTransition() },
            popExitTransition = { rankridgePopExitTransition() },
        ) {
            composable(CounsellorTab.HOME.route) {
                CounsellorHomeScreen(
                    dashboard = dashboard,
                    onOpenLead = { id -> navController.navigate(leadDetailRoute(id)) },
                    onOpenLeads = { navController.navigateToTab(CounsellorTab.LEADS) },
                    onOpenAdmissions = { navController.navigateToTab(CounsellorTab.ADMISSIONS) },
                    onAction = onAction,
                )
            }
            composable(CounsellorTab.LEADS.route) {
                CounsellorLeadsScreen(
                    dashboard = dashboard,
                    onOpenLead = { id -> navController.navigate(leadDetailRoute(id)) },
                )
            }
            composable(CounsellorTab.ADMISSIONS.route) {
                CounsellorAdmissionsScreen(dashboard = dashboard)
            }
            composable(CounsellorTab.PROFILE.route) {
                CounsellorProfileScreen(dashboard = dashboard, onSignOut = onSignOut, onAction = onAction)
            }
            composable("$LEAD_DETAIL_ROUTE/{$ARG_LEAD_ID}") { backStackEntry ->
                val leadId = backStackEntry.arguments?.getString(ARG_LEAD_ID).orEmpty()
                val lead = dashboard.leads.firstOrNull { it.id == leadId }
                if (lead != null) {
                    CounsellorLeadDetailScreen(
                        lead = lead,
                        notes = dashboard.notesByLead[leadId].orEmpty(),
                        onMarkContacted = { viewModel.markContacted(leadId) },
                        onAddNote = { text -> viewModel.addNote(leadId, text) },
                        onAction = onAction,
                    )
                }
            }
        }
    }
}

private fun androidx.navigation.NavHostController.navigateToTab(tab: CounsellorTab) {
    navigate(tab.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
