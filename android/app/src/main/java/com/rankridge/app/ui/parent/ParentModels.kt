package com.rankridge.app.ui.parent

import com.rankridge.app.ui.common.PerformanceBand

/**
 * UI-only data models for the Parent dashboard. Deliberately simpler than the Student models
 * (per CLAUDE.md: "deliberately simpler than the student dashboard") — a parent monitors rather
 * than studies, so there is no courses/doubts/practice graph here, only the subset needed to
 * answer: is my child attending? is performance improving? what needs attention? is anything
 * urgent? Every shape mirrors the Parent spec in CLAUDE.md so a real API can map onto it 1:1.
 * No backend in this demo — [ParentMockData] stands in, surfaced via [ParentDashboardViewModel].
 */

enum class TrendDirection { IMPROVING, STABLE, DECLINING }

enum class AlertSeverity { INFO, WARNING, URGENT }

data class ParentProfile(
    val name: String,
    val phone: String,
    val notifications: Int = 0,
) {
    val initials: String
        get() = name.trim().split(" ").filter { it.isNotEmpty() }.take(2)
            .joinToString("") { it.first().uppercase() }
}

data class ChildProfile(
    val id: String,
    val name: String,
    val course: String,
    val classYear: String,
    val batch: String,
) {
    val initials: String
        get() = name.trim().split(" ").filter { it.isNotEmpty() }.take(2)
            .joinToString("") { it.first().uppercase() }
}

data class WeeklyOverview(
    val attendancePercent: Int,
    val testsThisWeek: Int,
    val averageScorePercent: Int,
    val classesAttended: Int,
    val classesTotal: Int,
    val pendingWorkCount: Int,
)

data class UrgentAlert(
    val title: String,
    val detail: String,
    val severity: AlertSeverity,
)

data class SubjectAttendance(
    val subject: String,
    val percent: Int,
)

data class AttendanceOverview(
    val monthLabel: String,
    val monthPercent: Int,
    val present: Int,
    val absent: Int,
    val late: Int,
    val total: Int,
    val subjectWise: List<SubjectAttendance>,
    val recentAbsences: List<String>,
    val trend: TrendDirection,
)

data class SubjectScore(
    val subject: String,
    val percent: Int,
    val band: PerformanceBand,
)

data class LatestTest(
    val testName: String,
    val dateLabel: String,
    val score: Int,
    val totalMarks: Int,
    val percentage: Int,
    val percentile: Int,
    val batchRank: Int,
    val batchSize: Int,
    val subjectSplit: List<SubjectScore>,
)

data class SubjectTrend(
    val subject: String,
    val direction: TrendDirection,
)

data class PerformanceTrend(
    /** Percentage scores of the last 5 tests, oldest → newest, for the trend sparkline. */
    val last5Tests: List<Int>,
    val overallTrend: TrendDirection,
    val subjectTrends: List<SubjectTrend>,
)

/** One line of "doing well" or "needs attention" — a label plus the supporting detail. */
data class StrengthConcern(
    val label: String,
    val detail: String,
)

data class TeacherRemark(
    val teacherName: String,
    val subject: String,
    val remark: String,
    val dateLabel: String,
)

enum class PtmStatus { SCHEDULED, COMPLETED }

data class PtmMeeting(
    val dateLabel: String,
    val timeLabel: String,
    val mode: String,
    val agenda: String,
    val status: PtmStatus,
)

data class FeePayment(
    val label: String,
    val amount: Int,
    val dateLabel: String,
    val receiptAvailable: Boolean,
)

data class FeeSummary(
    val totalFee: Int,
    val paid: Int,
    val due: Int,
    val nextDueDateLabel: String,
    val paymentHistory: List<FeePayment>,
)

data class ParentUpdateAnnouncement(
    val title: String,
    val body: String,
    val timeLabel: String,
    val category: String,
)

/** Everything for one linked child — the unit that switches when the parent changes the child selector. */
data class ChildSnapshot(
    val weeklyOverview: WeeklyOverview,
    val urgentAlerts: List<UrgentAlert>,
    val attendance: AttendanceOverview,
    val latestTest: LatestTest,
    val performanceTrend: PerformanceTrend,
    val strengths: List<StrengthConcern>,
    val concerns: List<StrengthConcern>,
    val teacherRemarks: List<TeacherRemark>,
    val upcomingPtm: PtmMeeting?,
    val fees: FeeSummary,
    val announcements: List<ParentUpdateAnnouncement>,
)

