package com.rankridge.app.ui.teacher.batches

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rankridge.app.ui.common.DashboardCard
import com.rankridge.app.ui.common.MetricTile
import com.rankridge.app.ui.common.SectionHeader
import com.rankridge.app.ui.common.StatusPill
import com.rankridge.app.ui.common.VSpace
import com.rankridge.app.ui.teacher.AssignmentItem
import com.rankridge.app.ui.teacher.SyllabusItem
import com.rankridge.app.ui.teacher.SyllabusStatus
import com.rankridge.app.ui.teacher.TeacherBatch
import com.rankridge.app.ui.teacher.TeacherMockData
import com.rankridge.app.ui.teacher.TestItem
import com.rankridge.app.ui.teacher.TestStatus
import com.rankridge.app.ui.teacher.label
import com.rankridge.app.ui.teacher.syllabusColors
import com.rankridge.app.ui.theme.RankridgeTheme

@Composable
fun TeacherBatchDetailScreen(
    batch: TeacherBatch,
    syllabusItems: List<SyllabusItem>,
    tests: List<TestItem>,
    assignments: List<AssignmentItem>,
    onMarkSyllabusTaught: (String) -> Unit,
    onAction: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 28.dp),
    ) {
        VSpace(16)
        DashboardCard {
            Text(batch.subject, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${batch.studentCount} students", style = MaterialTheme.typography.titleSmall)
            VSpace(12)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricTile(value = "${batch.syllabusCompletionPercent}%", label = "Syllabus", modifier = Modifier.weight(1f))
                MetricTile(
                    value = "${batch.attendanceAveragePercent}%",
                    label = "Attendance",
                    modifier = Modifier.weight(1f),
                    accent = MaterialTheme.colorScheme.secondary,
                )
                MetricTile(
                    value = "${batch.latestTestAveragePercent}%",
                    label = "Last test avg",
                    modifier = Modifier.weight(1f),
                    accent = MaterialTheme.colorScheme.tertiary,
                )
            }
        }

        SectionHeader("Syllabus")
        DashboardCard {
            syllabusItems.forEachIndexed { index, item ->
                SyllabusRow(item, onMarkSyllabusTaught)
                if (index != syllabusItems.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                }
            }
        }

        if (tests.isNotEmpty()) {
            SectionHeader("Tests")
            DashboardCard {
                tests.forEachIndexed { index, test ->
                    TestRow(test)
                    if (index != tests.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }

        if (assignments.isNotEmpty()) {
            SectionHeader("Assignments")
            DashboardCard {
                assignments.forEachIndexed { index, assignment ->
                    AssignmentRow(assignment)
                    if (index != assignments.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@Composable
private fun SyllabusRow(item: SyllabusItem, onMarkSyllabusTaught: (String) -> Unit) {
    val colors = syllabusColors(item.status)
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.topic, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = item.plannedDateLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (item.status == SyllabusStatus.NOT_MARKED) {
            Button(onClick = { onMarkSyllabusTaught(item.id) }) {
                Text("Mark taught")
            }
        } else {
            StatusPill(text = item.status.label(), container = colors.container, content = colors.content)
        }
    }
}

@Composable
private fun TestRow(test: TestItem) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(test.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = if (test.status == TestStatus.COMPLETED && test.batchAverage != null) {
                    "${test.dateLabel} · Avg ${test.batchAverage}% · High ${test.highest}% · Low ${test.lowest}%"
                } else {
                    test.dateLabel
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        StatusPill(
            text = when (test.status) {
                TestStatus.UPCOMING -> "Upcoming"
                TestStatus.NEEDS_EVALUATION -> "Needs evaluation"
                TestStatus.COMPLETED -> "Completed"
            },
            container = when (test.status) {
                TestStatus.NEEDS_EVALUATION -> MaterialTheme.colorScheme.errorContainer
                TestStatus.UPCOMING -> MaterialTheme.colorScheme.surfaceVariant
                TestStatus.COMPLETED -> MaterialTheme.colorScheme.tertiaryContainer
            },
            content = when (test.status) {
                TestStatus.NEEDS_EVALUATION -> MaterialTheme.colorScheme.onErrorContainer
                TestStatus.UPCOMING -> MaterialTheme.colorScheme.onSurfaceVariant
                TestStatus.COMPLETED -> MaterialTheme.colorScheme.onTertiaryContainer
            },
        )
    }
}

@Composable
private fun AssignmentRow(assignment: AssignmentItem) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(assignment.title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "Due ${assignment.dueDateLabel} · ${assignment.submittedCount}/${assignment.totalCount} submitted",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (assignment.overdue) {
            StatusPill(
                text = "Overdue",
                container = MaterialTheme.colorScheme.errorContainer,
                content = MaterialTheme.colorScheme.onErrorContainer,
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 1800)
@Composable
private fun TeacherBatchDetailPreview() {
    val dashboard = TeacherMockData.dashboard
    val batch = dashboard.batches.first()
    RankridgeTheme {
        TeacherBatchDetailScreen(
            batch = batch,
            syllabusItems = dashboard.syllabusItems.filter { it.batchId == batch.id },
            tests = dashboard.tests.filter { it.batchId == batch.id },
            assignments = dashboard.assignments.filter { it.batchId == batch.id },
            onMarkSyllabusTaught = {},
            onAction = {},
        )
    }
}
