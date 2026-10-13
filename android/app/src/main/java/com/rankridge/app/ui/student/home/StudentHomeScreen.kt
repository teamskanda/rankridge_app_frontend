package com.rankridge.app.ui.student.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DownloadForOffline
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rankridge.app.ui.student.Announcement
import com.rankridge.app.ui.student.ClassMode
import com.rankridge.app.ui.student.ContinueLearning
import com.rankridge.app.ui.student.LatestResult
import com.rankridge.app.ui.student.LeaderboardEntry
import com.rankridge.app.ui.student.PerformanceOverview
import com.rankridge.app.ui.student.RecommendedPractice
import com.rankridge.app.ui.student.StudentDashboard
import com.rankridge.app.ui.student.StudentMockData
import com.rankridge.app.ui.student.StudyStreak
import com.rankridge.app.ui.student.TodayClass
import com.rankridge.app.ui.student.UpcomingTest
import com.rankridge.app.ui.common.DashboardCard
import com.rankridge.app.ui.common.HSpace
import com.rankridge.app.ui.common.MetricTile
import com.rankridge.app.ui.common.SectionHeader
import com.rankridge.app.ui.common.Sparkline
import com.rankridge.app.ui.common.StatRow
import com.rankridge.app.ui.common.StatusPill
import com.rankridge.app.ui.common.SubjectAvatar
import com.rankridge.app.ui.common.VSpace
import com.rankridge.app.ui.common.bandColors
import com.rankridge.app.ui.common.label
import com.rankridge.app.ui.theme.RankridgeTheme

@Composable
fun StudentHomeScreen(
    dashboard: StudentDashboard,
    onOpenTests: () -> Unit,
    onOpenPerformance: () -> Unit,
    onOpenLearn: () -> Unit,
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
        QuickActions(onAction)

        StudyStreakCard(dashboard.studyStreak, onAction)

        SectionHeader("Today's plan")
        TodayPlanCard(dashboard.todayClasses, dashboard.pending.size, onAction)

        SectionHeader("Continue learning", actionText = "My courses", onActionClick = onOpenLearn)
        ContinueLearningCard(dashboard.continueLearning, onAction)

        SectionHeader("Upcoming test", actionText = "All tests", onActionClick = onOpenTests)
        UpcomingTestCard(dashboard.upcomingTests.first(), onAction)

        SectionHeader("Latest result", actionText = "Details", onActionClick = onOpenPerformance)
        LatestResultCard(dashboard.latestResult, onOpenPerformance)

        SectionHeader("Performance", actionText = "More", onActionClick = onOpenPerformance)
        PerformanceOverviewCard(dashboard.overview)

        SectionHeader("Batch leaderboard", actionText = "Full", onActionClick = { onAction("Opening full leaderboard") })
        LeaderboardCard(dashboard.leaderboard)

        SectionHeader("Recommended practice")
        dashboard.recommendedPractice.forEach { practice ->
            RecommendedPracticeCard(practice, onAction)
            VSpace(8)
        }

        SectionHeader("Attendance", actionText = "View full", onActionClick = onOpenPerformance)
        AttendanceMiniCard(
            monthPercent = dashboard.attendance.monthPercent,
            attended = dashboard.attendance.attended,
            total = dashboard.attendance.total,
        )

        SectionHeader("Announcements", actionText = "All", onActionClick = { onAction("Opening all announcements") })
        dashboard.announcements.forEach { announcement ->
            AnnouncementCard(announcement, onAction)
            VSpace(8)
        }
    }
}

@Composable
private fun QuickActions(onAction: (String) -> Unit) {
    val actions = listOf(
        Triple(Icons.Filled.VideoCall, "Join class", "Joining next class"),
        Triple(Icons.AutoMirrored.Filled.HelpOutline, "Ask doubt", "Opening doubt composer"),
        Triple(Icons.Filled.Quiz, "Practice", "Opening practice"),
        Triple(Icons.Filled.CalendarMonth, "Timetable", "Opening timetable"),
        Triple(Icons.Filled.BookmarkBorder, "Bookmarks", "Opening bookmarks"),
        Triple(Icons.Filled.DownloadForOffline, "Downloads", "Opening downloads"),
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        actions.forEach { (icon, label, msg) ->
            QuickActionChip(icon, label) { onAction(msg) }
        }
    }
}

@Composable
private fun QuickActionChip(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(78.dp).clip(MaterialTheme.shapes.medium).clickable(onClick = onClick).padding(vertical = 4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
        VSpace(6)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
        )
    }
}

