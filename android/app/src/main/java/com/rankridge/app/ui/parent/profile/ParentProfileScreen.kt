package com.rankridge.app.ui.parent.profile

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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.HorizontalDivider
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
import com.rankridge.app.ui.common.DashboardCard
import com.rankridge.app.ui.common.HSpace
import com.rankridge.app.ui.common.SectionHeader
import com.rankridge.app.ui.common.StatRow
import com.rankridge.app.ui.common.VSpace
import com.rankridge.app.ui.parent.ChildProfile
import com.rankridge.app.ui.parent.ParentDashboard
import com.rankridge.app.ui.parent.ParentMockData
import com.rankridge.app.ui.parent.ParentProfile
import com.rankridge.app.ui.theme.RankridgeTheme

@Composable
fun ParentProfileScreen(
    dashboard: ParentDashboard,
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
        IdentityCard(dashboard.parent)

        SectionHeader("Linked ${if (dashboard.children.size > 1) "children" else "child"}")
        DashboardCard {
            dashboard.children.forEachIndexed { index, child ->
                ChildRow(child) { onAction("Opening ${child.name}'s profile") }
                if (index != dashboard.children.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                }
            }
        }

        SectionHeader("Settings")
        DashboardCard {
            SettingRow(Icons.Filled.Notifications, "Notifications") { onAction("Opening notifications settings") }
            SettingRow(Icons.Filled.Translate, "Language") { onAction("Opening language settings") }
            SettingRow(Icons.Filled.Settings, "Account settings") { onAction("Opening account settings") }
            SettingRow(Icons.AutoMirrored.Filled.HelpOutline, "Help & support", showDivider = false) { onAction("Opening help & support") }
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
private fun IdentityCard(parent: ParentProfile) {
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
                    text = parent.initials,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            HSpace(16)
            Column {
                Text(parent.name, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "+91 ${parent.phone}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ChildRow(child: ChildProfile, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = child.initials,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
        HSpace(14)
        Column(modifier = Modifier.weight(1f)) {
            Text(child.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "${child.classYear} · ${child.batch}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
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
        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 1200)
@Composable
private fun ParentProfilePreview() {
    RankridgeTheme {
        ParentProfileScreen(dashboard = ParentMockData.dashboard, onSignOut = {}, onAction = {})
    }
}
