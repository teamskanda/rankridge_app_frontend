package com.rankridge.app.ui.mentor

/**
 * UI-only data models for the Mentor dashboard (CLAUDE.md: "Assigned students, attendance alerts,
 * test trend, study consistency, weak subjects, missed work, parent follow-ups, mentor notes, next
 * review date"). A mentor's job is triage and follow-through on a small, fixed roster — not
 * teaching a batch or running the institute — so the shape here is a flat list of mentees plus the
 * follow-up/notes workflow around them, not courses, batches or syllabus. No backend in this demo —
 * [MentorMockData] stands in, surfaced via [MentorDashboardViewModel].
 */

enum class RiskLevel { HIGH, MODERATE, LOW }

enum class MentorTrend { IMPROVING, STABLE, DECLINING }

enum class FollowUpStatus { PENDING, DONE }

data class MentorProfile(
    val name: String,
    val phone: String,
    val notifications: Int = 0,
) {
    val initials: String
        get() = name.trim().split(" ").filter { it.isNotEmpty() }.take(2)
            .joinToString("") { it.first().uppercase() }
}

data class MenteeStudent(
    val id: String,
    val name: String,
    val course: String,
    val classYear: String,
    val batch: String,
    val riskLevel: RiskLevel,
    val riskReason: String,
    val attendancePercent: Int,
    val avgScorePercent: Int,
    val testTrend: List<Int>,
    val overallTrend: MentorTrend,
    val weakSubjects: List<String>,
    val missedAssignments: Int,
    val lastReviewLabel: String,
    val nextReviewLabel: String,
    val parentName: String,
    val parentPhone: String,
) {
    val initials: String
        get() = name.trim().split(" ").filter { it.isNotEmpty() }.take(2)
            .joinToString("") { it.first().uppercase() }
}

data class MentorNote(
    val id: String,
    val dateLabel: String,
    val text: String,
)

data class ParentFollowUp(
    val id: String,
    val studentId: String,
    val studentName: String,
    val dueLabel: String,
    val agenda: String,
    val status: FollowUpStatus,
)

data class MentorDashboard(
    val mentor: MentorProfile,
    val mentees: List<MenteeStudent>,
    val notesByStudent: Map<String, List<MentorNote>>,
    val followUps: List<ParentFollowUp>,
)

