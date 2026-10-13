package com.rankridge.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rankridge.app.ui.common.SystemBarAppearance
import com.rankridge.app.ui.theme.rankridgeBarColor
import com.rankridge.app.ui.theme.rankridgeBarContentColor

/**
 * Personalized header shown only on each dashboard's Home tab — avatar initials, a greeting, a
 * context line, and a notification bell — replacing the generic icon+title bar other tabs use.
 * This is the "personalized home header" pattern used by most polished consumer and ed-tech apps
 * (Blinkit, Swiggy, PhonePe, BYJU'S-style student apps): the landing screen earns a richer header,
 * inner screens stay simple utility bars ([RankridgeTopBar]) so they don't compete with it.
 */
@Composable
fun RankridgeHomeTopBar(
    greeting: String,
    subtitle: String,
    initials: String,
    notificationCount: Int,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SystemBarAppearance(lightIcons = true)
    val contentColor = rankridgeBarContentColor
    val barColor = rankridgeBarColor()
    val shape = RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = shape, clip = false)
            .clip(shape)
            .background(barColor)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(contentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.78f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            BadgedBox(
                badge = {
                    if (notificationCount > 0) {
                        Badge { Text(notificationCount.toString()) }
                    }
                },
            ) {
                IconButton(onClick = onNotificationClick) {
                    Icon(
                        imageVector = Icons.Filled.NotificationsNone,
                        contentDescription = "Notifications",
                        tint = contentColor,
                    )
                }
            }
        }
    }
}
