package com.rankridge.app.ui.academichead.faculty

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.rankridge.app.ui.academichead.FacultyMember
import com.rankridge.app.ui.common.DashboardCard
import com.rankridge.app.ui.common.SectionHeader
import com.rankridge.app.ui.common.StatRow
import com.rankridge.app.ui.common.StatusPill
import com.rankridge.app.ui.common.VSpace
import com.rankridge.app.ui.common.bandColors
import com.rankridge.app.ui.common.label
import com.rankridge.app.ui.theme.RankridgeTheme

@Composable
fun FacultyDetailScreen(
    faculty: FacultyMember,
    modifier: Modifier = Modifier,
) {
    val colors = bandColors(faculty.band)
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
                    Text(faculty.subject, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(faculty.batchesTaught.joinToString(", "), style = MaterialTheme.typography.titleSmall)
                }
                StatusPill(text = faculty.band.label(), container = colors.container, content = colors.content)
            }
        }

        SectionHeader("Compliance")
        DashboardCard {
            StatRow(label = "Lectures delivered", value = "${faculty.lecturesDelivered} of ${faculty.lecturesPlanned}")
            StatRow(label = "Compliance", value = "${faculty.compliancePercent}%")
            StatRow(
                label = "Delayed lectures",
                value = "${faculty.delayedLectures}",
                valueColor = if (faculty.delayedLectures > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            )
            StatRow(
                label = "Unmarked lectures",
                value = "${faculty.unmarkedLectures}",
                valueColor = if (faculty.unmarkedLectures > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            )
        }

        SectionHeader("Engagement")
        DashboardCard {
            StatRow(label = "PTMs conducted", value = "${faculty.ptmsConducted}")
            StatRow(
                label = "Pending follow-ups",
                value = "${faculty.pendingFollowUps}",
                valueColor = if (faculty.pendingFollowUps > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            )
        }

        SectionHeader("Batches taught")
        DashboardCard {
            faculty.batchesTaught.forEachIndexed { index, batch ->
                Text(batch, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
                if (index != faculty.batchesTaught.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 1600)
@Composable
private fun FacultyDetailPreview() {
    RankridgeTheme {
        FacultyDetailScreen(faculty = AcademicHeadMockData.dashboard.faculty.first())
    }
}
