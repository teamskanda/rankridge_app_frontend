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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rankridge.app.ui.common.HSpace
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
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(colors.container),
            contentAlignment = Alignment.Center,
        ) {
            Text(faculty.initials, style = MaterialTheme.typography.titleSmall, color = colors.content)
        }
        HSpace(14)
        Column(modifier = Modifier.weight(1f)) {
            Text(faculty.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "${faculty.subject} · ${faculty.batchesTaught.joinToString(", ")}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            StatusPill(text = "${faculty.compliancePercent}% compliance", container = colors.container, content = colors.content)
            if (faculty.unmarkedLectures > 0) {
                Spacer(Modifier.size(4.dp))
                Text(
                    text = "${faculty.unmarkedLectures} unmarked",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
        HSpace(4)
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** One batch row: name/course, at-risk badge, band pill, tap to open the batch detail screen. */
@Composable
fun BatchRow(
    batch: BatchPerformance,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = bandColors(batch.band)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(batch.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "${batch.course} · ${batch.studentCount} students",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            StatusPill(text = batch.band.label(), container = colors.container, content = colors.content)
            if (batch.atRiskCount > 0) {
                Spacer(Modifier.size(4.dp))
                Text(
                    text = "${batch.atRiskCount} at risk",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
        HSpace(4)
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** One compliance alert row with severity pill and an optional Resolve action. */
@Composable
fun ComplianceAlertRow(
    alert: ComplianceAlert,
    onResolve: ((String) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val colors = severityColors(alert.severity)
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusPill(text = alert.severity.label(), container = colors.container, content = colors.content)
                HSpace(8)
                Text(alert.title, style = MaterialTheme.typography.titleSmall)
            }
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
        }
        if (onResolve != null && !alert.resolved) {
            HSpace(8)
            androidx.compose.material3.TextButton(onClick = { onResolve(alert.id) }) {
                Text("Resolve")
            }
        } else if (alert.resolved) {
            HSpace(8)
            Text("Resolved", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.tertiary)
        }
    }
}
