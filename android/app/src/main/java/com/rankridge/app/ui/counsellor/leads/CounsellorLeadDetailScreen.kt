package com.rankridge.app.ui.counsellor.leads

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rankridge.app.ui.common.DashboardCard
import com.rankridge.app.ui.common.HSpace
import com.rankridge.app.ui.common.SectionHeader
import com.rankridge.app.ui.common.StatRow
import com.rankridge.app.ui.common.StatusPill
import com.rankridge.app.ui.common.VSpace
import com.rankridge.app.ui.counsellor.CounsellorMockData
import com.rankridge.app.ui.counsellor.Lead
import com.rankridge.app.ui.counsellor.LeadNote
import com.rankridge.app.ui.counsellor.LeadStatus
import com.rankridge.app.ui.counsellor.label
import com.rankridge.app.ui.counsellor.leadStatusColors
import com.rankridge.app.ui.theme.RankridgeTheme

@Composable
fun CounsellorLeadDetailScreen(
    lead: Lead,
    notes: List<LeadNote>,
    onMarkContacted: () -> Unit,
    onAddNote: (String) -> Unit,
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

        val colors = leadStatusColors(lead.status)
        DashboardCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(lead.courseInterested, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(lead.branch, style = MaterialTheme.typography.titleSmall)
                }
                StatusPill(text = lead.status.label(), container = colors.container, content = colors.content)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.surfaceVariant)
            StatRow(label = "Source", value = lead.source.label())
            StatRow(label = "Created", value = lead.createdLabel)
            StatRow(label = "Last contacted", value = lead.lastContactedLabel)
            StatRow(label = "Next follow-up", value = lead.nextFollowUpLabel)
            if (lead.status != LeadStatus.ADMITTED && lead.status != LeadStatus.LOST) {
                VSpace(10)
                Button(onClick = onMarkContacted, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    HSpace(6)
                    Text("Mark contacted today")
                }
            }
        }

        SectionHeader("Contact")
        DashboardCard {
            Text(lead.name, style = MaterialTheme.typography.titleSmall)
            Text("+91 ${lead.phone}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (lead.parentName != null) {
                VSpace(6)
                Text(
                    text = "Parent: ${lead.parentName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            VSpace(12)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = { onAction("Calling ${lead.name}…") },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Filled.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                    HSpace(6)
                    Text("Call")
                }
                OutlinedButton(
                    onClick = { onAction("Opening WhatsApp with ${lead.name}…") },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Filled.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                    HSpace(6)
                    Text("WhatsApp")
                }
            }
        }

        SectionHeader("Counsellor notes")
        DashboardCard {
            var draft by remember { mutableStateOf("") }
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                label = { Text("Add a note") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
            )
            VSpace(10)
            Button(
                onClick = {
                    if (draft.isNotBlank()) {
                        onAddNote(draft)
                        draft = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Save note")
            }
            if (notes.isNotEmpty()) {
                VSpace(14)
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                notes.forEach { note ->
                    VSpace(12)
                    Text(note.dateLabel, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(note.text, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 1800)
@Composable
private fun CounsellorLeadDetailPreview() {
    val lead = CounsellorMockData.dashboard.leads.first()
    RankridgeTheme {
        CounsellorLeadDetailScreen(
            lead = lead,
            notes = CounsellorMockData.dashboard.notesByLead[lead.id].orEmpty(),
            onMarkContacted = {},
            onAddNote = {},
            onAction = {},
        )
    }
}
