package com.rankridge.app.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rankridge.app.ui.theme.MetricNumber
import com.rankridge.app.ui.theme.rankridgeCardBorder
import com.rankridge.app.ui.theme.rankridgeCardColor

/**
 * Qualitative band used to colour subject/topic performance consistently across every dashboard
 * (Student, Parent, and future roles) — shared here rather than duplicated per role.
 */
enum class PerformanceBand { STRONG, GOOD, NEEDS_ATTENTION }

/** Row with a bold section title and an optional trailing text action (e.g. "View all"). */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f),
        )
        if (actionText != null && onActionClick != null) {
            TextButton(onClick = onActionClick) { Text(actionText) }
        }
    }
}

/**
 * The one card style used across every dashboard section.
 *
 * Separation from the page comes from tone first ([rankridgeCardColor]) and a 1dp hairline
 * ([rankridgeCardBorder]) second, with only 1dp of shadow left to seat the card. The previous
 * version painted the card in `surface` — identical to `background` — so a 2dp shadow was doing
 * all the work on its own and the result read as washed out.
 */
@Composable
fun DashboardCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = rankridgeCardColor()),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, rankridgeCardBorder()),
    ) {
        Column(modifier = Modifier.padding(16.dp)) { content() }
    }
}

/** Small rounded pill for statuses, tags and labels. Caller supplies semantic colours. */
@Composable
fun StatusPill(
    text: String,
    container: Color,
    content: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(container)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = content,
        )
    }
}

/** Compact circular subject avatar from the subject's first letter. */
@Composable
fun SubjectAvatar(
    subject: String,
    container: Color,
    content: Color,
    modifier: Modifier = Modifier,
    size: Int = 40,
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(container),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = subject.take(1).uppercase(),
            style = MaterialTheme.typography.titleMedium,
            color = content,
        )
    }
}

/** A labelled value block used in metric grids (big number + caption). */
@Composable
fun MetricTile(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    accent: Color = MaterialTheme.colorScheme.primary,
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(14.dp),
    ) {
        Column {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(20.dp).padding(bottom = 6.dp),
                )
            }
            Text(
                text = value,
                style = MetricNumber,
                color = accent,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** Subject name + horizontal progress bar + percentage, used for syllabus/attendance splits. */
@Composable
fun LabeledProgress(
    label: String,
    percent: Int,
    modifier: Modifier = Modifier,
    trailing: String = "$percent%",
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Column(modifier = modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = trailing,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { (percent / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            // Material 3 1.3 draws a stop dot at the track end and a gap before it. On a short
            // 8dp bar both read as rendering artefacts rather than information, so both are off.
            gapSize = 0.dp,
            drawStopIndicator = {},
        )
    }
}

/**
 * Minimal line sparkline for a short series (e.g. last 5 test scores). Draws a smooth polyline
 * with dots on a fixed 0–100 vertical scale so trends are comparable test to test.
 */
@Composable
fun Sparkline(
    values: List<Int>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
) {
    val fill = lineColor.copy(alpha = 0.12f)
    androidx.compose.foundation.Canvas(modifier = modifier) {
        if (values.size < 2) return@Canvas
        val maxV = 100f
        val minV = 0f
        val stepX = size.width / (values.size - 1)
        val points = values.mapIndexed { i, v ->
            val x = stepX * i
            val y = size.height - ((v - minV) / (maxV - minV)) * size.height
            Offset(x, y)
        }
        val linePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            points.drop(1).forEach { lineTo(it.x, it.y) }
        }
        val areaPath = Path().apply {
            addPath(linePath)
            lineTo(points.last().x, size.height)
            lineTo(points.first().x, size.height)
            close()
        }
        drawPath(areaPath, color = fill)
        drawPath(
            path = linePath,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
        )
        points.forEach { drawCircle(color = lineColor, radius = 3.5.dp.toPx(), center = it) }
    }
}

/** Thin labelled divider-style key-value row. */
@Composable
fun StatRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = valueColor,
        )
    }
}

/**
 * Calm "nothing here" placeholder for filtered lists and empty sections — an icon, a short title
 * and an optional one-line hint, centred inside a [DashboardCard]. Used instead of a bare line of
 * text so an empty filter result still reads as an intentional, designed state.
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    DashboardCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(26.dp),
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
            if (subtitle != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

/** A small coloured legend/strip spacer. */
@Composable
fun VSpace(height: Int) = Spacer(Modifier.height(height.dp))

@Composable
fun HSpace(width: Int) = Spacer(Modifier.width(width.dp))

/**
 * Semantic colour mapping for a [PerformanceBand], reused by every screen so "strong / good /
 * needs attention" always reads the same: emerald (tertiary), aqua (primary), warm error.
 */
data class BandColors(val container: Color, val content: Color, val solid: Color)

@Composable
fun bandColors(band: PerformanceBand): BandColors = when (band) {
    PerformanceBand.STRONG -> BandColors(
        container = MaterialTheme.colorScheme.tertiaryContainer,
        content = MaterialTheme.colorScheme.onTertiaryContainer,
        solid = MaterialTheme.colorScheme.tertiary,
    )
    PerformanceBand.GOOD -> BandColors(
        container = MaterialTheme.colorScheme.primaryContainer,
        content = MaterialTheme.colorScheme.onPrimaryContainer,
        solid = MaterialTheme.colorScheme.primary,
    )
    PerformanceBand.NEEDS_ATTENTION -> BandColors(
        container = MaterialTheme.colorScheme.errorContainer,
        content = MaterialTheme.colorScheme.onErrorContainer,
        solid = MaterialTheme.colorScheme.error,
    )
}

fun PerformanceBand.label(): String = when (this) {
    PerformanceBand.STRONG -> "Strong"
    PerformanceBand.GOOD -> "Good"
    PerformanceBand.NEEDS_ATTENTION -> "Needs attention"
}
