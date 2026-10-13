package com.rankridge.app.ui.teacher

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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.rankridge.app.ui.common.EntityRow
import com.rankridge.app.ui.common.HSpace
import com.rankridge.app.ui.common.InitialsAvatar
import com.rankridge.app.ui.common.StatusPill

/** Colour + label mapping for [RiskLevel], shared across Home, Students and the detail screen. */
data class RiskColors(val container: Color, val content: Color)

@Composable
fun riskColors(level: RiskLevel): RiskColors = when (level) {
    RiskLevel.HIGH -> RiskColors(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer)
    RiskLevel.MODERATE -> RiskColors(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
    RiskLevel.LOW -> RiskColors(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
}

fun RiskLevel.label(): String = when (this) {
    RiskLevel.HIGH -> "High risk"
    RiskLevel.MODERATE -> "Moderate risk"
    RiskLevel.LOW -> "On track"
}

@Composable
fun syllabusColors(status: SyllabusStatus): RiskColors = when (status) {
    SyllabusStatus.ON_TRACK -> RiskColors(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
    SyllabusStatus.DELAYED -> RiskColors(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer)
    SyllabusStatus.NOT_MARKED -> RiskColors(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
    SyllabusStatus.UPCOMING -> RiskColors(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
}

fun SyllabusStatus.label(): String = when (this) {
    SyllabusStatus.ON_TRACK -> "On track"
    SyllabusStatus.DELAYED -> "Delayed"
    SyllabusStatus.NOT_MARKED -> "Not marked"
    SyllabusStatus.UPCOMING -> "Upcoming"
}

@Composable
fun doubtColors(status: DoubtStatus): RiskColors = when (status) {
    DoubtStatus.URGENT -> RiskColors(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer)
    DoubtStatus.OPEN -> RiskColors(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
    DoubtStatus.RESOLVED -> RiskColors(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
}

fun DoubtStatus.label(): String = when (this) {
    DoubtStatus.URGENT -> "Urgent"
    DoubtStatus.OPEN -> "Open"
    DoubtStatus.RESOLVED -> "Resolved"
}

fun ScheduleEventType.icon(): ImageVector = when (this) {
    ScheduleEventType.LECTURE -> Icons.Filled.MenuBook
    ScheduleEventType.TEST -> Icons.Filled.CalendarMonth
    ScheduleEventType.PTM -> Icons.Filled.Groups
}

fun ScheduleEventType.label(): String = when (this) {
    ScheduleEventType.LECTURE -> "Lecture"
    ScheduleEventType.TEST -> "Test"
    ScheduleEventType.PTM -> "PTM"
}

/** One student row: avatar, name/batch, risk pill, tap to open the student detail screen. */
@Composable
fun TeacherStudentRow(
    student: TeacherStudent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = riskColors(student.riskLevel)
    EntityRow(
        title = student.name,
        subtitle = student.batchName,
        pillText = student.riskLevel.label(),
        pillContainer = colors.container,
        pillContent = colors.content,
        note = "${student.attendancePercent}% attendance",
        onClick = onClick,
        modifier = modifier,
        leading = { InitialsAvatar(student.initials, colors.container, colors.content) },
    )
}

/**
 * One schedule entry: time and kind, the lecture/test title, where it is, and the attendance
 * action.
 *
 * The action sits below the text rather than beside it. As a trailing element in the same Row it
 * was measured first, leaving a long lecture title ("Rotational Motion — Torque & Angular
 * Momentum") about half the row to wrap into, so it came out broken across two lines and still
 * ellipsised. Below the text the title gets the full row width and reads in full.
 */
@Composable
fun ScheduleEventRow(
    event: ScheduleEvent,
    onMarkAttendance: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                event.type.icon(),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp),
            )
        }
        HSpace(12)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${event.timeLabel} · ${event.type.label()}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = event.subjectOrTopic,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${event.batchName} · ${event.location}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (event.requiresAttendance) {
                Spacer(Modifier.size(10.dp))
                if (event.attendanceMarked) {
                    StatusPill(
                        text = "Attendance marked",
                        container = MaterialTheme.colorScheme.tertiaryContainer,
                        content = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                } else {
                    Button(
                        onClick = { onMarkAttendance(event.id) },
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                    ) {
                        Text("Mark attendance")
                    }
                }
            }
        }
    }
}
