package com.rankridge.app.ui.academichead.home

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rankridge.app.ui.academichead.AcademicHeadDashboard
import com.rankridge.app.ui.academichead.AcademicHeadMockData
import com.rankridge.app.ui.academichead.ComplianceAlertRow
import com.rankridge.app.ui.common.DashboardCard
import com.rankridge.app.ui.common.MetricTile
import com.rankridge.app.ui.common.SectionHeader
import com.rankridge.app.ui.common.VSpace
import com.rankridge.app.ui.theme.RankridgeTheme

@Composable
fun AcademicHeadHomeScreen(
    dashboard: AcademicHeadDashboard,
    onOpenFaculty: () -> Unit,
    onOpenBatches: () -> Unit,
    onResolveAlert: (String) -> Unit,
    onAction: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val ops = dashboard.dailyOps
    val risk = dashboard.riskOverview
    val openAlerts = dashboard.alerts.filter { !it.resolved }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 28.dp),
    ) {
        VSpace(16)

        SectionHeader("Today's academic operations")
        DashboardCard {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricTile(value = "${ops.classesCompleted}/${ops.classesPlanned}", label = "Classes held", modifier = Modifier.weight(1f))
                MetricTile(
                    value = "${ops.attendanceAvgPercent}%",
                    label = "Avg attendance",
                    modifier = Modifier.weight(1f),
                    accent = MaterialTheme.colorScheme.secondary,
                )
                MetricTile(
                    value = "${ops.syllabusOnTrackPercent}%",
                    label = "Syllabus on track",
                    modifier = Modifier.weight(1f),
                    accent = MaterialTheme.colorScheme.tertiary,
                )
            }
            if (ops.classesCancelled > 0) {
                VSpace(10)
                Text(
                    text = "${ops.classesCancelled} class${if (ops.classesCancelled == 1) "" else "es"} cancelled today",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }

        SectionHeader("Student risk overview")
        DashboardCard {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricTile(
                    value = "${risk.highCount}",
                    label = "High risk",
                    modifier = Modifier.weight(1f),
                    accent = MaterialTheme.colorScheme.error,
                )
                MetricTile(
                    value = "${risk.moderateCount}",
                    label = "Moderate risk",
                    modifier = Modifier.weight(1f),
                    accent = MaterialTheme.colorScheme.secondary,
                )
                MetricTile(
                    value = "${risk.openInterventions}",
                    label = "Open interventions",
                    modifier = Modifier.weight(1f),
                )
            }
            VSpace(10)
            Text("Top risk reasons", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            VSpace(4)
            risk.topReasons.forEach { reason ->
                Text("• $reason", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 2.dp))
            }
        }

        if (openAlerts.isNotEmpty()) {
            SectionHeader("Compliance alerts (${openAlerts.size})")
            DashboardCard {
                openAlerts.forEachIndexed { index, alert ->
                    ComplianceAlertRow(alert = alert, onResolve = onResolveAlert)
                    if (index != openAlerts.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }

        SectionHeader("Faculty needing support", actionText = "All faculty", onActionClick = onOpenFaculty)
        DashboardCard {
            val needsSupport = dashboard.faculty.filter { it.compliancePercent < 85 }.sortedBy { it.compliancePercent }
            if (needsSupport.isEmpty()) {
                Text(
                    "All faculty are meeting compliance targets",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                needsSupport.forEachIndexed { index, faculty ->
                    Text(faculty.name, style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = "${faculty.subject} · ${faculty.compliancePercent}% compliance · ${faculty.unmarkedLectures} unmarked",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (index != needsSupport.lastIndex) VSpace(10)
                }
            }
        }

        SectionHeader("Batch performance", actionText = "All batches", onActionClick = onOpenBatches)
        DashboardCard {
            dashboard.batches.sortedBy { it.avgTestScorePercent }.forEachIndexed { index, batch ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(batch.name, style = MaterialTheme.typography.titleSmall)
                        Text(
                            text = "${batch.avgTestScorePercent}% avg score · ${batch.attendancePercent}% attendance",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (batch.atRiskCount > 0) {
                        Text(
                            text = "${batch.atRiskCount} at risk",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
                if (index != dashboard.batches.lastIndex) VSpace(10)
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 2400)
@Composable
private fun AcademicHeadHomePreview() {
    RankridgeTheme {
        AcademicHeadHomeScreen(
            dashboard = AcademicHeadMockData.dashboard,
            onOpenFaculty = {},
            onOpenBatches = {},
            onResolveAlert = {},
            onAction = {},
        )
    }
}