@Composable
private fun StudyStreakCard(streak: StudyStreak, onAction: (String) -> Unit) {
    val days = listOf("M", "T", "W", "T", "F", "S", "S")
    DashboardCard(modifier = Modifier.padding(top = 20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.LocalFireDepartment,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(28.dp),
            )
            HSpace(10)
            Column(modifier = Modifier.weight(1f)) {
                Text("${streak.currentDays}-day streak", style = MaterialTheme.typography.titleSmall)
                Text(
                    text = "${streak.doneMinutes}/${streak.dailyGoalMinutes} min of today's goal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = "${(streak.doneMinutes * 100 / streak.dailyGoalMinutes)}%",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
        VSpace(12)
        androidx.compose.material3.LinearProgressIndicator(
            progress = { (streak.doneMinutes.toFloat() / streak.dailyGoalMinutes).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            gapSize = 0.dp,
            drawStopIndicator = {},
        )
        VSpace(12)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            streak.weekProgress.forEachIndexed { i, met ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(if (met) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (met) {
                            Icon(
                                Icons.Filled.LocalFireDepartment,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onTertiary,
                                modifier = Modifier.size(14.dp),
                            )
                        }
                    }
                    VSpace(4)
                    Text(days[i], style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun TodayPlanCard(classes: List<TodayClass>, pendingCount: Int, onAction: (String) -> Unit) {
    DashboardCard {
        classes.forEachIndexed { index, item ->
            ClassRow(item, onAction)
            if (index != classes.lastIndex) {
                androidx.compose.material3.HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }
        if (pendingCount > 0) {
            VSpace(12)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .clickable { onAction("Opening pending work") }
                    .padding(12.dp),
            ) {
                Text(
                    text = "$pendingCount pending items — DPP & assignments due",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
    }
}

@Composable
private fun ClassRow(item: TodayClass, onAction: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        SubjectAvatar(
            subject = item.subject,
            container = MaterialTheme.colorScheme.primaryContainer,
            content = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        HSpace(12)
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(item.subject, style = MaterialTheme.typography.titleSmall)
                if (item.isNext) {
                    HSpace(8)
                    StatusPill(
                        text = "Next",
                        container = MaterialTheme.colorScheme.primary,
                        content = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
            Text(
                text = item.topic,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            VSpace(4)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                HSpace(4)
                Text(
                    text = "${item.time} · ${item.location} · ${item.mode.label}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (item.isNext) {
            val isOnline = item.mode == ClassMode.ONLINE
            // Solid primary, not a gold tonal button. This is the single most important action
            // on the home screen, and gold is already carrying the "pending work" warning below —
            // two different jobs were reading as the same colour.
            Button(onClick = { onAction(if (isOnline) "Joining ${item.subject} class" else "Opening ${item.subject} class") }) {
                Text(if (isOnline) "Join" else "Open")
            }
        }
    }
}

@Composable
private fun ContinueLearningCard(data: ContinueLearning, onAction: (String) -> Unit) {
    DashboardCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.PlayCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(44.dp),
            )
            HSpace(12)
            Column(modifier = Modifier.weight(1f)) {
                Text(data.subject, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Text(data.topic, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = data.resumeAt,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = "${data.progressPercent}%",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        VSpace(12)
        androidx.compose.material3.LinearProgressIndicator(
            progress = { data.progressPercent / 100f },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            gapSize = 0.dp,
            drawStopIndicator = {},
        )
        VSpace(12)
        Button(onClick = { onAction("Resuming ${data.topic}") }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.PlayCircle, contentDescription = null, modifier = Modifier.size(18.dp))
            HSpace(8)
            Text("Resume lecture")
        }
    }
}

@Composable
private fun UpcomingTestCard(test: UpcomingTest, onAction: (String) -> Unit) {
    DashboardCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.CalendarMonth,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(40.dp),
            )
            HSpace(12)
            Column(modifier = Modifier.weight(1f)) {
                Text(test.name, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = test.dateLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        VSpace(10)
        Text(
            text = "Syllabus: ${test.syllabus}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        VSpace(4)
        Text(
            text = "${test.durationLabel} · ${test.totalMarks} marks",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        VSpace(12)
        OutlinedButton(onClick = { onAction("Reminder set for ${test.name}") }, modifier = Modifier.fillMaxWidth()) {
            Text("Set reminder")
        }
    }
}

@Composable
private fun LatestResultCard(result: LatestResult, onOpenDetails: () -> Unit) {
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
        Row {
            StatRow(label = "Batch rank", value = "#${result.batchRank} / ${result.batchSize}", modifier = Modifier.weight(1f))
            HSpace(16)
            StatRow(label = "Overall rank", value = "#${result.overallRank}", modifier = Modifier.weight(1f))
        }
        androidx.compose.material3.HorizontalDivider(
            modifier = Modifier.padding(vertical = 10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
        )
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
private fun PerformanceOverviewCard(overview: PerformanceOverview) {
    DashboardCard {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricTile(value = "${overview.averageScorePercent}%", label = "Avg score", modifier = Modifier.weight(1f))
            MetricTile(value = "${overview.testsAttempted}", label = "Tests", modifier = Modifier.weight(1f), accent = MaterialTheme.colorScheme.secondary)
            MetricTile(value = "${overview.attendancePercent}%", label = "Attendance", modifier = Modifier.weight(1f), accent = MaterialTheme.colorScheme.tertiary)
        }
        VSpace(16)
        Text("Last 5 tests", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        VSpace(8)
        Sparkline(
            values = overview.last5Trend,
            modifier = Modifier.fillMaxWidth().height(64.dp),
        )
        VSpace(8)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.tertiary,
            )
            HSpace(6)
            Text(
                text = "Trend: ${overview.trend.name.lowercase()} · ${overview.watchTimeHours}h watch time",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun LeaderboardCard(entries: List<LeaderboardEntry>) {
    DashboardCard {
        entries.forEachIndexed { index, entry ->
            val highlight = entry.isMe
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .background(if (highlight) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else Color.Transparent)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (entry.rank <= 3) {
                    Icon(
                        Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = when (entry.rank) {
                            1 -> MaterialTheme.colorScheme.secondary
                            2 -> MaterialTheme.colorScheme.onSurfaceVariant
                            else -> MaterialTheme.colorScheme.tertiary
                        },
                        modifier = Modifier.size(22.dp),
                    )
                } else {
                    Box(modifier = Modifier.size(22.dp), contentAlignment = Alignment.Center) {
                        Text("${entry.rank}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                HSpace(12)
                Text(
                    text = if (highlight) "${entry.name} (You)" else entry.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "${entry.scorePercent}%",
                    style = MaterialTheme.typography.titleSmall,
                    color = if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                )
            }
            if (index != entries.lastIndex) {
                androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
private fun RecommendedPracticeCard(practice: RecommendedPractice, onAction: (String) -> Unit) {
    DashboardCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SubjectAvatar(
                subject = practice.subject,
                container = MaterialTheme.colorScheme.tertiaryContainer,
                content = MaterialTheme.colorScheme.onTertiaryContainer,
            )
            HSpace(12)
            Column(modifier = Modifier.weight(1f)) {
                Text(practice.topic, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = "${practice.subject} · ${practice.questionCount} Qs · ${practice.difficulty.label}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = practice.reason,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }
            FilledTonalButton(onClick = { onAction("Starting practice: ${practice.topic}") }) { Text("Start") }
        }
    }
}

@Composable
private fun AttendanceMiniCard(monthPercent: Int, attended: Int, total: Int) {
    DashboardCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { monthPercent / 100f },
                    modifier = Modifier.size(64.dp),
                    strokeWidth = 7.dp,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                Text("$monthPercent%", style = MaterialTheme.typography.titleSmall)
            }
            HSpace(16)
            Column {
                Text("This month", style = MaterialTheme.typography.titleSmall)
                Text(
                    text = "$attended of $total classes attended",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun AnnouncementCard(announcement: Announcement, onAction: (String) -> Unit) {
    DashboardCard(modifier = Modifier.clickable { onAction("Opening: ${announcement.title}") }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusPill(
                text = announcement.category,
                container = MaterialTheme.colorScheme.secondaryContainer,
                content = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = announcement.timeLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        VSpace(8)
        Text(announcement.title, style = MaterialTheme.typography.titleSmall)
        Text(
            text = announcement.body,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 2800)
@Composable
private fun StudentHomePreview() {
    RankridgeTheme {
        StudentHomeScreen(
            dashboard = StudentMockData.dashboard,
            onOpenTests = {},
            onOpenPerformance = {},
            onOpenLearn = {},
            onAction = {},
        )
    }
}
