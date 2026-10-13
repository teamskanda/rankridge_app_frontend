package com.rankridge.app.ui.teacher.students

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
import com.rankridge.app.ui.common.Sparkline
import com.rankridge.app.ui.common.StatRow
import com.rankridge.app.ui.common.StatusPill
import com.rankridge.app.ui.common.VSpace
import com.rankridge.app.ui.teacher.TeacherMockData
import com.rankridge.app.ui.teacher.TeacherRemark
import com.rankridge.app.ui.teacher.TeacherStudent
import com.rankridge.app.ui.teacher.label
import com.rankridge.app.ui.teacher.riskColors
import com.rankridge.app.ui.theme.RankridgeTheme

@Composable
fun TeacherStudentDetailScreen(
    student: TeacherStudent,
    remarks: List<TeacherRemark>,
    onAddRemark: (String) -> Unit,
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
                Text(student.batchName, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                StatusPill(text = student.riskLevel.label(), container = colors.container, content = colors.content)
            }
            VSpace(10)
            Text(student.riskReason, style = MaterialTheme.typography.bodyMedium)
        }

        SectionHeader("Academic snapshot")
        DashboardCard {
            StatRow(label = "Attendance", value = "${student.attendancePercent}%")
            StatRow(label = "Average score", value = "${student.avgScorePercent}%")
            StatRow(label = "Overdue assignments", value = "${student.overdueAssignments}")
            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.surfaceVariant)
            Text("Last 5 tests", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            VSpace(8)
            Sparkline(values = student.testTrend, modifier = Modifier.fillMaxWidth().height(64.dp))
            if (student.weakTopics.isNotEmpty()) {
                VSpace(12)
                Text("Weak topics", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                VSpace(6)
                student.weakTopics.forEach { topic ->
                    Text("• $topic", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 2.dp))
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

        SectionHeader("Teacher remarks")
        DashboardCard {
            var draft by remember { mutableStateOf("") }
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                label = { Text("Add a remark") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
            )
            VSpace(10)
            Button(
                onClick = {
                    if (draft.isNotBlank()) {
                        onAddRemark(draft)
                        draft = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Save remark")
            }
            if (remarks.isNotEmpty()) {
                VSpace(14)
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                remarks.forEach { remark ->
                    VSpace(12)
                    Text(remark.dateLabel, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(remark.text, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 2000)
@Composable
private fun TeacherStudentDetailPreview() {
    val student = TeacherMockData.dashboard.students.first()
    RankridgeTheme {
        TeacherStudentDetailScreen(
            student = student,
            remarks = TeacherMockData.dashboard.remarksByStudent[student.id].orEmpty(),
            onAddRemark = {},
            onAction = {},
        )
    }
}
