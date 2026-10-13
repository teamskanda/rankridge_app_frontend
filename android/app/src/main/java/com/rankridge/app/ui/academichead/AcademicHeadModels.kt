package com.rankridge.app.ui.academichead

import com.rankridge.app.ui.common.PerformanceBand

/**
 * UI-only data models for the Academic Head dashboard (CLAUDE.md: "Are classes happening? Is
 * syllabus on track? Which faculty needs support? Which students/batches are at risk?"). This
 * role is academic quality control across a branch, not classroom teaching or admissions — so the
 * shape here is oversight: daily ops numbers, a faculty compliance table, batch health, a student
 * risk summary and compliance alerts a head can resolve — not a single teacher's classroom or a
 * single mentee's file. No backend in this demo — [AcademicHeadMockData] stands in, surfaced via
 * [AcademicHeadDashboardViewModel].
 */

enum class ComplianceAlertType { UNMARKED_LECTURE, DELAYED_BATCH, LOW_ATTENDANCE, OVERDUE_PTM, PENDING_FOLLOWUP }

enum class AlertSeverity { HIGH, MEDIUM, LOW }

data class AcademicHeadProfile(
    val name: String,
    val phone: String,
    val branch: String,
    val notifications: Int = 0,
) {
    val initials: String
        get() = name.trim().split(" ").filter { it.isNotEmpty() }.take(2)
            .joinToString("") { it.first().uppercase() }
}

data class DailyOps(
    val classesPlanned: Int,
    val classesCompleted: Int,
    val classesCancelled: Int,
    val attendanceAvgPercent: Int,
    val syllabusOnTrackPercent: Int,
)

data class FacultyMember(
    val id: String,
    val name: String,
    val subject: String,
    val batchesTaught: List<String>,
    val lecturesDelivered: Int,
    val lecturesPlanned: Int,
    val compliancePercent: Int,
    val delayedLectures: Int,
    val unmarkedLectures: Int,
    val ptmsConducted: Int,
    val pendingFollowUps: Int,
    val band: PerformanceBand,
) {
    val initials: String
        get() = name.trim().split(" ").filter { it.isNotEmpty() }.take(2)
            .joinToString("") { it.first().uppercase() }
}

data class BatchPerformance(
    val id: String,
    val name: String,
    val course: String,
    val studentCount: Int,
    val avgTestScorePercent: Int,
    val attendancePercent: Int,
    val syllabusCompletionPercent: Int,
    val atRiskCount: Int,
    val scoreTrend: List<Int>,
    val band: PerformanceBand,
    val facultyName: String,
)

data class ComplianceAlert(
    val id: String,
    val type: ComplianceAlertType,
    val severity: AlertSeverity,
    val title: String,
    val detail: String,
    val relatedName: String,
    val dateLabel: String,
    val resolved: Boolean = false,
)

data class StudentRiskOverview(
    val highCount: Int,
    val moderateCount: Int,
    val lowCount: Int,
    val topReasons: List<String>,
    val openInterventions: Int,
)

data class AcademicHeadDashboard(
    val head: AcademicHeadProfile,
    val dailyOps: DailyOps,
    val faculty: List<FacultyMember>,
    val batches: List<BatchPerformance>,
    val alerts: List<ComplianceAlert>,
    val riskOverview: StudentRiskOverview,
)

/** Static sample content standing in for the backend. Reuses the same institute, faculty, and
 * batches established across the Teacher/Mentor/Counsellor dashboards. */
object AcademicHeadMockData {
    private val dayaRao = FacultyMember(
        id = "fac_rao",
        name = "Dr. Rohan Rao",
        subject = "Physics",
        batchesTaught = listOf("Nucleus XI-A", "Zenith XII-B"),
        lecturesDelivered = 42,
        lecturesPlanned = 44,
        compliancePercent = 95,
        delayedLectures = 1,
        unmarkedLectures = 0,
        ptmsConducted = 3,
        pendingFollowUps = 1,
        band = PerformanceBand.STRONG,
    )
    private val priyaDesai = FacultyMember(
        id = "fac_desai",
        name = "Priya Desai",
        subject = "Chemistry",
        batchesTaught = listOf("Nucleus XI-A", "Foundation IX-B"),
        lecturesDelivered = 36,
        lecturesPlanned = 40,
        compliancePercent = 82,
        delayedLectures = 3,
        unmarkedLectures = 1,
        ptmsConducted = 2,
        pendingFollowUps = 2,
        band = PerformanceBand.GOOD,
    )
    private val vikramNair = FacultyMember(
        id = "fac_nair",
        name = "Vikram Nair",
        subject = "Mathematics",
        batchesTaught = listOf("Zenith XII-B"),
        lecturesDelivered = 28,
        lecturesPlanned = 38,
        compliancePercent = 61,
        delayedLectures = 7,
        unmarkedLectures = 3,
        ptmsConducted = 1,
        pendingFollowUps = 4,
        band = PerformanceBand.NEEDS_ATTENTION,
    )
    private val sunitaIyer = FacultyMember(
        id = "fac_iyer",
        name = "Sunita Iyer",
        subject = "Biology",
        batchesTaught = listOf("Zenith XII-B", "Foundation IX-B"),
        lecturesDelivered = 39,
        lecturesPlanned = 40,
        compliancePercent = 90,
        delayedLectures = 1,
        unmarkedLectures = 0,
        ptmsConducted = 3,
        pendingFollowUps = 0,
        band = PerformanceBand.STRONG,
    )

