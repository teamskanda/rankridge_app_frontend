package com.rankridge.app.ui.mentor.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rankridge.app.ui.common.DashboardCard
import com.rankridge.app.ui.common.HSpace
import com.rankridge.app.ui.common.MetricTile
import com.rankridge.app.ui.common.SectionHeader
import com.rankridge.app.ui.common.VSpace
import com.rankridge.app.ui.mentor.FollowUpStatus
import com.rankridge.app.ui.mentor.MenteeRow
import com.rankridge.app.ui.mentor.MentorDashboard
import com.rankridge.app.ui.mentor.MentorMockData
import com.rankridge.app.ui.mentor.RiskLevel
import com.rankridge.app.ui.theme.RankridgeTheme

@Composable
fun MentorHomeScreen(
    dashboard: MentorDashboard,
    onOpenStudent: (String) -> Unit,
    onOpenStudents: () -> Unit,
    onOpenFollowUps: () -> Unit,
    onAction: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val needsAttention = dashboard.mentees
        .filter { it.riskLevel != RiskLevel.LOW }
        .sortedBy { if (it.riskLevel == RiskLevel.HIGH) 0 else 1 }
    val pendingFollowUps = dashboard.followUps.filter { it.status == FollowUpStatus.PENDING }
    val highRiskCount = dashboard.mentees.count { it.riskLevel == RiskLevel.HIGH }
    val dueTodayCount = pendingFollowUps.count { it.dueLabel == "Today" }

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
                MetricTile(value = "${dashboard.mentees.size}", label = "Students", modifier = Modifier.weight(1f))
                MetricTile(
                    value = "$highRiskCount",
                    label = "High risk",
                    modifier = Modifier.weight(1f),
                    accent = MaterialTheme.colorScheme.error,
                )
                MetricTile(
                    value = "$dueTodayCount",
                    label = "Due today",
                    modifier = Modifier.weight(1f),
                    accent = MaterialTheme.colorScheme.secondary,
                )
            }
        }

        if (needsAttention.isNotEmpty()) {
            SectionHeader("Needs attention", actionText = "All students", onActionClick = onOpenStudents)
            DashboardCard {
                needsAttention.forEachIndexed { index, student ->
                    MenteeRow(student = student, onClick = { onOpenStudent(student.id) })
                    if (index != needsAttention.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }

        if (pendingFollowUps.isNotEmpty()) {
            SectionHeader("Parent follow-ups", actionText = "View all", onActionClick = onOpenFollowUps)
            DashboardCard {
                pendingFollowUps.take(3).forEachIndexed { index, followUp ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenStudent(followUp.studentId) }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.EventNote, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        HSpace(12)
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${followUp.studentName} · ${followUp.dueLabel}", style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = followUp.agenda,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    if (index != pendingFollowUps.take(3).lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 1600)
@Composable
private fun MentorHomePreview() {
    RankridgeTheme {
        MentorHomeScreen(
            dashboard = MentorMockData.dashboard,
            onOpenStudent = {},
            onOpenStudents = {},
            onOpenFollowUps = {},
            onAction = {},
        )
    }
}
