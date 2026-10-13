package com.rankridge.app.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rankridge.app.R
import com.rankridge.app.ui.common.DashboardCard
import com.rankridge.app.ui.common.VSpace
import com.rankridge.app.ui.components.RankridgeTopBar
import com.rankridge.app.ui.theme.RankridgeTheme

/**
 * Where an Admin sign-in lands.
 *
 * Admin is deliberately a web dashboard, not a mobile one: it is built around tables, filters,
 * exports and bulk actions, none of which survive a phone screen honestly. Previously the Admin
 * role fell through to the Student dashboard, which showed an administrator somebody else's
 * homework as if it were their own console. This screen says plainly where the console is and
 * what is waiting there, so the sign-in still ends somewhere deliberate.
 */
@Composable
fun AdminConsoleScreen(
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { RankridgeTopBar(title = "Admin", icon = Icons.Filled.Shield) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.widthIn(max = 480.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                VSpace(32)
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Computer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(42.dp),
                    )
                }

                VSpace(20)
                Text(
                    text = stringResource(R.string.admin_console_title),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                )
                VSpace(8)
                Text(
                    text = stringResource(R.string.admin_console_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )

                VSpace(20)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    Text(
                        text = stringResource(R.string.admin_console_url),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                    )
                }

                VSpace(28)
                DashboardCard {
                    Text(
                        text = stringResource(R.string.admin_console_waiting),
                        style = MaterialTheme.typography.titleSmall,
                    )
                    VSpace(4)
                    AdminCapability(
                        icon = Icons.Filled.Groups,
                        title = "People and access",
                        detail = "Students, faculty, parents, roles and permissions, audit logs",
                    )
                    AdminCapability(
                        icon = Icons.Filled.MenuBook,
                        title = "Academic operations",
                        detail = "Courses, batches, timetable, attendance, tests, results, content",
                    )
                    AdminCapability(
                        icon = Icons.Filled.Business,
                        title = "Business operations",
                        detail = "Admissions, fees and receipts, support tickets, reports and exports",
                    )
                    AdminCapability(
                        icon = Icons.Filled.Shield,
                        title = "Governance",
                        detail = "Permission matrix, branch data visibility, activity logs, settings",
                    )
                }

                VSpace(28)
                OutlinedButton(
                    onClick = onSignOut,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = stringResource(R.string.action_sign_out),
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
        }
    }
}

/** One "what the console does" line: icon, heading, and a one-line detail beneath it. */
@Composable
private fun AdminCapability(
    icon: ImageVector,
    title: String,
    detail: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp),
            )
        }
        Column(
            modifier = Modifier.padding(start = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = detail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 1100)
@Composable
private fun AdminConsolePreview() {
    RankridgeTheme {
        AdminConsoleScreen(onSignOut = {})
    }
}
