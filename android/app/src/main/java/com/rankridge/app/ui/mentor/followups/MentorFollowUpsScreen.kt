package com.rankridge.app.ui.mentor.followups

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rankridge.app.ui.common.DashboardCard
import com.rankridge.app.ui.common.HSpace
import com.rankridge.app.ui.common.SectionHeader
import com.rankridge.app.ui.common.VSpace
import com.rankridge.app.ui.mentor.FollowUpStatus
import com.rankridge.app.ui.mentor.MentorDashboard
import com.rankridge.app.ui.mentor.MentorMockData
import com.rankridge.app.ui.mentor.ParentFollowUp
import com.rankridge.app.ui.theme.RankridgeTheme

@Composable
fun MentorFollowUpsScreen(
    dashboard: MentorDashboard,
    onOpenStudent: (String) -> Unit,
    onMarkDone: (String) -> Unit,
    onAction: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pending = dashboard.followUps.filter { it.status == FollowUpStatus.PENDING }
    val done = dashboard.followUps.filter { it.status == FollowUpStatus.DONE }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 28.dp),
    ) {
        VSpace(16)

        SectionHeader("Pending (${pending.size})")
        if (pending.isEmpty()) {
            DashboardCard {
                Text(
                    "No pending follow-ups — you're all caught up",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            DashboardCard {
                pending.forEachIndexed { index, followUp ->
                    FollowUpRow(followUp, onOpenStudent, onMarkDone)
                    if (index != pending.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }

        if (done.isNotEmpty()) {
            SectionHeader("Completed (${done.size})")
            DashboardCard {
                done.forEachIndexed { index, followUp ->
                    FollowUpRow(followUp, onOpenStudent, onMarkDone = null)
                    if (index != done.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@Composable
private fun FollowUpRow(
    followUp: ParentFollowUp,
    onOpenStudent: (String) -> Unit,
    onMarkDone: ((String) -> Unit)?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenStudent(followUp.studentId) }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Filled.EventNote,
            contentDescription = null,
            tint = if (followUp.status == FollowUpStatus.DONE) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.secondary
            },
        )
        HSpace(12)
        Column(modifier = Modifier.weight(1f)) {
            Text("${followUp.studentName} · ${followUp.dueLabel}", style = MaterialTheme.typography.titleSmall)
            Text(
                text = followUp.agenda,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (onMarkDone != null) {
            TextButton(onClick = { onMarkDone(followUp.id) }) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                Text("Done")
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 1200)
@Composable
private fun MentorFollowUpsPreview() {
    RankridgeTheme {
        MentorFollowUpsScreen(dashboard = MentorMockData.dashboard, onOpenStudent = {}, onMarkDone = {}, onAction = {})
    }
}
