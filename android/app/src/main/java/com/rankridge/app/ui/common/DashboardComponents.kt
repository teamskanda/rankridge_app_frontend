package com.rankridge.app.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
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

/** Circular initials avatar used as the leading element of an [EntityRow]. */
@Composable
fun InitialsAvatar(
    initials: String,
    container: Color,
    content: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(container),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = initials, style = MaterialTheme.typography.titleSmall, color = content)
    }
}

/**
 * A labelled value block used in metric grids (big number + caption).
 *
 * The value and the label each sit in a box whose height is reserved from that text style's own
 * line height, with the text bottom-aligned inside it. [AutoShrinkText] shrinks a value that
 * would not otherwise fit its tile, and without a reserved box that shorter line pulled the label
 * up with it — which is why "81% / Syllabus" sat several dp above "3 / Today" in the same row.
 * Reserving the full line height regardless of the size actually rendered keeps every tile in a
 * row sitting on the same two lines.
 *
 * Prefer [MetricRow] over hand-rolling a Row of these: it caps how many share one line.
 */
@Composable
fun MetricTile(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    accent: Color = MaterialTheme.colorScheme.primary,
) {
    val labelStyle = MaterialTheme.typography.labelMedium
    val valueHeight = reservedLineHeight(MetricNumber)
    val labelHeight = reservedLineHeight(labelStyle)
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(12.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(20.dp).padding(bottom = 6.dp),
                )
            }
            // A metric is a single token — "81%" must never become "81" over "%". It shrinks to
            // fit instead of wrapping, which is what a narrow tile was previously forcing.
            Box(
                modifier = Modifier.fillMaxWidth().height(valueHeight),
                contentAlignment = Alignment.BottomStart,
            ) {
                AutoShrinkText(text = value, style = MetricNumber, color = accent)
            }
            Spacer(Modifier.height(2.dp))
            Box(
                modifier = Modifier.fillMaxWidth().height(labelHeight),
                contentAlignment = Alignment.BottomStart,
            ) {
                AutoShrinkText(
                    text = label,
                    style = labelStyle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    minScale = 0.7f,
                )
            }
        }
    }
}

/** Height to reserve for one line of [style], falling back when the style sets no line height. */
@Composable
private fun reservedLineHeight(style: TextStyle) = with(LocalDensity.current) {
    if (style.lineHeight.isSpecified) style.lineHeight.toDp() else style.fontSize.toDp() * 1.4f
}

/** One tile in a [MetricRow]. [accent] falls back to the theme primary when left null. */
data class Metric(
    val value: String,
    val label: String,
    val accent: Color? = null,
    val icon: ImageVector? = null,
)

/**
 * Lays [MetricTile]s out in lines of at most three, wrapping onto further lines as needed.
 *
 * Four tiles across a 360dp phone leave each one roughly 42dp of usable text width — narrower
 * than the word "Syllabus". Each tile then shrinks its own text by a different amount and the row
 * stops reading as one set of numbers. Wrapping four into 2x2 gives each tile about 128dp, which
 * fits the real labels at their intended size, so nothing has to shrink at all.
 */
@Composable
fun MetricRow(
    metrics: List<Metric>,
    modifier: Modifier = Modifier,
) {
    if (metrics.isEmpty()) return
    val perLine = if (metrics.size == 4) 2 else minOf(metrics.size, 3)
    val primary = MaterialTheme.colorScheme.primary
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        metrics.chunked(perLine).forEach { line ->
            Row(
                // Intrinsic min height plus fillMaxHeight makes every tile on a line as tall as
                // the tallest, so one wrapped label never leaves its neighbours short.
                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                line.forEach { metric ->
                    MetricTile(
                        value = metric.value,
                        label = metric.label,
                        icon = metric.icon,
                        accent = metric.accent ?: primary,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                    )
                }
                // A short final line keeps the column widths of the lines above it, rather than
                // letting one leftover tile stretch across the whole card.
                repeat(perLine - line.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

/**
 * The one list-row style shared by every "person or group you can open" list — students,
 * mentees, leads, faculty, batches.
 *
 * The status pill sits under the title rather than beside it. In the previous side-by-side
 * layout the pill was measured first and the title column got whatever was left, so a wide pill
 * ("68% compliance", "Moderate risk") squeezed the name into two broken lines on a 360dp phone.
 * Here the title always gets the full row width, and the pill reads as what it is: a property of
 * the row, not a competing column.
 */
@Composable
fun EntityRow(
    title: String,
    subtitle: String,
    pillText: String,
    pillContainer: Color,
    pillContent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null,
    titleDotColor: Color? = null,
    note: String? = null,
    noteColor: Color = Color.Unspecified,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leading != null) {
            leading()
            HSpace(14)
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (titleDotColor != null) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(titleDotColor),
                    )
                    HSpace(6)
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusPill(text = pillText, container = pillContainer, content = pillContent)
                if (note != null) {
                    HSpace(8)
                    Text(
                        text = note,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (noteColor == Color.Unspecified) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            noteColor
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        HSpace(8)
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * Single-line text that steps its own font size down until it fits the width it is given.
 *
 * Compose wraps by character once a container is narrower than a single word, which turns a
 * metric like "81%" into two stacked lines. Forcing one line with [softWrap] = false alone would
 * clip instead. Measuring, shrinking and re-measuring keeps the value whole and legible at any
 * screen width or user font scale, down to [minScale] of the original size.
 *
 * Drawing is held back until a size that fits is found, so the shrink steps are never visible.
 */
@Composable
fun AutoShrinkText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    minScale: Float = 0.6f,
) {
    var resolved by remember(text, style) { mutableStateOf(style) }
    var measured by remember(text, style) { mutableStateOf(false) }
    Text(
        text = text,
        style = resolved,
        color = color,
        maxLines = 1,
        softWrap = false,
        modifier = modifier.drawWithContent { if (measured) drawContent() },
        onTextLayout = { result ->
            if (result.didOverflowWidth && resolved.fontSize > style.fontSize * minScale) {
                resolved = resolved.copy(fontSize = resolved.fontSize * 0.92f)
            } else {
                measured = true
            }
        },
    )
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
