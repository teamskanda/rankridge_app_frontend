package com.rankridge.app.ui.parent.attendance

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material3.CircularProgressIndicator
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
import com.rankridge.app.ui.common.LabeledProgress
import com.rankridge.app.ui.common.SectionHeader
import com.rankridge.app.ui.common.VSpace
import com.rankridge.app.ui.parent.AttendanceOverview
import com.rankridge.app.ui.parent.ChildSnapshot
import com.rankridge.app.ui.parent.ParentMockData
import com.rankridge.app.ui.parent.TrendDirection
import com.rankridge.app.ui.theme.RankridgeTheme

@Composable
fun ParentAttendanceScreen(
    snapshot: ChildSnapshot,
    onAction: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val attendance = snapshot.attendance
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 28.dp),
    ) {
        VSpace(16)
        MonthSummaryCard(attendance)

        SectionHeader("Subject-wise")
        DashboardCard {
            attendance.subjectWise.forEach { subject ->
                LabeledProgress(label = subject.subject, percent = subject.percent)
            }
        }

        if (attendance.recentAbsences.isNotEmpty()) {
            SectionHeader("Recent absences")
            DashboardCard {
                attendance.recentAbsences.forEachIndexed { index, note ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                        Icon(Icons.Filled.EventBusy, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        HSpace(10)
                        Text(note, style = MaterialTheme.typography.bodyMedium)
                    }
                    if (index != attendance.recentAbsences.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthSummaryCard(attendance: AttendanceOverview) {
    DashboardCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { attendance.monthPercent / 100f },
                    modifier = Modifier.size(72.dp),
                    strokeWidth = 7.dp,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                Text("${attendance.monthPercent}%", style = MaterialTheme.typography.titleMedium)
            }
            HSpace(18)
            Column {
                Text(attendance.monthLabel, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${attendance.present} of ${attendance.total} classes attended",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "Trend: ${attendance.trend.readable()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        VSpace(14)
        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
        VSpace(14)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween) {
            AttendanceStat(label = "Present", value = attendance.present, color = MaterialTheme.colorScheme.tertiary)
            AttendanceStat(label = "Absent", value = attendance.absent, color = MaterialTheme.colorScheme.error)
            AttendanceStat(label = "Late", value = attendance.late, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
private fun AttendanceStat(label: String, value: Int, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("$value", style = MaterialTheme.typography.titleLarge, color = color)
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun TrendDirection.readable(): String = when (this) {
    TrendDirection.IMPROVING -> "Improving"
    TrendDirection.STABLE -> "Stable"
    TrendDirection.DECLINING -> "Declining"
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 1400)
@Composable
private fun ParentAttendancePreview() {
    val dashboard = ParentMockData.dashboard
    RankridgeTheme {
        ParentAttendanceScreen(
            snapshot = dashboard.childData.getValue(dashboard.children.first().id),
            onAction = {},
        )
    }
}