/** The full Parent dashboard: one parent, one or more linked children, one snapshot per child. */
data class ParentDashboard(
    val parent: ParentProfile,
    val children: List<ChildProfile>,
    val childData: Map<String, ChildSnapshot>,
)

/** Static sample content standing in for the backend. Realistic coaching-institute data. */
object ParentMockData {
    private val ishita = ChildProfile(
        id = "ishita",
        name = "Ishita Verma",
        course = "Two-Year Classroom Program (JEE)",
        classYear = "Class XI",
        batch = "Nucleus XI-A",
    )
    private val yash = ChildProfile(
        id = "yash",
        name = "Yash Verma",
        course = "Foundation Program",
        classYear = "Class IX",
        batch = "Foundation IX-B",
    )

    private val ishitaSnapshot = ChildSnapshot(
        weeklyOverview = WeeklyOverview(
            attendancePercent = 92,
            testsThisWeek = 1,
            averageScorePercent = 76,
            classesAttended = 18,
            classesTotal = 20,
            pendingWorkCount = 2,
        ),
        urgentAlerts = listOf(
            UrgentAlert(
                title = "Mathematics score dropped",
                detail = "58% in Weekly Test 11, down from 66% in the previous test",
                severity = AlertSeverity.WARNING,
            ),
        ),
        attendance = AttendanceOverview(
            monthLabel = "October",
            monthPercent = 92,
            present = 46,
            absent = 3,
            late = 1,
            total = 50,
            subjectWise = listOf(
                SubjectAttendance("Physics", 95),
                SubjectAttendance("Chemistry", 90),
                SubjectAttendance("Mathematics", 88),
            ),
            recentAbsences = listOf(
                "Absent — Chemistry, 7 Oct",
                "Late — Mathematics, 3 Oct",
            ),
            trend = TrendDirection.STABLE,
        ),
        latestTest = LatestTest(
            testName = "Weekly Test 11",
            dateLabel = "28 Sep",
            score = 228,
            totalMarks = 300,
            percentage = 76,
            percentile = 94,
            batchRank = 4,
            batchSize = 60,
            subjectSplit = listOf(
                SubjectScore("Physics", 84, PerformanceBand.STRONG),
                SubjectScore("Chemistry", 71, PerformanceBand.GOOD),
                SubjectScore("Mathematics", 58, PerformanceBand.NEEDS_ATTENTION),
            ),
        ),
        performanceTrend = PerformanceTrend(
            last5Tests = listOf(64, 69, 67, 72, 76),
            overallTrend = TrendDirection.IMPROVING,
            subjectTrends = listOf(
                SubjectTrend("Physics", TrendDirection.IMPROVING),
                SubjectTrend("Chemistry", TrendDirection.STABLE),
                SubjectTrend("Mathematics", TrendDirection.DECLINING),
            ),
        ),
        strengths = listOf(
            StrengthConcern("Physics", "Consistently scoring above 80% — strongest subject"),
            StrengthConcern("Attendance", "92% this month, above the batch average"),
        ),
        concerns = listOf(
            StrengthConcern("Mathematics", "Dropped to 58% — trigonometry and rotational dynamics are weak areas"),
            StrengthConcern("Pending work", "2 assignments due this week"),
        ),
        teacherRemarks = listOf(
            TeacherRemark(
                teacherName = "Dr. Rao",
                subject = "Physics",
                remark = "Ishita shows a strong grasp of mechanics. Keep encouraging practice at home.",
                dateLabel = "3 Oct",
            ),
            TeacherRemark(
                teacherName = "Ms. Nair",
                subject = "Mathematics",
                remark = "Needs more practice with trigonometric identities — recommend the extra worksheet.",
                dateLabel = "28 Sep",
            ),
        ),
        upcomingPtm = PtmMeeting(
            dateLabel = "Sat, 18 Oct",
            timeLabel = "4:00 PM",
            mode = "Online",
            agenda = "Discuss Mathematics performance & term plan",
            status = PtmStatus.SCHEDULED,
        ),
        fees = FeeSummary(
            totalFee = 185000,
            paid = 135000,
            due = 50000,
            nextDueDateLabel = "31 Oct 2026",
            paymentHistory = listOf(
                FeePayment("Installment 2", 60000, "12 Jul 2026", receiptAvailable = true),
                FeePayment("Installment 1", 75000, "2 Apr 2026", receiptAvailable = true),
            ),
        ),
        announcements = listOf(
            ParentUpdateAnnouncement(
                title = "Weekly Test 12 timing changed",
                body = "Now starts at 10:00 AM (was 9:00 AM). Report 15 min early.",
                timeLabel = "2h ago",
                category = "Test",
            ),
            ParentUpdateAnnouncement(
                title = "PTM scheduled for 18 Oct",
                body = "A parent-teacher meeting has been scheduled to discuss Mathematics performance.",
                timeLabel = "1d ago",
                category = "PTM",
            ),
            ParentUpdateAnnouncement(
                title = "Fee due reminder",
                body = "₹50,000 is due by 31 Oct 2026 for the Two-Year Classroom Program.",
                timeLabel = "2d ago",
                category = "Fees",
            ),
        ),
    )

