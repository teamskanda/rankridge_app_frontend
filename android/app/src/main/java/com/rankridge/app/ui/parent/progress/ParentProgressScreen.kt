package com.rankridge.app.ui.parent.progress

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.rankridge.app.ui.common.DashboardCard
import com.rankridge.app.ui.common.HSpace
import com.rankridge.app.ui.common.SectionHeader
import com.rankridge.app.ui.common.Sparkline
import com.rankridge.app.ui.common.StatRow
import com.rankridge.app.ui.common.StatusPill
import com.rankridge.app.ui.common.VSpace
import com.rankridge.app.ui.common.bandColors
import com.rankridge.app.ui.common.label
import com.rankridge.app.ui.parent.ChildSnapshot
import com.rankridge.app.ui.parent.ParentMockData
import com.rankridge.app.ui.parent.StrengthConcern
import com.rankridge.app.ui.parent.SubjectTrend
import com.rankridge.app.ui.parent.TrendDirection
import com.rankridge.app.ui.theme.RankridgeTheme

@Composable
fun ParentProgressScreen(
    snapshot: ChildSnapshot,
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
        LatestTestDetailCard(snapshot)

        SectionHeader("Performance trend")
        PerformanceTrendDetailCard(snapshot)

        SectionHeader("Strengths")
        StrengthListCard(snapshot.strengths, positive = true)

        if (snapshot.concerns.isNotEmpty()) {
            SectionHeader("Needs attention")
            StrengthListCard(snapshot.concerns, positive = false)
        }
    }
}

@Composable
private fun LatestTestDetailCard(snapshot: ChildSnapshot) {
    val result = snapshot.latestTest
    DashboardCard {
        Text(result.testName, style = MaterialTheme.typography.titleMedium)
        Text(result.dateLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        VSpace(12)
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
        VSpace(10)
        StatRow(label = "Percentage", value = "${result.percentage}%")
        StatRow(label = "Percentile", value = "${result.percentile}")
        StatRow(label = "Batch rank", value = "#${result.batchRank} of ${result.batchSize}")
        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.surfaceVariant)
        Text("Subject split", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        VSpace(6)
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
private fun PerformanceTrendDetailCard(snapshot: ChildSnapshot) {
    val trend = snapshot.performanceTrend
    DashboardCard {
        Text("Last 5 tests", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        VSpace(8)
        Sparkline(values = trend.last5Tests, modifier = Modifier.fillMaxWidth().height(72.dp))
        VSpace(14)
        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
        VSpace(10)
        Text("Subject-wise trend", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        VSpace(6)
        trend.subjectTrends.forEach { subjectTrend -> SubjectTrendRow(subjectTrend) }
    }
}

@Composable
private fun SubjectTrendRow(subjectTrend: SubjectTrend) {
    val icon: ImageVector
    val label: String
    val tint = when (subjectTrend.direction) {
        TrendDirection.IMPROVING -> {
            icon = Icons.AutoMirrored.Filled.TrendingUp
            label = "Improving"
            MaterialTheme.colorScheme.tertiary
        }
        TrendDirection.STABLE -> {
            icon = Icons.AutoMirrored.Filled.TrendingFlat
            label = "Stable"
            MaterialTheme.colorScheme.secondary
        }
        TrendDirection.DECLINING -> {
            icon = Icons.AutoMirrored.Filled.TrendingDown
            label = "Declining"
            MaterialTheme.colorScheme.error
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(subjectTrend.subject, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.height(18.dp))
        HSpace(6)
        Text(label, style = MaterialTheme.typography.labelMedium, color = tint)
    }
}

@Composable
private fun StrengthListCard(items: List<StrengthConcern>, positive: Boolean) {
    DashboardCard {
        items.forEachIndexed { index, item ->
            Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(vertical = 6.dp)) {
                Text(
                    text = if (positive) "✓" else "!",
                    style = MaterialTheme.typography.titleSmall,
                    color = if (positive) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
                )
                HSpace(10)
                Column {
                    Text(item.label, style = MaterialTheme.typography.titleSmall)
                    Text(item.detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (index != items.lastIndex) {
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 1800)
@Composable
private fun ParentProgressPreview() {
    val dashboard = ParentMockData.dashboard
    RankridgeTheme {
        ParentProgressScreen(
            snapshot = dashboard.childData.getValue(dashboard.children.first().id),
            onAction = {},
        )
    }
}
