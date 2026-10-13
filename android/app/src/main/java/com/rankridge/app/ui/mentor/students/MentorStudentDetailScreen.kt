package com.rankridge.app.ui.mentor.students

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.EventAvailable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.rankridge.app.ui.common.DashboardCard
import com.rankridge.app.ui.common.HSpace
import com.rankridge.app.ui.common.SectionHeader
import com.rankridge.app.ui.common.Sparkline
import com.rankridge.app.ui.common.StatRow
import com.rankridge.app.ui.common.StatusPill
import com.rankridge.app.ui.common.VSpace
import com.rankridge.app.ui.mentor.MenteeStudent
import com.rankridge.app.ui.mentor.MentorMockData
import com.rankridge.app.ui.mentor.MentorNote
import com.rankridge.app.ui.mentor.label
import com.rankridge.app.ui.mentor.riskColors
import com.rankridge.app.ui.mentor.trendColor
import com.rankridge.app.ui.mentor.trendIcon
import com.rankridge.app.ui.theme.RankridgeTheme

@Composable
fun MentorStudentDetailScreen(
    student: MenteeStudent,
    notes: List<MentorNote>,
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

        val colors = riskColors(student.riskLevel)
        DashboardCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(student.course, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${student.classYear} · ${student.batch}", style = MaterialTheme.typography.titleSmall)
                }
                StatusPill(text = student.riskLevel.label(), container = colors.container, content = colors.content)
            }
            VSpace(10)
            Text(student.riskReason, style = MaterialTheme.typography.bodyMedium)
            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.surfaceVariant)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.EventAvailable, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                HSpace(10)
                Column {
                    Text("Next review", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(student.nextReviewLabel, style = MaterialTheme.typography.titleSmall)
                }
                HSpace(24)
                Column {
                    Text("Last review", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(student.lastReviewLabel, style = MaterialTheme.typography.titleSmall)
                }
            }
        }

        SectionHeader("Academic snapshot")
        DashboardCard {
            StatRow(label = "Attendance", value = "${student.attendancePercent}%")
            StatRow(label = "Average score", value = "${student.avgScorePercent}%")
            StatRow(label = "Missed assignments", value = "${student.missedAssignments}")
            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.surfaceVariant)
            Text("Last 5 tests", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            VSpace(8)
            Sparkline(values = student.testTrend, modifier = Modifier.fillMaxWidth().height(64.dp))
            VSpace(8)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(trendIcon(student.overallTrend), contentDescription = null, modifier = Modifier.size(16.dp), tint = trendColor(student.overallTrend))
                HSpace(6)
                Text(
                    text = "Trend: ${student.overallTrend.name.lowercase()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (student.weakSubjects.isNotEmpty()) {
                VSpace(12)
                Text("Weak topics", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                VSpace(6)
                student.weakSubjects.forEach { subject ->
                    Text("• $subject", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 2.dp))
                }
            }
        }

        SectionHeader("Parent contact")
        DashboardCard {
            Text(student.parentName, style = MaterialTheme.typography.titleSmall)
            Text("+91 ${student.parentPhone}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            VSpace(12)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = { onAction("Calling ${student.parentName}…") },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Filled.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                    HSpace(6)
                    Text("Call")
                }
                OutlinedButton(
                    onClick = { onAction("Opening chat with ${student.parentName}…") },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Filled.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                    HSpace(6)
                    Text("Message")
                }
            }
        }

        SectionHeader("Mentor notes")
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

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 2000)
@Composable
private fun MentorStudentDetailPreview() {
    val student = MentorMockData.dashboard.mentees.first()
    RankridgeTheme {
        MentorStudentDetailScreen(
            student = student,
            notes = MentorMockData.dashboard.notesByStudent[student.id].orEmpty(),
            onAddNote = {},
            onAction = {},
        )
    }
}
