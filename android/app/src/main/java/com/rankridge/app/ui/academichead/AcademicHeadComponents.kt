package com.rankridge.app.ui.academichead

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rankridge.app.ui.common.EntityRow
import com.rankridge.app.ui.common.HSpace
import com.rankridge.app.ui.common.InitialsAvatar
import com.rankridge.app.ui.common.StatusPill
import com.rankridge.app.ui.common.bandColors
import com.rankridge.app.ui.common.label

/** Colour + label mapping for [AlertSeverity], shared by the Home and alert list screens. */
data class SeverityColors(val container: Color, val content: Color)

@Composable
fun severityColors(severity: AlertSeverity): SeverityColors = when (severity) {
    AlertSeverity.HIGH -> SeverityColors(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer)
    AlertSeverity.MEDIUM -> SeverityColors(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
    AlertSeverity.LOW -> SeverityColors(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
}

fun AlertSeverity.label(): String = when (this) {
    AlertSeverity.HIGH -> "High"
    AlertSeverity.MEDIUM -> "Medium"
    AlertSeverity.LOW -> "Low"
}

fun ComplianceAlertType.icon() = when (this) {
    ComplianceAlertType.UNMARKED_LECTURE -> Icons.Filled.EventBusy
    ComplianceAlertType.DELAYED_BATCH -> Icons.AutoMirrored.Filled.TrendingDown
    ComplianceAlertType.LOW_ATTENDANCE -> Icons.Filled.PersonOff
    ComplianceAlertType.OVERDUE_PTM -> Icons.Filled.EventNote
    ComplianceAlertType.PENDING_FOLLOWUP -> Icons.Filled.Flag
}

/** One faculty row: avatar, name/subject, compliance pill, tap to open the faculty detail screen. */
@Composable
fun FacultyRow(
    faculty: FacultyMember,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = bandColors(faculty.band)
    EntityRow(
        title = faculty.name,
        subtitle = "${faculty.subject} · ${faculty.batchesTaught.joinToString(", ")}",
        pillText = "${faculty.compliancePercent}% compliance",
        pillContainer = colors.container,
        pillContent = colors.content,
        note = if (faculty.unmarkedLectures > 0) "${faculty.unmarkedLectures} unmarked" else null,
        noteColor = MaterialTheme.colorScheme.error,
        onClick = onClick,
        modifier = modifier,
        leading = { InitialsAvatar(faculty.initials, colors.container, colors.content) },
    )
}

/** One batch row: name/course, at-risk badge, band pill, tap to open the batch detail screen. */
@Composable
fun BatchRow(
    batch: BatchPerformance,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = bandColors(batch.band)
    EntityRow(
        title = batch.name,
        subtitle = "${batch.course} · ${batch.studentCount} students",
        pillText = batch.band.label(),
        pillContainer = colors.container,
        pillContent = colors.content,
        note = if (batch.atRiskCount > 0) "${batch.atRiskCount} at risk" else null,
        noteColor = MaterialTheme.colorScheme.error,
        onClick = onClick,
        modifier = modifier,
    )
}

/** One compliance alert row with severity pill and an optional Resolve action. */
@Composable
fun ComplianceAlertRow(
    alert: ComplianceAlert,
    onResolve: ((String) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val colors = severityColors(alert.severity)
    // Severity, title and detail run the full width; the resolve action sits on its own line
    // underneath. As a trailing button it took about a third of the row, so an alert title
    // ("3 unmarked lectures") wrapped and its detail sentence was cut off mid-word.
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            alert.type.icon(),
            contentDescription = null,
            tint = colors.content,
            modifier = Modifier.size(20.dp).padding(top = 2.dp),
        )
        HSpace(10)
        Column(modifier = Modifier.weight(1f)) {
            StatusPill(text = alert.severity.label(), container = colors.container, content = colors.content)
            Spacer(Modifier.size(6.dp))
            Text(alert.title, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.size(4.dp))
            Text(
                text = alert.detail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "${alert.relatedName} · ${alert.dateLabel}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (onResolve != null && !alert.resolved) {
                androidx.compose.material3.TextButton(
                    onClick = { onResolve(alert.id) },
                    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 4.dp),
                ) {
                    Text("Resolve")
                }
            } else if (alert.resolved) {
                Spacer(Modifier.size(6.dp))
                Text(
                    text = "Resolved",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }
        }
    }
}
