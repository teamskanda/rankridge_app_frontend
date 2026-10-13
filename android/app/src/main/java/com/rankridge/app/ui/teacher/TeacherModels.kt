package com.rankridge.app.ui.teacher

/**
 * UI-only data models for the Teacher dashboard (CLAUDE.md: "Today's schedule, quick metrics, my
 * batches, attendance action center, syllabus compliance, students needing attention, tests,
 * assignments/DPPs, doubts queue, PTM & follow-ups"). A teacher owns batches and the delivery
 * machinery around them (syllabus, attendance, tests, doubts) — the opposite emphasis from the
 * Mentor dashboard, which owns a small roster of individual students but teaches nothing. No
 * backend in this demo — [TeacherMockData] stands in, surfaced via [TeacherDashboardViewModel].
 * Mock data deliberately reuses names from the Student/Parent/Mentor mock universe (Aarav Sharma,
 * Kabir Mehta, the Nucleus XI-A / Zenith XII-B batches) so the four dashboards read as one product.
 */

enum class ScheduleEventType { LECTURE, TEST, PTM }

enum class SyllabusStatus { ON_TRACK, DELAYED, NOT_MARKED, UPCOMING }

enum class DoubtStatus { OPEN, URGENT, RESOLVED }

enum class TestStatus { UPCOMING, NEEDS_EVALUATION, COMPLETED }

enum class PtmStatus { SCHEDULED, COMPLETED }

enum class RiskLevel { HIGH, MODERATE, LOW }

data class TeacherProfile(
    val name: String,
    val subject: String,
    val phone: String,
    val notifications: Int = 0,
) {
    val initials: String
        get() = name.trim().split(" ").filter { it.isNotEmpty() }.take(2)
            .joinToString("") { it.first().uppercase() }
}

data class ScheduleEvent(
    val id: String,
    val dayLabel: String,
    val timeLabel: String,
    val type: ScheduleEventType,
    val batchName: String,
    val subjectOrTopic: String,
    val location: String,
    val isOnline: Boolean,
    val attendanceMarked: Boolean = false,
    val requiresAttendance: Boolean = true,
)

data class TeacherBatch(
    val id: String,
    val name: String,
    val subject: String,
    val studentCount: Int,
    val syllabusCompletionPercent: Int,
    val attendanceAveragePercent: Int,
    val latestTestAveragePercent: Int,
)

data class SyllabusItem(
    val id: String,
    val batchId: String,
    val batchName: String,
    val topic: String,
    val plannedDateLabel: String,
    val status: SyllabusStatus,
)

data class TeacherStudent(
    val id: String,
    val name: String,
    val batchId: String,
    val batchName: String,
    val riskLevel: RiskLevel,
    val riskReason: String,
    val attendancePercent: Int,
    val avgScorePercent: Int,
    val testTrend: List<Int>,
    val weakTopics: List<String>,
    val overdueAssignments: Int,
    val parentName: String,
    val parentPhone: String,
) {
    val initials: String
        get() = name.trim().split(" ").filter { it.isNotEmpty() }.take(2)
            .joinToString("") { it.first().uppercase() }
}

data class TeacherRemark(
    val id: String,
    val dateLabel: String,
    val text: String,
)

data class TestItem(
    val id: String,
    val name: String,
    val batchId: String,
    val batchName: String,
    val status: TestStatus,
    val dateLabel: String,
    val batchAverage: Int? = null,
    val highest: Int? = null,
    val lowest: Int? = null,
    val weakestTopic: String? = null,
)

data class AssignmentItem(
    val id: String,
    val title: String,
    val batchId: String,
    val batchName: String,
    val dueDateLabel: String,
    val submittedCount: Int,
    val totalCount: Int,
    val overdue: Boolean,
)

data class DoubtTicket(
    val id: String,
    val studentName: String,
    val batchName: String,
    val subject: String,
    val question: String,
    val status: DoubtStatus,
    val timeLabel: String,
)

data class PtmItem(
    val id: String,
    val studentName: String,
    val parentName: String,
    val dateLabel: String,
    val timeLabel: String,
    val concern: String,
    val status: PtmStatus,
)

data class TeacherDashboard(
    val teacher: TeacherProfile,
    val schedule: List<ScheduleEvent>,
    val batches: List<TeacherBatch>,
    val syllabusItems: List<SyllabusItem>,
    val students: List<TeacherStudent>,
    val remarksByStudent: Map<String, List<TeacherRemark>>,
    val tests: List<TestItem>,
    val assignments: List<AssignmentItem>,
    val doubts: List<DoubtTicket>,
    val ptms: List<PtmItem>,
)

