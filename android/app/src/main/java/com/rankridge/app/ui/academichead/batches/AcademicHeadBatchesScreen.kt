package com.rankridge.app.ui.academichead.batches

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
import com.rankridge.app.ui.academichead.BatchRow
import com.rankridge.app.ui.common.DashboardCard
import com.rankridge.app.ui.common.SectionHeader
import com.rankridge.app.ui.common.VSpace
import com.rankridge.app.ui.theme.RankridgeTheme

@Composable
fun AcademicHeadBatchesScreen(
    dashboard: AcademicHeadDashboard,
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
        SectionHeader("${dashboard.batches.size} batches")
        DashboardCard {
            dashboard.batches.forEachIndexed { index, batch ->
                BatchRow(batch = batch, onClick = { onOpenBatch(batch.id) })
                if (index != dashboard.batches.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 1200)
@Composable
private fun AcademicHeadBatchesPreview() {
    RankridgeTheme {
        AcademicHeadBatchesScreen(dashboard = AcademicHeadMockData.dashboard, onOpenBatch = {})
    }
}
