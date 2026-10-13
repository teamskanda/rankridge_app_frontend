package com.rankridge.app.ui.student.tests

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rankridge.app.ui.student.CompletedTest
import com.rankridge.app.ui.student.LatestResult
import com.rankridge.app.ui.student.StudentDashboard
import com.rankridge.app.ui.student.StudentMockData
import com.rankridge.app.ui.student.UpcomingTest
import com.rankridge.app.ui.common.DashboardCard
import com.rankridge.app.ui.common.HSpace
import com.rankridge.app.ui.common.SectionHeader
import com.rankridge.app.ui.common.StatRow
import com.rankridge.app.ui.common.StatusPill
import com.rankridge.app.ui.common.VSpace
import com.rankridge.app.ui.common.bandColors
import com.rankridge.app.ui.common.label
import com.rankridge.app.ui.theme.RankridgeTheme

@Composable
fun StudentTestsScreen(
    dashboard: StudentDashboard,
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
        SectionHeader("Upcoming")
        dashboard.upcomingTests.forEach { test ->
            UpcomingTestRow(test, onAction)
            VSpace(8)
        }

        SectionHeader("Latest result")
        CompletedResultCard(dashboard.latestResult, onAction)

        SectionHeader("Past tests", actionText = "Export", onActionClick = { onAction("Exporting results") })
        DashboardCard {
            dashboard.completedTests.forEachIndexed { index, test ->
                PastTestRow(test, onAction)
                if (index != dashboard.completedTests.lastIndex) {
                    androidx.compose.material3.HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun UpcomingTestRow(test: UpcomingTest, onAction: (String) -> Unit) {
    DashboardCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.CalendarMonth,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
            )
            HSpace(12)
            Column(modifier = Modifier.weight(1f)) {
                Text(test.name, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = test.dateLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            StatusPill(
                text = "${test.totalMarks} marks",
                container = MaterialTheme.colorScheme.secondaryContainer,
                content = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
        VSpace(8)
        Text(
            text = "Syllabus: ${test.syllabus}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "Duration: ${test.durationLabel}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        VSpace(12)
        Row {
            OutlinedButton(onClick = { onAction("Opening syllabus for ${test.name}") }, modifier = Modifier.weight(1f)) { Text("Syllabus") }
            HSpace(12)
            Button(onClick = { onAction("Reminder set for ${test.name}") }, modifier = Modifier.weight(1f)) { Text("Set reminder") }
        }
    }
}

@Composable
private fun CompletedResultCard(result: LatestResult, onAction: (String) -> Unit) {
    DashboardCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(result.testName, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
            StatusPill(
                text = "${result.percentage}%",
                container = MaterialTheme.colorScheme.primaryContainer,
                content = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        VSpace(8)
        StatRow("Score", "${result.score} / ${result.totalMarks}")
        StatRow("Percentile", "${result.percentile}%ile")
        StatRow("Batch rank", "#${result.batchRank} of ${result.batchSize}")
        StatRow("Overall rank", "#${result.overallRank}")
        androidx.compose.material3.HorizontalDivider(
            modifier = Modifier.padding(vertical = 10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
        )
        Text("Subject split", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        VSpace(4)
        result.subjectSplit.forEach { subject ->
            val colors = bandColors(subject.band)
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(subject.subject, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                Text("${subject.percent}%", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(end = 10.dp))
                StatusPill(text = subject.band.label(), container = colors.container, content = colors.content)
            }
        }
        VSpace(12)
        OutlinedButton(onClick = { onAction("Opening detailed report") }, modifier = Modifier.fillMaxWidth()) {
            Text("View detailed report")
        }
    }
}

@Composable
private fun PastTestRow(test: CompletedTest, onAction: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().clickable { onAction("Opening ${test.name}") },
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "${test.scorePercent}%",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold,
            )
        }
        HSpace(12)
        Column(modifier = Modifier.weight(1f)) {
            Text(test.name, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "${test.dateLabel} · Batch rank #${test.batchRank}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 1800)
@Composable
private fun StudentTestsPreview() {
    RankridgeTheme {
        StudentTestsScreen(dashboard = StudentMockData.dashboard, onAction = {})
    }
}