/** Static sample content standing in for the backend. Realistic coaching-institute data. */
object TeacherMockData {
    private const val NUCLEUS = "nucleus"
    private const val ZENITH = "zenith"

    private val nucleusBatch = TeacherBatch(
        id = NUCLEUS,
        name = "Nucleus XI-A",
        subject = "Physics",
        studentCount = 42,
        syllabusCompletionPercent = 78,
        attendanceAveragePercent = 91,
        latestTestAveragePercent = 74,
    )
    private val zenithBatch = TeacherBatch(
        id = ZENITH,
        name = "Zenith XII-B",
        subject = "Physics",
        studentCount = 38,
        syllabusCompletionPercent = 85,
        attendanceAveragePercent = 82,
        latestTestAveragePercent = 61,
    )

    private val kabir = TeacherStudent(
        id = "kabir",
        name = "Kabir Mehta",
        batchId = ZENITH,
        batchName = "Zenith XII-B",
        riskLevel = RiskLevel.HIGH,
        riskReason = "Attendance below 70% and weak in Optics",
        attendancePercent = 64,
        avgScorePercent = 48,
        testTrend = listOf(58, 54, 51, 49, 46),
        weakTopics = listOf("Optics — Ray Diagrams"),
        overdueAssignments = 2,
        parentName = "Sunita Mehta",
        parentPhone = "99887 76655",
    )
    private val rohanGupta = TeacherStudent(
        id = "rohan_gupta",
        name = "Rohan Gupta",
        batchId = ZENITH,
        batchName = "Zenith XII-B",
        riskLevel = RiskLevel.MODERATE,
        riskReason = "Score declining over last 3 tests in Semiconductor Devices",
        attendancePercent = 86,
        avgScorePercent = 58,
        testTrend = listOf(68, 64, 61, 59, 56),
        weakTopics = listOf("Semiconductor Devices"),
        overdueAssignments = 1,
        parentName = "Anil Gupta",
        parentPhone = "97654 32109",
    )
    private val meeraIyer = TeacherStudent(
        id = "meera_iyer",
        name = "Meera Iyer",
        batchId = NUCLEUS,
        batchName = "Nucleus XI-A",
        riskLevel = RiskLevel.MODERATE,
        riskReason = "Missed the last test — no score on record",
        attendancePercent = 88,
        avgScorePercent = 69,
        testTrend = listOf(72, 70, 69, 68, 0),
        weakTopics = listOf("Rotational Motion"),
        overdueAssignments = 0,
        parentName = "Lakshmi Iyer",
        parentPhone = "96543 21098",
    )
    private val aarav = TeacherStudent(
        id = "aarav",
        name = "Aarav Sharma",
        batchId = NUCLEUS,
        batchName = "Nucleus XI-A",
        riskLevel = RiskLevel.LOW,
        riskReason = "Consistent, strong performer",
        attendancePercent = 94,
        avgScorePercent = 78,
        testTrend = listOf(68, 71, 74, 76, 78),
        weakTopics = emptyList(),
        overdueAssignments = 0,
        parentName = "Rakesh Sharma",
        parentPhone = "91234 56780",
    )

