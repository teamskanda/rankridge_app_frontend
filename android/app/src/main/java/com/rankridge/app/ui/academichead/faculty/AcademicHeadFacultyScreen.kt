package com.rankridge.app.ui.academichead.faculty

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rankridge.app.ui.academichead.AcademicHeadDashboard
import com.rankridge.app.ui.academichead.AcademicHeadMockData
import com.rankridge.app.ui.academichead.FacultyRow
import com.rankridge.app.ui.common.DashboardCard
import com.rankridge.app.ui.common.SectionHeader
import com.rankridge.app.ui.common.VSpace
import com.rankridge.app.ui.theme.RankridgeTheme

@Composable
fun AcademicHeadFacultyScreen(
    dashboard: AcademicHeadDashboard,
    onOpenFaculty: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sorted = dashboard.faculty.sortedBy { it.compliancePercent }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 28.dp),
    ) {
        VSpace(16)
        SectionHeader("${sorted.size} faculty")
        DashboardCard {
            sorted.forEachIndexed { index, faculty ->
                FacultyRow(faculty = faculty, onClick = { onOpenFaculty(faculty.id) })
                if (index != sorted.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 1400)
@Composable
private fun AcademicHeadFacultyPreview() {
    RankridgeTheme {
        AcademicHeadFacultyScreen(dashboard = AcademicHeadMockData.dashboard, onOpenFaculty = {})
    }
}
