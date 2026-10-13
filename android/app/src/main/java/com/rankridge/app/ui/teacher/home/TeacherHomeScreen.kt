package com.rankridge.app.ui.teacher.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.rankridge.app.ui.common.DashboardCard
import com.rankridge.app.ui.common.HSpace
import com.rankridge.app.ui.common.MetricTile
import com.rankridge.app.ui.common.SectionHeader
import com.rankridge.app.ui.common.StatusPill
import com.rankridge.app.ui.common.VSpace
import com.rankridge.app.ui.teacher.DoubtStatus
import com.rankridge.app.ui.teacher.RiskLevel
import com.rankridge.app.ui.teacher.ScheduleEvent
import com.rankridge.app.ui.teacher.TeacherDashboard
import com.rankridge.app.ui.teacher.TeacherMockData
import com.rankridge.app.ui.teacher.TeacherStudentRow
import com.rankridge.app.ui.teacher.doubtColors
import com.rankridge.app.ui.teacher.icon
import com.rankridge.app.ui.teacher.label
import com.rankridge.app.ui.theme.RankridgeTheme

@Composable
fun TeacherHomeScreen(
    dashboard: TeacherDashboard,
    onOpenStudent: (String) -> Unit,
    onOpenStudents: () -> Unit,
    onOpenSchedule: () -> Unit,
    onMarkAttendance: (String) -> Unit,
    onResolveDoubt: (String) -> Unit,
    onAction: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val today = dashboard.schedule.filter { it.dayLabel == "Today" }
    val pendingAttendance = today.count { it.requiresAttendance && !it.attendanceMarked }
    val avgSyllabus = if (dashboard.batches.isEmpty()) 0 else dashboard.batches.sumOf { it.syllabusCompletionPercent } / dashboard.batches.size
    val ptmsToday = dashboard.ptms.count { it.dateLabel == "Today" }
    val needsAttention = dashboard.students
        .filter { it.riskLevel != RiskLevel.LOW }
        .sortedBy { if (it.riskLevel == RiskLevel.HIGH) 0 else 1 }
    val openDoubts = dashboard.doubts.filter { it.status != DoubtStatus.RESOLVED }
        .sortedBy { if (it.status == DoubtStatus.URGENT) 0 else 1 }

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
                MetricTile(value = "${today.size}", label = "Today", modifier = Modifier.weight(1f))
                MetricTile(
                    value = "$avgSyllabus%",
                    label = "Syllabus",
                    modifier = Modifier.weight(1f),
                    accent = MaterialTheme.colorScheme.secondary,
                )
                MetricTile(
                    value = "$pendingAttendance",
                    label = "Pending att.",
                    modifier = Modifier.weight(1f),
                    accent = MaterialTheme.colorScheme.error,
                )
                MetricTile(
                    value = "$ptmsToday",
                    label = "PTMs today",
                    modifier = Modifier.weight(1f),
                    accent = MaterialTheme.colorScheme.tertiary,
                )
            }
        }

        SectionHeader("Today's schedule", actionText = "Full schedule", onActionClick = onOpenSchedule)
        if (today.isEmpty()) {
            DashboardCard {
                Text("Nothing on today's schedule", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            DashboardCard {
                today.forEachIndexed { index, event ->
                    ScheduleRow(event, onMarkAttendance)
                    if (index != today.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }

        if (needsAttention.isNotEmpty()) {
            SectionHeader("Students needing attention", actionText = "All students", onActionClick = onOpenStudents)
            DashboardCard {
                needsAttention.forEachIndexed { index, student ->
                    TeacherStudentRow(student = student, onClick = { onOpenStudent(student.id) })
                    if (index != needsAttention.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }

        if (openDoubts.isNotEmpty()) {
            SectionHeader("Doubts queue (${openDoubts.size})")
            DashboardCard {
                openDoubts.forEachIndexed { index, doubt ->
                    val colors = doubtColors(doubt.status)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("${doubt.studentName} · ${doubt.batchName}", style = MaterialTheme.typography.titleSmall)
                                HSpace(8)
                                StatusPill(text = doubt.status.label(), container = colors.container, content = colors.content)
                            }
                            Text(
                                text = doubt.question,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 2.dp),
                            )
                        }
                        TextButton(onClick = { onResolveDoubt(doubt.id) }) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                            Text("Resolve")
                        }
                    }
                    if (index != openDoubts.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleRow(event: ScheduleEvent, onMarkAttendance: (String) -> Unit) {
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

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 2000)
@Composable
private fun TeacherHomePreview() {
    RankridgeTheme {
        TeacherHomeScreen(
            dashboard = TeacherMockData.dashboard,
            onOpenStudent = {},
            onOpenStudents = {},
            onOpenSchedule = {},
            onMarkAttendance = {},
            onResolveDoubt = {},
            onAction = {},
        )
    }
}
