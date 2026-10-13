package com.rankridge.app.ui.counsellor.admissions

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
import com.rankridge.app.ui.common.DashboardCard
import com.rankridge.app.ui.common.SectionHeader
import com.rankridge.app.ui.common.StatusPill
import com.rankridge.app.ui.common.VSpace
import com.rankridge.app.ui.counsellor.AdmissionApplication
import com.rankridge.app.ui.counsellor.ApplicationStatus
import com.rankridge.app.ui.counsellor.CounsellorDashboard
import com.rankridge.app.ui.counsellor.CounsellorMockData
import com.rankridge.app.ui.counsellor.applicationStatusColors
import com.rankridge.app.ui.counsellor.label
import com.rankridge.app.ui.theme.RankridgeTheme

@Composable
fun CounsellorAdmissionsScreen(
    dashboard: CounsellorDashboard,
    modifier: Modifier = Modifier,
) {
    val inProgress = dashboard.applications.filter { it.status != ApplicationStatus.ADMITTED }
    val admitted = dashboard.applications.filter { it.status == ApplicationStatus.ADMITTED }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 28.dp),
    ) {
        VSpace(16)

        SectionHeader("In progress (${inProgress.size})")
        if (inProgress.isEmpty()) {
            DashboardCard {
                Text(
                    "No applications awaiting action",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            DashboardCard {
                inProgress.forEachIndexed { index, application ->
                    ApplicationRow(application)
                    if (index != inProgress.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }

        if (admitted.isNotEmpty()) {
            SectionHeader("Admitted (${admitted.size})")
            DashboardCard {
                admitted.forEachIndexed { index, application ->
                    ApplicationRow(application)
                    if (index != admitted.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@Composable
private fun ApplicationRow(application: AdmissionApplication) {
    val colors = applicationStatusColors(application.status)
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(application.studentName, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = "${application.course} · ${application.branch}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            StatusPill(text = application.status.label(), container = colors.container, content = colors.content)
        }
        VSpace(8)
        Text(
            text = "Submitted ${application.submittedLabel} · ${application.documentsStatus}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = application.feeStatus,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 1400)
@Composable
private fun CounsellorAdmissionsPreview() {
    RankridgeTheme {
        CounsellorAdmissionsScreen(dashboard = CounsellorMockData.dashboard)
    }
}
