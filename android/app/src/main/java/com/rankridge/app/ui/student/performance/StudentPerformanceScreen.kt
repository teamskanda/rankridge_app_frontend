package com.rankridge.app.ui.student.performance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rankridge.app.ui.student.AttendanceSummary
import com.rankridge.app.ui.student.PerformanceOverview
import com.rankridge.app.ui.student.StudentDashboard
import com.rankridge.app.ui.student.StudentMockData
import com.rankridge.app.ui.student.TopicAccuracy
import com.rankridge.app.ui.student.TrendDirection
import com.rankridge.app.ui.common.DashboardCard
import com.rankridge.app.ui.common.HSpace
import com.rankridge.app.ui.common.LabeledProgress
import com.rankridge.app.ui.common.MetricTile
import com.rankridge.app.ui.common.SectionHeader
import com.rankridge.app.ui.common.Sparkline
import com.rankridge.app.ui.common.StatusPill
import com.rankridge.app.ui.common.VSpace
import com.rankridge.app.ui.common.bandColors
import com.rankridge.app.ui.common.label
import com.rankridge.app.ui.theme.RankridgeTheme

@Composable
fun StudentPerformanceScreen(
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
        SectionHeader("Overview")
        OverviewCard(dashboard.overview)

        SectionHeader("Subject mastery")
        SubjectMasteryCard(dashboard)

        SectionHeader("Strengths")
        TopicListCard(dashboard.strengths, strong = true)

        SectionHeader("Needs attention", actionText = "Practice", onActionClick = { onAction("Opening recommended practice") })
        TopicListCard(dashboard.weaknesses, strong = false)

        SectionHeader("Attendance")
        AttendanceFullCard(dashboard.attendance)

        VSpace(20)
        androidx.compose.material3.OutlinedButton(
            onClick = { onAction("Downloading full performance report") },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Download full report")
        }
    }
}

@Composable
private fun SubjectMasteryCard(dashboard: StudentDashboard) {
    DashboardCard {
        dashboard.latestResult.subjectSplit.forEachIndexed { index, subject ->
            val colors = bandColors(subject.band)
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(subject.subject, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                Text(
                    text = "${subject.percent}%",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(end = 10.dp),
                )
                StatusPill(text = subject.band.label(), container = colors.container, content = colors.content)
            }
            if (index != dashboard.latestResult.subjectSplit.lastIndex) {
                androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
private fun OverviewCard(overview: PerformanceOverview) {
    DashboardCard {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricTile(value = "${overview.averageScorePercent}%", label = "Avg score", modifier = Modifier.weight(1f))
            MetricTile(value = "${overview.testsAttempted}", label = "Tests taken", modifier = Modifier.weight(1f), accent = MaterialTheme.colorScheme.secondary)
        }
        VSpace(10)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricTile(value = "${overview.attendancePercent}%", label = "Attendance", modifier = Modifier.weight(1f), accent = MaterialTheme.colorScheme.tertiary)
            MetricTile(value = "${overview.watchTimeHours}h", label = "Watch time", modifier = Modifier.weight(1f))
        }
        VSpace(16)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Last 5 test scores", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
            TrendBadge(overview.trend)
        }
        VSpace(8)
        Sparkline(values = overview.last5Trend, modifier = Modifier.fillMaxWidth().height(72.dp))
        VSpace(4)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            overview.last5Trend.forEach { v ->
                Text("$v", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun TrendBadge(trend: TrendDirection) {
    val (icon, container, content, text) = when (trend) {
        TrendDirection.IMPROVING -> Quad(Icons.Filled.TrendingUp, MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer, "Improving")
        TrendDirection.STABLE -> Quad(Icons.Filled.TrendingFlat, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer, "Stable")
        TrendDirection.DECLINING -> Quad(Icons.Filled.TrendingDown, MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer, "Declining")
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(container, MaterialTheme.shapes.extraLarge)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Icon(icon, contentDescription = null, tint = content, modifier = Modifier.size(16.dp))
        HSpace(4)
        Text(text, style = MaterialTheme.typography.labelMedium, color = content)
    }
}

private data class Quad(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val container: androidx.compose.ui.graphics.Color,
    val content: androidx.compose.ui.graphics.Color,
    val text: String,
)

@Composable
private fun TopicListCard(topics: List<TopicAccuracy>, strong: Boolean) {
    val barColor = if (strong) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
    DashboardCard {
        topics.forEachIndexed { index, topic ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(topic.topic, style = MaterialTheme.typography.titleSmall)
                    Text(topic.subject, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            LabeledProgress(
                label = "Accuracy",
                percent = topic.accuracyPercent,
                color = barColor,
            )
            if (index != topics.lastIndex) VSpace(8)
        }
    }
}

@Composable
private fun AttendanceFullCard(attendance: AttendanceSummary) {
    DashboardCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { attendance.monthPercent / 100f },
                    modifier = Modifier.size(72.dp),
                    strokeWidth = 8.dp,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                Text("${attendance.monthPercent}%", style = MaterialTheme.typography.titleMedium)
            }
            HSpace(16)
            Column {
                Text(attendance.monthLabel, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = "${attendance.attended} of ${attendance.total} classes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        androidx.compose.material3.HorizontalDivider(
            modifier = Modifier.padding(vertical = 12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
        )
        Text("Subject-wise", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        attendance.subjectWise.forEach { subject ->
            LabeledProgress(label = subject.subject, percent = subject.percent)
        }
        if (attendance.recentNotes.isNotEmpty()) {
            VSpace(8)
            Text("Recent", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            VSpace(4)
            attendance.recentNotes.forEach { note ->
                StatusPill(
                    text = note,
                    container = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                    content = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(vertical = 2.dp),
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 1500)
@Composable
private fun StudentPerformancePreview() {
    RankridgeTheme {
        StudentPerformanceScreen(dashboard = StudentMockData.dashboard, onAction = {})
    }
}