    val dashboard = TeacherDashboard(
        teacher = TeacherProfile(name = "Dr. Rohan Rao", subject = "Physics", phone = "92345 67890", notifications = 4),
        schedule = listOf(
            ScheduleEvent(
                id = "s1",
                dayLabel = "Today",
                timeLabel = "9:00 AM",
                type = ScheduleEventType.LECTURE,
                batchName = "Nucleus XI-A",
                subjectOrTopic = "Rotational Motion — Torque & Angular Momentum",
                location = "Room 204",
                isOnline = false,
            ),
            ScheduleEvent(
                id = "s2",
                dayLabel = "Today",
                timeLabel = "11:30 AM",
                type = ScheduleEventType.TEST,
                batchName = "Zenith XII-B",
                subjectOrTopic = "Monthly Test 4",
                location = "Live link",
                isOnline = true,
                attendanceMarked = true,
                requiresAttendance = false,
            ),
            ScheduleEvent(
                id = "s3",
                dayLabel = "Today",
                timeLabel = "4:00 PM",
                type = ScheduleEventType.PTM,
                batchName = "Zenith XII-B",
                subjectOrTopic = "PTM — Kabir Mehta with Sunita Mehta",
                location = "Staff room",
                isOnline = false,
                requiresAttendance = false,
            ),
            ScheduleEvent(
                id = "s4",
                dayLabel = "Tomorrow",
                timeLabel = "10:00 AM",
                type = ScheduleEventType.LECTURE,
                batchName = "Zenith XII-B",
                subjectOrTopic = "Optics — Ray Diagrams",
                location = "Room 112",
                isOnline = false,
            ),
            ScheduleEvent(
                id = "s5",
                dayLabel = "Tomorrow",
                timeLabel = "2:00 PM",
                type = ScheduleEventType.LECTURE,
                batchName = "Nucleus XI-A",
                subjectOrTopic = "Rotational Motion — Moment of Inertia",
                location = "Room 204",
                isOnline = false,
            ),
        ),
        batches = listOf(nucleusBatch, zenithBatch),
        syllabusItems = listOf(
            SyllabusItem("sy1", NUCLEUS, "Nucleus XI-A", "Rotational Motion — Torque & Angular Momentum", "Today", SyllabusStatus.UPCOMING),
            SyllabusItem("sy2", NUCLEUS, "Nucleus XI-A", "Rotational Motion — Moment of Inertia", "Tomorrow", SyllabusStatus.UPCOMING),
            SyllabusItem("sy3", NUCLEUS, "Nucleus XI-A", "Work, Energy & Power", "28 Sep", SyllabusStatus.ON_TRACK),
            SyllabusItem("sy4", NUCLEUS, "Nucleus XI-A", "Gravitation", "4 Oct", SyllabusStatus.DELAYED),
            SyllabusItem("sy5", ZENITH, "Zenith XII-B", "Optics — Ray Diagrams", "Tomorrow", SyllabusStatus.UPCOMING),
            SyllabusItem("sy6", ZENITH, "Zenith XII-B", "Optics — Reflection & Refraction", "6 Oct", SyllabusStatus.NOT_MARKED),
            SyllabusItem("sy7", ZENITH, "Zenith XII-B", "Modern Physics — Photoelectric Effect", "29 Sep", SyllabusStatus.ON_TRACK),
            SyllabusItem("sy8", ZENITH, "Zenith XII-B", "Semiconductor Devices", "2 Oct", SyllabusStatus.DELAYED),
        ),
        students = listOf(kabir, rohanGupta, meeraIyer, aarav),
        remarksByStudent = mapOf(
            "kabir" to listOf(
                TeacherRemark("r1", "20 Sep", "Spoke to Kabir about attendance. Advised extra Optics practice before the next test."),
            ),
            "rohan_gupta" to listOf(
                TeacherRemark("r2", "1 Oct", "Recommended revisiting Semiconductor Devices basics — scheduled a doubt session."),
            ),
        ),
        tests = listOf(
            TestItem("t1", "Monthly Test 4", ZENITH, "Zenith XII-B", TestStatus.NEEDS_EVALUATION, "Today"),
            TestItem(
                "t2", "Weekly Test 11", NUCLEUS, "Nucleus XI-A", TestStatus.COMPLETED, "28 Sep",
                batchAverage = 71, highest = 94, lowest = 38, weakestTopic = "Rotational Dynamics",
            ),
            TestItem("t3", "Monthly Test 5", NUCLEUS, "Nucleus XI-A", TestStatus.UPCOMING, "18 Oct"),
        ),
        assignments = listOf(
            AssignmentItem("a1", "DPP 14 — Rotational Motion", NUCLEUS, "Nucleus XI-A", "Today", submittedCount = 35, totalCount = 42, overdue = false),
            AssignmentItem("a2", "Optics Practice Set", ZENITH, "Zenith XII-B", "9 Oct", submittedCount = 29, totalCount = 38, overdue = true),
        ),
        doubts = listOf(
            DoubtTicket("d1", "Kabir Mehta", "Zenith XII-B", "Physics", "Lens formula sign convention — I keep getting the sign wrong for concave lenses.", DoubtStatus.URGENT, "2h ago"),
            DoubtTicket("d2", "Aarav Sharma", "Nucleus XI-A", "Physics", "Why does torque direction flip in a rotating reference frame?", DoubtStatus.OPEN, "5h ago"),
            DoubtTicket("d3", "Meera Iyer", "Nucleus XI-A", "Physics", "Angular momentum conservation in collision problems.", DoubtStatus.RESOLVED, "1d ago"),
        ),
        ptms = listOf(
            PtmItem("p1", "Kabir Mehta", "Sunita Mehta", "Today", "4:00 PM", "Discuss attendance drop and missed tests", PtmStatus.SCHEDULED),
            PtmItem("p2", "Aarav Sharma", "Rakesh Sharma", "28 Sep", "—", "Routine monthly update", PtmStatus.COMPLETED),
        ),
    )
}
