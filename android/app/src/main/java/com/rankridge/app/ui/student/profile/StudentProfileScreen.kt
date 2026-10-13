package com.rankridge.app.ui.student.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DownloadForOffline
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rankridge.app.R
import com.rankridge.app.ui.student.StudentDashboard
import com.rankridge.app.ui.student.StudentMockData
import com.rankridge.app.ui.student.StudentProfile
import com.rankridge.app.ui.common.DashboardCard
import com.rankridge.app.ui.common.HSpace
import com.rankridge.app.ui.common.SectionHeader
import com.rankridge.app.ui.common.StatRow
import com.rankridge.app.ui.common.VSpace
import com.rankridge.app.ui.theme.RankridgeTheme

@Composable
fun StudentProfileScreen(
    dashboard: StudentDashboard,
    onSignOut: () -> Unit,
    onAction: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp),
    ) {
        VSpace(16)
        IdentityCard(dashboard.profile)

        SectionHeader("Academic")
        DashboardCard {
            StatRow("Course", dashboard.profile.course)
            StatRow("Goal", dashboard.profile.goal)
            StatRow("Batch", dashboard.profile.batch)
            StatRow("Class", dashboard.profile.classYear)
        }

        SectionHeader("Settings")
        DashboardCard {
            SettingRow(Icons.Filled.Notifications, "Notifications") { onAction("Opening notifications settings") }
            SettingRow(Icons.Filled.Translate, "Language") { onAction("Opening language settings") }
            SettingRow(Icons.Filled.DownloadForOffline, "Offline downloads") { onAction("Opening offline downloads") }
            SettingRow(Icons.Filled.Settings, "Account settings") { onAction("Opening account settings") }
            SettingRow(Icons.Filled.HelpOutline, "Help & support", showDivider = false) { onAction("Opening help & support") }
        }

        VSpace(20)
        OutlinedButton(
            onClick = onSignOut,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
            HSpace(8)
            Text(stringResource(R.string.action_sign_out))
        }
    }
}

@Composable
private fun IdentityCard(profile: StudentProfile) {
    DashboardCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = profile.initials,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            HSpace(16)
            Column {
                Text(profile.name, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "${profile.classYear} · ${profile.batch}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    label: String,
    showDivider: Boolean = true,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        HSpace(14)
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
    if (showDivider) {
        androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 1200)
@Composable
private fun StudentProfilePreview() {
    RankridgeTheme {
        StudentProfileScreen(dashboard = StudentMockData.dashboard, onSignOut = {}, onAction = {})
    }
}
