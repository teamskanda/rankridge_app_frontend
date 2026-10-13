package com.rankridge.app.ui.teacher.batches

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rankridge.app.ui.common.DashboardCard
import com.rankridge.app.ui.common.MetricTile
import com.rankridge.app.ui.common.VSpace
import com.rankridge.app.ui.teacher.TeacherBatch
import com.rankridge.app.ui.teacher.TeacherDashboard
import com.rankridge.app.ui.teacher.TeacherMockData
import com.rankridge.app.ui.theme.RankridgeTheme

@Composable
fun TeacherBatchesScreen(
    dashboard: TeacherDashboard,
    onOpenBatch: (String) -> Unit,
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
        dashboard.batches.forEach { batch ->
            BatchCard(batch, onClick = { onOpenBatch(batch.id) })
            VSpace(12)
        }
    }
}

@Composable
private fun BatchCard(batch: TeacherBatch, onClick: () -> Unit) {
    DashboardCard(modifier = Modifier.clickable(onClick = onClick)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(batch.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${batch.subject} · ${batch.studentCount} students",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        VSpace(12)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            MetricTile(value = "${batch.syllabusCompletionPercent}%", label = "Syllabus", modifier = Modifier.weight(1f))
            MetricTile(
                value = "${batch.attendanceAveragePercent}%",
                label = "Attendance",
                modifier = Modifier.weight(1f),
                accent = MaterialTheme.colorScheme.secondary,
            )
            MetricTile(
                value = "${batch.latestTestAveragePercent}%",
                label = "Test avg",
                modifier = Modifier.weight(1f),
                accent = MaterialTheme.colorScheme.tertiary,
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 900)
@Composable
private fun TeacherBatchesPreview() {
    RankridgeTheme {
        TeacherBatchesScreen(dashboard = TeacherMockData.dashboard, onOpenBatch = {})
    }
}
