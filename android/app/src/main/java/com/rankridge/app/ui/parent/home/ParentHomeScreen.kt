package com.rankridge.app.ui.parent.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rankridge.app.ui.common.DashboardCard
import com.rankridge.app.ui.common.HSpace
import com.rankridge.app.ui.common.MetricTile
import com.rankridge.app.ui.common.SectionHeader
import com.rankridge.app.ui.common.Sparkline
import com.rankridge.app.ui.common.StatRow
import com.rankridge.app.ui.common.StatusPill
import com.rankridge.app.ui.common.VSpace
import com.rankridge.app.ui.common.bandColors
import com.rankridge.app.ui.common.label
import com.rankridge.app.ui.parent.AlertSeverity
import com.rankridge.app.ui.parent.ChildProfile
import com.rankridge.app.ui.parent.ChildSnapshot
import com.rankridge.app.ui.parent.ParentDashboard
import com.rankridge.app.ui.parent.ParentMockData
import com.rankridge.app.ui.parent.PtmMeeting
import com.rankridge.app.ui.parent.TrendDirection
import com.rankridge.app.ui.parent.UrgentAlert
import com.rankridge.app.ui.theme.RankridgeTheme

@Composable
fun ParentHomeScreen(
    dashboard: ParentDashboard,
    snapshot: ChildSnapshot,
    selectedChildId: String,
    onSelectChild: (String) -> Unit,
    onOpenProgress: () -> Unit,
    onOpenAttendance: () -> Unit,
    onOpenUpdates: () -> Unit,
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

        if (dashboard.children.size > 1) {
            ChildSelector(dashboard.children, selectedChildId, onSelectChild)
            VSpace(14)
        }

        if (snapshot.urgentAlerts.isNotEmpty()) {
            snapshot.urgentAlerts.forEach { alert ->
                UrgentAlertCard(alert, onAction)
                VSpace(8)
            }
        }

        SectionHeader("This week")
        WeeklyOverviewCard(snapshot, onOpenAttendance)

        SectionHeader("Latest test", actionText = "Full report", onActionClick = onOpenProgress)
        LatestTestCard(snapshot, onOpenProgress)

        SectionHeader("Performance trend", actionText = "Details", onActionClick = onOpenProgress)
        PerformanceTrendCard(snapshot)

        SectionHeader("Strengths & concerns", actionText = "More", onActionClick = onOpenProgress)
        StrengthsConcernsCard(snapshot)

        SectionHeader("Teacher & PTM", actionText = "All updates", onActionClick = onOpenUpdates)
        TeacherAndPtmCard(snapshot, onAction)

        SectionHeader("Fees", actionText = "View details", onActionClick = onOpenUpdates)
        FeesMiniCard(snapshot, onAction)
    }
}

