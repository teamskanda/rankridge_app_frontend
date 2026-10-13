package com.rankridge.app.ui.counsellor.leads

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
import com.rankridge.app.ui.counsellor.CounsellorDashboard
import com.rankridge.app.ui.counsellor.CounsellorMockData
import com.rankridge.app.ui.counsellor.LeadRow
import com.rankridge.app.ui.counsellor.LeadStatus
import com.rankridge.app.ui.counsellor.leadStatusColors
import com.rankridge.app.ui.theme.RankridgeTheme

private enum class LeadFilter(val label: String, val status: LeadStatus?) {
    ALL("All", null),
    NEW("New", LeadStatus.NEW),
    FOLLOW_UP("Follow-up", LeadStatus.FOLLOW_UP),
    INTERESTED("Interested", LeadStatus.INTERESTED),
    APPLICATION("Application", LeadStatus.APPLICATION_SUBMITTED),
    ADMITTED("Admitted", LeadStatus.ADMITTED),
    LOST("Lost", LeadStatus.LOST),
}

@Composable
fun CounsellorLeadsScreen(
    dashboard: CounsellorDashboard,
    onOpenLead: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var filter by remember { mutableStateOf(LeadFilter.ALL) }
    val filtered = dashboard.leads
        .filter { filter.status == null || it.status == filter.status }
        .sortedBy { it.status.ordinal }

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
            LeadFilter.entries.forEach { option ->
                val selected = option == filter
                val colors = option.status?.let { leadStatusColors(it) }
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

        SectionHeader("${filtered.size} lead${if (filtered.size == 1) "" else "s"}")
        if (filtered.isEmpty()) {
            EmptyState(
                icon = Icons.Filled.FilterAltOff,
                title = "No leads in this category",
                subtitle = "Try a different filter to see more leads.",
            )
        } else {
            DashboardCard {
                filtered.forEachIndexed { index, lead ->
                    LeadRow(lead = lead, onClick = { onOpenLead(lead.id) })
                    if (index != filtered.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 1800)
@Composable
private fun CounsellorLeadsPreview() {
    RankridgeTheme {
        CounsellorLeadsScreen(dashboard = CounsellorMockData.dashboard, onOpenLead = {})
    }
}
