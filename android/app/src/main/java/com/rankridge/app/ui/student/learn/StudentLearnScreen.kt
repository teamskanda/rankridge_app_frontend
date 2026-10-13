package com.rankridge.app.ui.student.learn

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rankridge.app.ui.student.Bookmark
import com.rankridge.app.ui.student.Course
import com.rankridge.app.ui.student.Doubt
import com.rankridge.app.ui.student.DoubtStatus
import com.rankridge.app.ui.student.DownloadItem
import com.rankridge.app.ui.student.StudentDashboard
import com.rankridge.app.ui.student.StudentMockData
import com.rankridge.app.ui.common.DashboardCard
import com.rankridge.app.ui.common.HSpace
import com.rankridge.app.ui.common.LabeledProgress
import com.rankridge.app.ui.common.SectionHeader
import com.rankridge.app.ui.common.StatusPill
import com.rankridge.app.ui.common.SubjectAvatar
import com.rankridge.app.ui.common.VSpace
import com.rankridge.app.ui.theme.RankridgeTheme

@Composable
fun StudentLearnScreen(
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
        SectionHeader("Resume where you left off")
        DashboardCard(modifier = Modifier.clickable { onAction("Resuming ${dashboard.continueLearning.topic}") }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.PlayCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
                HSpace(12)
                Column(modifier = Modifier.weight(1f)) {
                    Text(dashboard.continueLearning.subject, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Text(dashboard.continueLearning.topic, style = MaterialTheme.typography.titleSmall)
                }
            }
            VSpace(8)
            LabeledProgress(
                label = dashboard.continueLearning.resumeAt,
                percent = dashboard.continueLearning.progressPercent,
            )
        }

        SectionHeader("My courses")
        dashboard.courses.forEach { course ->
            CourseCard(course, onAction)
            VSpace(8)
        }

        SectionHeader("Bookmarks", actionText = "All", onActionClick = { onAction("Opening all bookmarks") })
        DashboardCard {
            dashboard.bookmarks.forEachIndexed { index, bookmark ->
                BookmarkRow(bookmark, onAction)
                if (index != dashboard.bookmarks.lastIndex) {
                    androidx.compose.material3.HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }
        }

        SectionHeader("Downloads")
        DashboardCard {
            dashboard.downloads.forEachIndexed { index, item ->
                DownloadRow(item, onAction)
                if (index != dashboard.downloads.lastIndex) {
                    androidx.compose.material3.HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }
        }

        SectionHeader("Doubt center", actionText = "Ask new", onActionClick = { onAction("Opening doubt composer") })
        dashboard.doubts.forEach { doubt ->
            DoubtCard(doubt, onAction)
            VSpace(8)
        }
        OutlinedButton(
            onClick = { onAction("Opening doubt composer") },
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            HSpace(8)
            Text("Ask a new doubt")
        }
    }
}

@Composable
private fun CourseCard(course: Course, onAction: (String) -> Unit) {
    DashboardCard {
        Text(course.name, style = MaterialTheme.typography.titleSmall)
        VSpace(4)
        Text(
            text = "Course → Subject → Chapter → Topic",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        VSpace(8)
        course.subjects.forEach { subject ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAction("Opening ${subject.name}") }
                    .padding(vertical = 4.dp),
            ) {
                SubjectAvatar(
                    subject = subject.name,
                    container = MaterialTheme.colorScheme.primaryContainer,
                    content = MaterialTheme.colorScheme.onPrimaryContainer,
                    size = 36,
                )
                HSpace(12)
                Column(modifier = Modifier.weight(1f)) {
                    LabeledProgress(
                        label = subject.name,
                        percent = subject.progressPercent,
                        trailing = "${subject.chaptersDone}/${subject.chapters} ch",
                    )
                }
            }
        }
    }
}

@Composable
private fun BookmarkRow(bookmark: Bookmark, onAction: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().clickable { onAction("Opening ${bookmark.title}") },
    ) {
        Icon(Icons.Filled.BookmarkBorder, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
        HSpace(12)
        Column(modifier = Modifier.weight(1f)) {
            Text(bookmark.title, style = MaterialTheme.typography.bodyMedium)
            Text("${bookmark.subject} · ${bookmark.type}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun DownloadRow(item: DownloadItem, onAction: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().clickable {
            onAction(if (item.downloaded) "Opening ${item.title}" else "Downloading ${item.title}")
        },
    ) {
        Icon(
            if (item.downloaded) Icons.Filled.CheckCircle else Icons.Filled.Download,
            contentDescription = null,
            tint = if (item.downloaded) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
        )
        HSpace(12)
        Column(modifier = Modifier.weight(1f)) {
            Text(item.title, style = MaterialTheme.typography.bodyMedium)
            Text("${item.subject} · ${item.sizeLabel}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        StatusPill(
            text = if (item.downloaded) "Saved" else "Download",
            container = if (item.downloaded) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.primaryContainer,
            content = if (item.downloaded) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

@Composable
private fun DoubtCard(doubt: Doubt, onAction: (String) -> Unit) {
    val (container, content) = when (doubt.status) {
        DoubtStatus.RESOLVED -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        DoubtStatus.AWAITING_REPLY -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        DoubtStatus.OPEN -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }
    DashboardCard(modifier = Modifier.clickable { onAction("Opening doubt thread") }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.AutoMirrored.Filled.HelpOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            HSpace(10)
            Text(doubt.subject, style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
            StatusPill(text = doubt.status.label, container = container, content = content)
        }
        VSpace(8)
        Text(doubt.question, style = MaterialTheme.typography.bodyMedium)
        VSpace(4)
        Text(doubt.timeLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 2000)
@Composable
private fun StudentLearnPreview() {
    RankridgeTheme {
        StudentLearnScreen(dashboard = StudentMockData.dashboard, onAction = {})
    }
}
