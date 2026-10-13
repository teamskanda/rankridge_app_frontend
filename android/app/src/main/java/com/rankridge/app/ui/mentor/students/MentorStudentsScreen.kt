package com.rankridge.app.ui.mentor.students

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.rankridge.app.ui.common.DashboardCard
import com.rankridge.app.ui.common.EmptyState
import com.rankridge.app.ui.common.SectionHeader
import com.rankridge.app.ui.common.VSpace
import com.rankridge.app.ui.mentor.MenteeRow
import com.rankridge.app.ui.mentor.MentorDashboard
import com.rankridge.app.ui.mentor.MentorMockData
import com.rankridge.app.ui.mentor.RiskLevel
import com.rankridge.app.ui.mentor.label
import com.rankridge.app.ui.mentor.riskColors
import com.rankridge.app.ui.theme.RankridgeTheme

private enum class RiskFilter(val label: String, val level: RiskLevel?) {
    ALL("All", null),
    HIGH("High risk", RiskLevel.HIGH),
    MODERATE("Moderate", RiskLevel.MODERATE),
    LOW("On track", RiskLevel.LOW),
}

@Composable
fun MentorStudentsScreen(
    dashboard: MentorDashboard,
    onOpenStudent: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var filter by remember { mutableStateOf(RiskFilter.ALL) }
    val filtered = dashboard.mentees
        .filter { filter.level == null || it.riskLevel == filter.level }
        .sortedBy { it.riskLevel.ordinal }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 28.dp),
    ) {
        VSpace(16)

        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            RiskFilter.entries.forEach { option ->
                val selected = option == filter
                val colors = option.level?.let { riskColors(it) }
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            when {
                                selected && colors != null -> colors.container
                                selected -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            },
                        )
                        .clickable { filter = option }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = option.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = when {
                            selected && colors != null -> colors.content
                            selected -> MaterialTheme.colorScheme.onPrimary
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                    )
                }
            }
        }

        SectionHeader("${filtered.size} student${if (filtered.size == 1) "" else "s"}")
        if (filtered.isEmpty()) {
            EmptyState(
                icon = Icons.Filled.FilterAltOff,
                title = "No students in this category",
                subtitle = "Try a different filter to see more mentees.",
            )
        } else {
            DashboardCard {
                filtered.forEachIndexed { index, student ->
                    MenteeRow(student = student, onClick = { onOpenStudent(student.id) })
                    if (index != filtered.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 1600)
@Composable
private fun MentorStudentsPreview() {
    RankridgeTheme {
        MentorStudentsScreen(dashboard = MentorMockData.dashboard, onOpenStudent = {})
    }
}
