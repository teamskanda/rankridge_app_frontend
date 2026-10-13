package com.rankridge.app.ui.counsellor.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rankridge.app.ui.common.DashboardCard
import com.rankridge.app.ui.common.MetricTile
import com.rankridge.app.ui.common.SectionHeader
import com.rankridge.app.ui.common.VSpace
import com.rankridge.app.ui.counsellor.ApplicationStatus
import com.rankridge.app.ui.counsellor.CounsellorDashboard
import com.rankridge.app.ui.counsellor.CounsellorMockData
import com.rankridge.app.ui.counsellor.LeadRow
import com.rankridge.app.ui.counsellor.LeadStatus
import com.rankridge.app.ui.counsellor.label
import com.rankridge.app.ui.theme.RankridgeTheme

@Composable
fun CounsellorHomeScreen(
    dashboard: CounsellorDashboard,
    onOpenLead: (String) -> Unit,
    onOpenLeads: () -> Unit,
    onOpenAdmissions: () -> Unit,
    onAction: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val metrics = dashboard.metrics
    val activeLeads = dashboard.leads.filter { it.status != LeadStatus.ADMITTED && it.status != LeadStatus.LOST }
    val followUpsToday = activeLeads.filter { it.nextFollowUpLabel == "Today" }
    val newLeads = activeLeads.filter { it.status == LeadStatus.NEW }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 28.dp),
    ) {
        VSpace(16)

        DashboardCard {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricTile(
                    value = "${metrics.newLeadsToday}",
                    label = "New leads",
                    modifier = Modifier.weight(1f),
                )
                MetricTile(
                    value = "${metrics.followUpsToday}",
                    label = "Follow-ups",
                    modifier = Modifier.weight(1f),
                    accent = MaterialTheme.colorScheme.secondary,
                )
                MetricTile(
                    value = "${metrics.conversionRatePercent}%",
                    label = "Conversion",
                    modifier = Modifier.weight(1f),
                    accent = MaterialTheme.colorScheme.tertiary,
                )
            }
            VSpace(10)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricTile(
                    value = "${metrics.applicationsPending}",
                    label = "Pending",
                    modifier = Modifier.weight(1f),
                )
                MetricTile(
                    value = "${metrics.admissionsThisMonth}",
                    label = "Admissions",
                    modifier = Modifier.weight(1f),
                    accent = MaterialTheme.colorScheme.tertiary,
                )
            }
        }

        if (newLeads.isNotEmpty()) {
            SectionHeader("New leads", actionText = "All leads", onActionClick = onOpenLeads)
            DashboardCard {
                newLeads.forEachIndexed { index, lead ->
                    LeadRow(lead = lead, onClick = { onOpenLead(lead.id) })
                    if (index != newLeads.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }

        if (followUpsToday.isNotEmpty()) {
            SectionHeader("Today's follow-ups", actionText = "All leads", onActionClick = onOpenLeads)
            DashboardCard {
                followUpsToday.forEachIndexed { index, lead ->
                    LeadRow(lead = lead, onClick = { onOpenLead(lead.id) })
                    if (index != followUpsToday.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }

        SectionHeader("Applications in progress", actionText = "View all", onActionClick = onOpenAdmissions)
        DashboardCard {
            val inProgress = dashboard.applications.filter { it.status != ApplicationStatus.ADMITTED }
            if (inProgress.isEmpty()) {
                Text(
                    "No applications awaiting action right now",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                inProgress.forEachIndexed { index, application ->
                    Text(application.studentName, style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = "${application.course} · ${application.status.label()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (index != inProgress.lastIndex) VSpace(10)
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 1800)
@Composable
private fun CounsellorHomePreview() {
    RankridgeTheme {
        CounsellorHomeScreen(
            dashboard = CounsellorMockData.dashboard,
            onOpenLead = {},
            onOpenLeads = {},
            onOpenAdmissions = {},
            onAction = {},
        )
    }
}