    val dashboard = AcademicHeadDashboard(
        head = AcademicHeadProfile(
            name = "Meenakshi Pillai",
            phone = "90001 23456",
            branch = "Rankridge — HSR Layout",
            notifications = 5,
        ),
        dailyOps = DailyOps(
            classesPlanned = 18,
            classesCompleted = 14,
            classesCancelled = 1,
            attendanceAvgPercent = 86,
            syllabusOnTrackPercent = 78,
        ),
        faculty = listOf(dayaRao, priyaDesai, vikramNair, sunitaIyer),
        batches = listOf(
            BatchPerformance(
                id = "batch_nucleus",
                name = "Nucleus XI-A",
                course = "Two-Year Classroom Program (JEE)",
                studentCount = 42,
                avgTestScorePercent = 74,
                attendancePercent = 91,
                syllabusCompletionPercent = 81,
                atRiskCount = 2,
                scoreTrend = listOf(68, 70, 71, 73, 74),
                band = PerformanceBand.STRONG,
                facultyName = "Dr. Rohan Rao · Priya Desai",
            ),
            BatchPerformance(
                id = "batch_zenith",
                name = "Zenith XII-B",
                course = "One-Year Target Program (NEET)",
                studentCount = 38,
                avgTestScorePercent = 58,
                attendancePercent = 79,
                syllabusCompletionPercent = 64,
                atRiskCount = 6,
                scoreTrend = listOf(64, 62, 60, 59, 58),
                band = PerformanceBand.NEEDS_ATTENTION,
                facultyName = "Vikram Nair · Sunita Iyer",
            ),
            BatchPerformance(
                id = "batch_foundation",
                name = "Foundation IX-B",
                course = "Foundation Program",
                studentCount = 35,
                avgTestScorePercent = 77,
                attendancePercent = 95,
                syllabusCompletionPercent = 88,
                atRiskCount = 0,
                scoreTrend = listOf(72, 74, 75, 76, 77),
                band = PerformanceBand.STRONG,
                facultyName = "Priya Desai · Sunita Iyer",
            ),
        ),
        alerts = listOf(
            ComplianceAlert(
                id = "a1",
                type = ComplianceAlertType.UNMARKED_LECTURE,
                severity = AlertSeverity.HIGH,
                title = "3 unmarked lectures",
                detail = "Vikram Nair has not marked attendance for 3 lectures this week",
                relatedName = "Vikram Nair",
                dateLabel = "Today",
            ),
            ComplianceAlert(
                id = "a2",
                type = ComplianceAlertType.DELAYED_BATCH,
                severity = AlertSeverity.HIGH,
                title = "Syllabus delayed",
                detail = "Zenith XII-B is 14% behind the planned syllabus schedule",
                relatedName = "Zenith XII-B",
                dateLabel = "Today",
            ),
            ComplianceAlert(
                id = "a3",
                type = ComplianceAlertType.LOW_ATTENDANCE,
                severity = AlertSeverity.MEDIUM,
                title = "Attendance below threshold",
                detail = "Zenith XII-B attendance has dropped to 79%, below the 85% target",
                relatedName = "Zenith XII-B",
                dateLabel = "9 Oct",
            ),
            ComplianceAlert(
                id = "a4",
                type = ComplianceAlertType.OVERDUE_PTM,
                severity = AlertSeverity.MEDIUM,
                title = "PTM overdue",
                detail = "No parent-teacher meeting held for Zenith XII-B in the last 6 weeks",
                relatedName = "Vikram Nair",
                dateLabel = "6 Oct",
            ),
            ComplianceAlert(
                id = "a5",
                type = ComplianceAlertType.PENDING_FOLLOWUP,
                severity = AlertSeverity.LOW,
                title = "Faculty follow-up pending",
                detail = "Priya Desai has 2 student follow-ups pending beyond the review window",
                relatedName = "Priya Desai",
                dateLabel = "4 Oct",
            ),
        ),
        riskOverview = StudentRiskOverview(
            highCount = 5,
            moderateCount = 9,
            lowCount = 91,
            topReasons = listOf("Attendance below 75%", "Two or more missed tests", "Declining subject score"),
            openInterventions = 6,
        ),
    )
}
