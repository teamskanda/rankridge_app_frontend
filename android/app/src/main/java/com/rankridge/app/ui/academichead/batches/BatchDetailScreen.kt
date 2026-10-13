package com.rankridge.app.ui.academichead.batches

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rankridge.app.ui.academichead.AcademicHeadMockData
import com.rankridge.app.ui.academichead.BatchPerformance
import com.rankridge.app.ui.common.DashboardCard
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
fun BatchDetailScreen(
    batch: BatchPerformance,
    modifier: Modifier = Modifier,
) {
    val colors = bandColors(batch.band)
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 28.dp),
    ) {
        VSpace(16)

        DashboardCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(batch.course, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(batch.facultyName, style = MaterialTheme.typography.titleSmall)
                }
                StatusPill(text = batch.band.label(), container = colors.container, content = colors.content)
            }
            VSpace(14)
            Row {
                MetricTile(value = "${batch.studentCount}", label = "Students", modifier = Modifier.weight(1f))
                MetricTile(
                    value = "${batch.atRiskCount}",
                    label = "At risk",
                    modifier = Modifier.weight(1f),
                    accent = if (batch.atRiskCount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary,
                )
            }
        }

        SectionHeader("Performance")
        DashboardCard {
            LabeledProgress(label = "Average test score", percent = batch.avgTestScorePercent, color = colors.solid)
            LabeledProgress(label = "Attendance", percent = batch.attendancePercent)
            LabeledProgress(label = "Syllabus completion", percent = batch.syllabusCompletionPercent, color = MaterialTheme.colorScheme.secondary)
            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.surfaceVariant)
            Text("Score trend (last 5 tests)", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            VSpace(8)
            Sparkline(values = batch.scoreTrend, modifier = Modifier.fillMaxWidth().height(72.dp))
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 1400)
@Composable
private fun BatchDetailPreview() {
    RankridgeTheme {
        BatchDetailScreen(batch = AcademicHeadMockData.dashboard.batches.first())
    }
}