/** Static sample content standing in for the backend. Realistic coaching-institute data. */
object MentorMockData {
    private val aarav = MenteeStudent(
        id = "aarav",
        name = "Aarav Sharma",
        course = "Two-Year Classroom Program (JEE)",
        classYear = "Class XI",
        batch = "Nucleus XI-A",
        riskLevel = RiskLevel.LOW,
        riskReason = "Consistent attendance and steady score improvement",
        attendancePercent = 94,
        avgScorePercent = 78,
        testTrend = listOf(68, 71, 74, 76, 78),
        overallTrend = MentorTrend.IMPROVING,
        weakSubjects = listOf("Organic Chemistry"),
        missedAssignments = 0,
        lastReviewLabel = "28 Sep",
        nextReviewLabel = "19 Oct",
        parentName = "Rakesh Sharma",
        parentPhone = "91234 56780",
    )
    private val ishita = MenteeStudent(
        id = "ishita",
        name = "Ishita Verma",
        course = "Two-Year Classroom Program (JEE)",
        classYear = "Class XI",
        batch = "Nucleus XI-A",
        riskLevel = RiskLevel.MODERATE,
        riskReason = "Mathematics score dipped two tests in a row",
        attendancePercent = 92,
        avgScorePercent = 71,
        testTrend = listOf(70, 68, 66, 64, 62),
        overallTrend = MentorTrend.DECLINING,
        weakSubjects = listOf("Mathematics — Trigonometry"),
        missedAssignments = 1,
        lastReviewLabel = "3 Oct",
        nextReviewLabel = "14 Oct",
        parentName = "Deepak Verma",
        parentPhone = "98765 43210",
    )
    private val kabir = MenteeStudent(
        id = "kabir",
        name = "Kabir Mehta",
        course = "One-Year Target Program (NEET)",
        classYear = "Class XII",
        batch = "Zenith XII-B",
        riskLevel = RiskLevel.HIGH,
        riskReason = "Attendance fell below 70% and two tests missed",
        attendancePercent = 64,
        avgScorePercent = 48,
        testTrend = listOf(58, 54, 51, 49, 46),
        overallTrend = MentorTrend.DECLINING,
        weakSubjects = listOf("Biology — Genetics", "Physics — Optics"),
        missedAssignments = 4,
        lastReviewLabel = "20 Sep",
        nextReviewLabel = "12 Oct",
        parentName = "Sunita Mehta",
        parentPhone = "99887 76655",
    )
    private val priya = MenteeStudent(
        id = "priya",
        name = "Priya Nair",
        course = "One-Year Target Program (NEET)",
        classYear = "Class XII",
        batch = "Zenith XII-B",
        riskLevel = RiskLevel.HIGH,
        riskReason = "Repeated low scores in Chemistry, no PTM in 6 weeks",
        attendancePercent = 79,
        avgScorePercent = 52,
        testTrend = listOf(60, 57, 55, 53, 52),
        overallTrend = MentorTrend.DECLINING,
        weakSubjects = listOf("Chemistry — Equilibrium"),
        missedAssignments = 2,
        lastReviewLabel = "25 Sep",
        nextReviewLabel = "13 Oct",
        parentName = "Ramesh Nair",
        parentPhone = "90909 80808",
    )
    private val yash = MenteeStudent(
        id = "yash",
        name = "Yash Verma",
        course = "Foundation Program",
        classYear = "Class IX",
        batch = "Foundation IX-B",
        riskLevel = RiskLevel.LOW,
        riskReason = "Strong, consistent performer across subjects",
        attendancePercent = 97,
        avgScorePercent = 81,
        testTrend = listOf(74, 76, 78, 79, 81),
        overallTrend = MentorTrend.IMPROVING,
        weakSubjects = emptyList(),
        missedAssignments = 0,
        lastReviewLabel = "1 Oct",
        nextReviewLabel = "22 Oct",
        parentName = "Deepak Verma",
        parentPhone = "98765 43210",
    )

    val dashboard = MentorDashboard(
        mentor = MentorProfile(name = "Ananya Krishnan", phone = "90123 45678", notifications = 3),
        mentees = listOf(kabir, priya, ishita, aarav, yash),
        notesByStudent = mapOf(
            "kabir" to listOf(
                MentorNote("k1", "20 Sep", "Spoke with Kabir about attendance. He cited a family issue — following up with parents."),
            ),
            "priya" to listOf(
                MentorNote("p1", "25 Sep", "Recommended extra Chemistry doubt sessions on weekends. Priya agreed to try one this week."),
            ),
            "ishita" to listOf(
                MentorNote("i1", "3 Oct", "Shared trigonometry practice set. Parent aware and supportive."),
            ),
        ),
        followUps = listOf(
            ParentFollowUp(
                id = "f1",
                studentId = "kabir",
                studentName = "Kabir Mehta",
                dueLabel = "Today",
                agenda = "Discuss attendance drop and missed tests with Mrs. Mehta",
                status = FollowUpStatus.PENDING,
            ),
            ParentFollowUp(
                id = "f2",
                studentId = "priya",
                studentName = "Priya Nair",
                dueLabel = "Tomorrow",
                agenda = "Review Chemistry support plan with Mr. Nair",
                status = FollowUpStatus.PENDING,
            ),
            ParentFollowUp(
                id = "f3",
                studentId = "ishita",
                studentName = "Ishita Verma",
                dueLabel = "14 Oct",
                agenda = "Mathematics trend check-in — scheduled PTM already covers this",
                status = FollowUpStatus.PENDING,
            ),
            ParentFollowUp(
                id = "f4",
                studentId = "yash",
                studentName = "Yash Verma",
                dueLabel = "30 Sep",
                agenda = "Routine monthly update — shared progress summary",
                status = FollowUpStatus.DONE,
            ),
        ),
    )
}
