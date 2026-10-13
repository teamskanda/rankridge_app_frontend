package com.rankridge.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rankridge.app.ui.common.SystemBarAppearance
import com.rankridge.app.ui.theme.rankridgeBarColor
import com.rankridge.app.ui.theme.rankridgeBarContentColor

/**
 * Branded top app bar used on every screen (auth and dashboard alike): a flat, light tint of the
 * Aqua Peacock primary — the same fill as the bottom navigation bar — so the header always reads
 * as "RankRidge" rather than a generic Material bar. A soft shadow and rounded bottom corners make
 * it read as a floating header card (the pattern used by Blinkit, Swiggy, PhonePe and most polished
 * consumer/ed-tech apps) instead of a flat strip glued to the content below. An optional [icon]
 * renders beside the title, matching the icon shown for that same section in the bottom nav.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankridgeTopBar(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    SystemBarAppearance(lightIcons = true)
    val contentColor = rankridgeBarContentColor
    val barColor = rankridgeBarColor()
    val shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)

    Box(
        modifier = modifier
            .shadow(elevation = 6.dp, shape = shape, clip = false)
            .clip(shape)
            .background(barColor),
    ) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(Modifier.width(10.dp))
                    }
                    Text(
                        text = title,
                        color = contentColor,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            },
            navigationIcon = navigationIcon,
            actions = actions,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent,
                navigationIconContentColor = contentColor,
                titleContentColor = contentColor,
                actionIconContentColor = contentColor,
            ),
        )
    }
}