@Composable
private fun ChildSelector(children: List<ChildProfile>, selectedChildId: String, onSelectChild: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        children.forEach { child ->
            val selected = child.id == selectedChildId
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    )
                    .clickable { onSelectChild(child.id) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(
                            if (selected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.primaryContainer,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = child.initials,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
                HSpace(8)
                Column {
                    Text(
                        text = child.name,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "${child.classYear} · ${child.batch}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun UrgentAlertCard(alert: UrgentAlert, onAction: (String) -> Unit) {
    val (container, content) = when (alert.severity) {
        AlertSeverity.URGENT -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        AlertSeverity.WARNING -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        AlertSeverity.INFO -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(container)
            .clickable { onAction("Opening: ${alert.title}") }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = if (alert.severity == AlertSeverity.URGENT) Icons.Filled.ReportProblem else Icons.Filled.Warning,
            contentDescription = null,
            tint = content,
        )
        HSpace(12)
        Column(modifier = Modifier.weight(1f)) {
            Text(alert.title, style = MaterialTheme.typography.titleSmall, color = content)
            Text(alert.detail, style = MaterialTheme.typography.bodySmall, color = content)
        }
    }
}

@Composable
private fun WeeklyOverviewCard(snapshot: ChildSnapshot, onOpenAttendance: () -> Unit) {
    val overview = snapshot.weeklyOverview
    DashboardCard {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricTile(value = "${overview.attendancePercent}%", label = "Attendance", modifier = Modifier.weight(1f))
            MetricTile(
                value = "${overview.averageScorePercent}%",
                label = "Avg score",
                modifier = Modifier.weight(1f),
                accent = MaterialTheme.colorScheme.secondary,
            )
            MetricTile(
                value = "${overview.classesAttended}/${overview.classesTotal}",
                label = "Classes",
                modifier = Modifier.weight(1f),
                accent = MaterialTheme.colorScheme.tertiary,
            )
        }
        VSpace(12)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                .clickable(onClick = onOpenAttendance)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (overview.pendingWorkCount > 0) {
                    "${overview.testsThisWeek} test(s) this week · ${overview.pendingWorkCount} pending work item(s)"
                } else {
                    "${overview.testsThisWeek} test(s) this week · no pending work"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun LatestTestCard(snapshot: ChildSnapshot, onOpenDetails: () -> Unit) {
    val result = snapshot.latestTest
    DashboardCard(modifier = Modifier.clickable(onClick = onOpenDetails)) {
        Row(verticalAlignment = Alignment.Bottom) {
            Column(modifier = Modifier.weight(1f)) {
                Text(result.testName, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${result.score}",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = " / ${result.totalMarks}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }
            }
            StatusPill(
                text = "${result.percentage}% · ${result.percentile}%ile",
                container = MaterialTheme.colorScheme.primaryContainer,
                content = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        VSpace(8)
        StatRow(label = "Batch rank", value = "#${result.batchRank} / ${result.batchSize}")
        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.surfaceVariant)
        result.subjectSplit.forEach { subject ->
            val colors = bandColors(subject.band)
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
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
        }
    }
}

@Composable
private fun PerformanceTrendCard(snapshot: ChildSnapshot) {
    val trend = snapshot.performanceTrend
    DashboardCard {
        Text("Last 5 tests", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        VSpace(8)
        Sparkline(values = trend.last5Tests, modifier = Modifier.fillMaxWidth().height(64.dp))
        VSpace(8)
        Row(verticalAlignment = Alignment.CenterVertically) {
            TrendIcon(trend.overallTrend)
            HSpace(6)
            Text(
                text = "Overall trend: ${trend.overallTrend.name.lowercase()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun TrendIcon(direction: TrendDirection) {
    Icon(
        Icons.AutoMirrored.Filled.ArrowForward,
        contentDescription = null,
        modifier = Modifier.size(16.dp),
        tint = when (direction) {
            TrendDirection.IMPROVING -> MaterialTheme.colorScheme.tertiary
            TrendDirection.STABLE -> MaterialTheme.colorScheme.secondary
            TrendDirection.DECLINING -> MaterialTheme.colorScheme.error
        },
    )
}

@Composable
private fun StrengthsConcernsCard(snapshot: ChildSnapshot) {
    DashboardCard {
        Text("Doing well", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.tertiary)
        VSpace(6)
        snapshot.strengths.forEach { item ->
            Text("• ${item.label} — ${item.detail}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 4.dp))
        }
        if (snapshot.concerns.isNotEmpty()) {
            VSpace(10)
            Text("Needs attention", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.error)
            VSpace(6)
            snapshot.concerns.forEach { item ->
                Text("• ${item.label} — ${item.detail}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 4.dp))
            }
        }
    }
}

@Composable
private fun TeacherAndPtmCard(snapshot: ChildSnapshot, onAction: (String) -> Unit) {
    DashboardCard {
        val remark = snapshot.teacherRemarks.firstOrNull()
        if (remark != null) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Filled.Forum, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                HSpace(10)
                Column(modifier = Modifier.weight(1f)) {
                    Text("${remark.teacherName} · ${remark.subject}", style = MaterialTheme.typography.titleSmall)
                    Text(remark.remark, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        val ptm = snapshot.upcomingPtm
        if (ptm != null) {
            VSpace(12)
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            VSpace(12)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                HSpace(10)
                Column(modifier = Modifier.weight(1f)) {
                    Text("PTM · ${ptm.dateLabel}, ${ptm.timeLabel}", style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = "${ptm.mode} · ${ptm.agenda}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else if (remark == null) {
            Text(
                "No recent remarks or meetings",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun FeesMiniCard(snapshot: ChildSnapshot, onAction: (String) -> Unit) {
    val fees = snapshot.fees
    DashboardCard(modifier = Modifier.clickable { onAction("Opening fee details") }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.AccountBalanceWallet, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
            HSpace(10)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (fees.due > 0) "₹${fees.due} due" else "Fully paid",
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = if (fees.due > 0) "Next due ${fees.nextDueDateLabel}" else fees.nextDueDateLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        VSpace(10)
        LinearProgressIndicator(
            progress = { (fees.paid.toFloat() / fees.totalFee).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            gapSize = 0.dp,
            drawStopIndicator = {},
        )
        VSpace(6)
        Text(
            text = "₹${fees.paid} paid of ₹${fees.totalFee}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 2200)
@Composable
private fun ParentHomePreview() {
    val dashboard = ParentMockData.dashboard
    RankridgeTheme {
        ParentHomeScreen(
            dashboard = dashboard,
            snapshot = dashboard.childData.getValue(dashboard.children.first().id),
            selectedChildId = dashboard.children.first().id,
            onSelectChild = {},
            onOpenProgress = {},
            onOpenAttendance = {},
            onOpenUpdates = {},
            onAction = {},
        )
    }
}