    private val yashSnapshot = ChildSnapshot(
        weeklyOverview = WeeklyOverview(
            attendancePercent = 97,
            testsThisWeek = 0,
            averageScorePercent = 81,
            classesAttended = 19,
            classesTotal = 20,
            pendingWorkCount = 0,
        ),
        urgentAlerts = emptyList(),
        attendance = AttendanceOverview(
            monthLabel = "October",
            monthPercent = 97,
            present = 29,
            absent = 1,
            late = 0,
            total = 30,
            subjectWise = listOf(
                SubjectAttendance("Science", 98),
                SubjectAttendance("Mathematics", 96),
            ),
            recentAbsences = listOf("Absent — Science, 29 Sep"),
            trend = TrendDirection.IMPROVING,
        ),
        latestTest = LatestTest(
            testName = "Foundation Test 6",
            dateLabel = "30 Sep",
            score = 162,
            totalMarks = 200,
            percentage = 81,
            percentile = 88,
            batchRank = 6,
            batchSize = 40,
            subjectSplit = listOf(
                SubjectScore("Science", 85, PerformanceBand.STRONG),
                SubjectScore("Mathematics", 77, PerformanceBand.GOOD),
            ),
        ),
        performanceTrend = PerformanceTrend(
            last5Tests = listOf(74, 76, 78, 79, 81),
            overallTrend = TrendDirection.IMPROVING,
            subjectTrends = listOf(
                SubjectTrend("Science", TrendDirection.IMPROVING),
                SubjectTrend("Mathematics", TrendDirection.STABLE),
            ),
        ),
        strengths = listOf(
            StrengthConcern("Science", "Consistently improving — up 7% over the last 5 tests"),
        ),
        concerns = listOf(
            StrengthConcern("Mathematics", "Accuracy dips in word-problem sections"),
        ),
        teacherRemarks = listOf(
            TeacherRemark(
                teacherName = "Mr. Shetty",
                subject = "Science",
                remark = "Yash is curious and asks great questions in class.",
                dateLabel = "25 Sep",
            ),
        ),
        upcomingPtm = null,
        fees = FeeSummary(
            totalFee = 95000,
            paid = 95000,
            due = 0,
            nextDueDateLabel = "Fully paid",
            paymentHistory = listOf(
                FeePayment("Full payment", 95000, "10 Apr 2026", receiptAvailable = true),
            ),
        ),
        announcements = listOf(
            ParentUpdateAnnouncement(
                title = "Foundation batch timing change",
                body = "Science moves to 8:30 AM from Monday.",
                timeLabel = "1d ago",
                category = "Notice",
            ),
        ),
    )

    val dashboard = ParentDashboard(
        parent = ParentProfile(name = "Deepak Verma", phone = "98765 43210", notifications = 2),
        children = listOf(ishita, yash),
        childData = mapOf(
            ishita.id to ishitaSnapshot,
            yash.id to yashSnapshot,
        ),
    )
}
