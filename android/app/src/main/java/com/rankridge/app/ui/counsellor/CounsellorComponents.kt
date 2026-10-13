package com.rankridge.app.ui.counsellor

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

/** Colour + label mapping for [LeadStatus], shared by the Home, Leads and detail screens. */
data class LeadStatusColors(val container: Color, val content: Color)

@Composable
fun leadStatusColors(status: LeadStatus): LeadStatusColors = when (status) {
    LeadStatus.NEW -> LeadStatusColors(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
    LeadStatus.CONTACTED -> LeadStatusColors(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
    LeadStatus.FOLLOW_UP -> LeadStatusColors(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
    LeadStatus.INTERESTED -> LeadStatusColors(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
    LeadStatus.APPLICATION_SUBMITTED -> LeadStatusColors(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
    LeadStatus.ADMITTED -> LeadStatusColors(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
    LeadStatus.LOST -> LeadStatusColors(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer)
}

fun LeadStatus.label(): String = when (this) {
    LeadStatus.NEW -> "New"
    LeadStatus.CONTACTED -> "Contacted"
    LeadStatus.FOLLOW_UP -> "Follow-up"
    LeadStatus.INTERESTED -> "Interested"
    LeadStatus.APPLICATION_SUBMITTED -> "Application in"
    LeadStatus.ADMITTED -> "Admitted"
    LeadStatus.LOST -> "Lost"
}

@Composable
fun leadPriorityColor(priority: LeadPriority): Color = when (priority) {
    LeadPriority.HIGH -> MaterialTheme.colorScheme.error
    LeadPriority.MEDIUM -> MaterialTheme.colorScheme.secondary
    LeadPriority.LOW -> MaterialTheme.colorScheme.onSurfaceVariant
}

fun LeadSource.label(): String = when (this) {
    LeadSource.WEBSITE -> "Website"
    LeadSource.REFERRAL -> "Referral"
    LeadSource.WALK_IN -> "Walk-in"
    LeadSource.PHONE_INQUIRY -> "Phone inquiry"
    LeadSource.SOCIAL_MEDIA -> "Social media"
}

fun ApplicationStatus.label(): String = when (this) {
    ApplicationStatus.DOCUMENTS_PENDING -> "Documents pending"
    ApplicationStatus.UNDER_REVIEW -> "Under review"
    ApplicationStatus.APPROVED -> "Approved"
    ApplicationStatus.ADMITTED -> "Admitted"
}

@Composable
fun applicationStatusColors(status: ApplicationStatus): LeadStatusColors = when (status) {
    ApplicationStatus.DOCUMENTS_PENDING -> LeadStatusColors(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer)
    ApplicationStatus.UNDER_REVIEW -> LeadStatusColors(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
    ApplicationStatus.APPROVED -> LeadStatusColors(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
    ApplicationStatus.ADMITTED -> LeadStatusColors(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
}

/** One lead row: avatar, name/course, priority dot, status pill, tap to open the lead detail screen. */
@Composable
fun LeadRow(
    lead: Lead,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val statusColors = leadStatusColors(lead.status)
    EntityRow(
        title = lead.name,
        subtitle = lead.courseInterested,
        pillText = lead.status.label(),
        pillContainer = statusColors.container,
        pillContent = statusColors.content,
        note = "Next: ${lead.nextFollowUpLabel}",
        titleDotColor = leadPriorityColor(lead.priority),
        onClick = onClick,
        modifier = modifier,
        leading = { InitialsAvatar(lead.initials, statusColors.container, statusColors.content) },
    )
}
