package com.rankridge.app.ui.parent.updates

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.rankridge.app.ui.common.DashboardCard
import com.rankridge.app.ui.common.HSpace
import com.rankridge.app.ui.common.SectionHeader
import com.rankridge.app.ui.common.StatusPill
import com.rankridge.app.ui.common.VSpace
import com.rankridge.app.ui.parent.ChildSnapshot
import com.rankridge.app.ui.parent.FeePayment
import com.rankridge.app.ui.parent.ParentMockData
import com.rankridge.app.ui.parent.ParentUpdateAnnouncement
import com.rankridge.app.ui.parent.PtmStatus
import com.rankridge.app.ui.parent.TeacherRemark
import com.rankridge.app.ui.theme.RankridgeTheme

@Composable
fun ParentUpdatesScreen(
    snapshot: ChildSnapshot,
    onAction: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 28.dp),
    ) {
        VSpace(16)

        val ptm = snapshot.upcomingPtm
        if (ptm != null) {
            SectionHeader("Upcoming PTM")
            DashboardCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    HSpace(10)
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${ptm.dateLabel}, ${ptm.timeLabel}", style = MaterialTheme.typography.titleSmall)
                        Text(
                            text = "${ptm.mode} · ${ptm.agenda}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    StatusPill(
                        text = if (ptm.status == PtmStatus.SCHEDULED) "Scheduled" else "Completed",
                        container = MaterialTheme.colorScheme.secondaryContainer,
                        content = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
                VSpace(12)
                OutlinedButton(onClick = { onAction("Reschedule request sent") }, modifier = Modifier.fillMaxWidth()) {
                    Text("Reschedule")
                }
            }
        }

        SectionHeader("Teacher remarks")
        DashboardCard {
            if (snapshot.teacherRemarks.isEmpty()) {
                Text("No remarks yet", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                snapshot.teacherRemarks.forEachIndexed { index, remark ->
                    TeacherRemarkRow(remark)
                    if (index != snapshot.teacherRemarks.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }

        SectionHeader("Fees")
        FeesDetailCard(snapshot, onAction)

        SectionHeader("Announcements")
        snapshot.announcements.forEach { announcement ->
            AnnouncementCard(announcement, onAction)
            VSpace(8)
        }
    }
}

@Composable
private fun TeacherRemarkRow(remark: TeacherRemark) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(vertical = 10.dp)) {
        Icon(Icons.Filled.Forum, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        HSpace(10)
        Column(modifier = Modifier.weight(1f)) {
            Row {
                Text("${remark.teacherName} · ${remark.subject}", style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                Text(remark.dateLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(remark.remark, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun FeesDetailCard(snapshot: ChildSnapshot, onAction: (String) -> Unit) {
    val fees = snapshot.fees
    DashboardCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("₹${fees.paid} paid of ₹${fees.totalFee}", style = MaterialTheme.typography.titleSmall)
                Text(
                    text = if (fees.due > 0) "₹${fees.due} due · ${fees.nextDueDateLabel}" else fees.nextDueDateLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (fees.due > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        VSpace(10)
        LinearProgressIndicator(
            progress = { (fees.paid.toFloat() / fees.totalFee).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            gapSize = 0.dp,
            drawStopIndicator = {},
        )
        if (fees.due > 0) {
            VSpace(14)
            Button(onClick = { onAction("Opening payment for ₹${fees.due}") }, modifier = Modifier.fillMaxWidth()) {
                Text("Pay ₹${fees.due} now")
            }
        }
        VSpace(14)
        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
        VSpace(10)
        Text("Payment history", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        fees.paymentHistory.forEach { payment -> PaymentRow(payment, onAction) }
    }
}

@Composable
private fun PaymentRow(payment: FeePayment, onAction: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(payment.label, style = MaterialTheme.typography.bodyMedium)
            Text(payment.dateLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text("₹${payment.amount}", style = MaterialTheme.typography.titleSmall)
        if (payment.receiptAvailable) {
            HSpace(4)
            IconButton(onClick = { onAction("Downloading receipt for ${payment.label}") }) {
                Icon(Icons.Filled.Download, contentDescription = "Download receipt", tint = MaterialTheme.colorScheme.primary)
            }
        } else {
            Spacer(Modifier.height(1.dp))
        }
    }
}

@Composable
private fun AnnouncementCard(announcement: ParentUpdateAnnouncement, onAction: (String) -> Unit) {
    DashboardCard(modifier = Modifier.clickable { onAction("Opening: ${announcement.title}") }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusPill(
                text = announcement.category,
                container = MaterialTheme.colorScheme.secondaryContainer,
                content = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = announcement.timeLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        VSpace(8)
        Text(announcement.title, style = MaterialTheme.typography.titleSmall)
        Text(
            text = announcement.body,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 2000)
@Composable
private fun ParentUpdatesPreview() {
    val dashboard = ParentMockData.dashboard
    RankridgeTheme {
        ParentUpdatesScreen(
            snapshot = dashboard.childData.getValue(dashboard.children.first().id),
            onAction = {},
        )
    }
}
