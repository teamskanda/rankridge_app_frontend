package com.rankridge.app.ui.teacher.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.rankridge.app.ui.common.DashboardCard
import com.rankridge.app.ui.common.HSpace
import com.rankridge.app.ui.common.SectionHeader
import com.rankridge.app.ui.common.StatusPill
import com.rankridge.app.ui.common.VSpace
import com.rankridge.app.ui.teacher.PtmStatus
import com.rankridge.app.ui.teacher.ScheduleEvent
import com.rankridge.app.ui.teacher.TeacherDashboard
import com.rankridge.app.ui.teacher.TeacherMockData
import com.rankridge.app.ui.teacher.icon
import com.rankridge.app.ui.teacher.label
import com.rankridge.app.ui.theme.RankridgeTheme

@Composable
fun TeacherScheduleScreen(
    dashboard: TeacherDashboard,
    onMarkAttendance: (String) -> Unit,
    onAction: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val byDay = dashboard.schedule.groupBy { it.dayLabel }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 28.dp),
    ) {
        VSpace(16)
        byDay.forEach { (day, events) ->
            SectionHeader(day)
            DashboardCard {
                events.forEachIndexed { index, event ->
                    EventRow(event, onMarkAttendance)
                    if (index != events.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }

        if (dashboard.ptms.isNotEmpty()) {
            SectionHeader("Parent-teacher meetings")
            DashboardCard {
                dashboard.ptms.forEachIndexed { index, ptm ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${ptm.studentName} · ${ptm.dateLabel}${if (ptm.timeLabel != "—") " · ${ptm.timeLabel}" else ""}", style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = "${ptm.parentName} — ${ptm.concern}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        StatusPill(
                            text = if (ptm.status == PtmStatus.SCHEDULED) "Scheduled" else "Completed",
                            container = if (ptm.status == PtmStatus.SCHEDULED) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.tertiaryContainer,
                            content = if (ptm.status == PtmStatus.SCHEDULED) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                    }
                    if (index != dashboard.ptms.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@Composable
private fun EventRow(event: ScheduleEvent, onMarkAttendance: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(event.type.icon(), contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(20.dp))
        }
        HSpace(12)
        Column(modifier = Modifier.weight(1f)) {
            Text("${event.timeLabel} · ${event.type.label()}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(event.subjectOrTopic, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "${event.batchName} · ${event.location}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (event.requiresAttendance) {
            if (event.attendanceMarked) {
                StatusPill(
                    text = "Marked",
                    container = MaterialTheme.colorScheme.tertiaryContainer,
                    content = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            } else {
                Button(onClick = { onMarkAttendance(event.id) }) {
                    Text("Mark")
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 1800)
@Composable
private fun TeacherSchedulePreview() {
    RankridgeTheme {
        TeacherScheduleScreen(dashboard = TeacherMockData.dashboard, onMarkAttendance = {}, onAction = {})
    }
}
