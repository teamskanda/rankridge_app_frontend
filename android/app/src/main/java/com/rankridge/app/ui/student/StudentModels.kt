package com.rankridge.app.ui.student

import com.rankridge.app.ui.common.PerformanceBand

/**
 * UI-only data models for the Student dashboard. There is no backend in this demo — every screen
 * is driven by the in-memory [StudentMockData] below, surfaced through [StudentDashboardViewModel]
 * with a simulated load delay so the loading/empty states are real and swappable for a repository
 * later. Shapes here deliberately mirror the Student spec in CLAUDE.md (today's plan → continue →
 * upcoming test → latest result → performance → strengths/weaknesses → practice → attendance →
 * announcements) so a real API layer can map onto them 1:1.
 */

enum class Difficulty(val label: String) { EASY("Easy"), MEDIUM("Medium"), HARD("Hard") }

enum class ClassMode(val label: String) { ONLINE("Online"), OFFLINE("In-class") }

enum class TestStatus { UPCOMING, LIVE, COMPLETED }

enum class DoubtStatus(val label: String) { OPEN("Open"), AWAITING_REPLY("Faculty replying"), RESOLVED("Resolved") }

enum class TrendDirection { IMPROVING, STABLE, DECLINING }

data class StudentProfile(
    val name: String,
    val goal: String,
    val course: String,
    val batch: String,
    val classYear: String,
    val notifications: Int,
) {
    val initials: String
        get() = name.trim().split(" ").filter { it.isNotEmpty() }.take(2)
            .joinToString("") { it.first().uppercase() }
}

data class TodayClass(
    val subject: String,
    val topic: String,
    val time: String,
    val location: String,
    val mode: ClassMode,
    val isNext: Boolean = false,
)

data class PendingItem(
    val title: String,
    val subject: String,
    val dueLabel: String,
)

data class ContinueLearning(
    val subject: String,
    val topic: String,
    val progressPercent: Int,
    val resumeAt: String,
)

data class UpcomingTest(
    val name: String,
    val dateLabel: String,
    val durationLabel: String,
    val syllabus: String,
    val totalMarks: Int,
    val status: TestStatus = TestStatus.UPCOMING,
)

data class SubjectScore(
    val subject: String,
    val percent: Int,
    val band: PerformanceBand,
)

data class LatestResult(
    val testName: String,
    val score: Int,
    val totalMarks: Int,
    val percentage: Int,
    val percentile: Int,
    val batchRank: Int,
    val batchSize: Int,
    val overallRank: Int,
    val subjectSplit: List<SubjectScore>,
)

data class PerformanceOverview(
    val testsAttempted: Int,
    val averageScorePercent: Int,
    val attendancePercent: Int,
    val watchTimeHours: Int,
    /** Percentage scores of the last 5 tests, oldest → newest, for the trend sparkline. */
    val last5Trend: List<Int>,
    val trend: TrendDirection,
)

data class TopicAccuracy(
    val topic: String,
    val subject: String,
    val accuracyPercent: Int,
)

data class RecommendedPractice(
    val topic: String,
    val subject: String,
    val questionCount: Int,
    val difficulty: Difficulty,
    val reason: String,
)

data class SubjectAttendance(
    val subject: String,
    val percent: Int,
)

data class AttendanceSummary(
    val monthLabel: String,
    val monthPercent: Int,
    val attended: Int,
    val total: Int,
    val subjectWise: List<SubjectAttendance>,
    val recentNotes: List<String>,
)

data class Announcement(
    val title: String,
    val body: String,
    val timeLabel: String,
    val category: String,
)

data class CourseSubject(
    val name: String,
    val progressPercent: Int,
    val chapters: Int,
    val chaptersDone: Int,
)

data class Course(
    val name: String,
    val subjects: List<CourseSubject>,
)

data class Doubt(
    val question: String,
    val subject: String,
    val status: DoubtStatus,
    val timeLabel: String,
)

/** Daily study streak + today's goal progress, shown as a motivational strip on Home. */
data class StudyStreak(
    val currentDays: Int,
    /** 7 flags, Mon→Sun, true where the daily goal was met. */
    val weekProgress: List<Boolean>,
    val dailyGoalMinutes: Int,
    val doneMinutes: Int,
)

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val scorePercent: Int,
    val isMe: Boolean = false,
)

/** A past test for the Tests-tab history list. */
data class CompletedTest(
    val name: String,
    val dateLabel: String,
    val scorePercent: Int,
    val batchRank: Int,
)

data class Bookmark(
    val title: String,
    val subject: String,
    val type: String,
)

data class DownloadItem(
    val title: String,
    val subject: String,
    val sizeLabel: String,
    val downloaded: Boolean,
)

/** The full home snapshot, assembled once and held by the view model. */
data class StudentDashboard(
    val profile: StudentProfile,
    val todayClasses: List<TodayClass>,
    val pending: List<PendingItem>,
    val continueLearning: ContinueLearning,
    val upcomingTests: List<UpcomingTest>,
    val latestResult: LatestResult,
    val overview: PerformanceOverview,
    val strengths: List<TopicAccuracy>,
    val weaknesses: List<TopicAccuracy>,
    val recommendedPractice: List<RecommendedPractice>,
    val attendance: AttendanceSummary,
    val announcements: List<Announcement>,
    val courses: List<Course>,
    val doubts: List<Doubt>,
    val studyStreak: StudyStreak,
    val leaderboard: List<LeaderboardEntry>,
    val completedTests: List<CompletedTest>,
    val bookmarks: List<Bookmark>,
    val downloads: List<DownloadItem>,
)

/** Static sample content standing in for the backend. Realistic JEE/NEET-style coaching data. */
object StudentMockData {
    val dashboard = StudentDashboard(
        profile = StudentProfile(
            name = "Aarav Sharma",
            goal = "JEE Main + Advanced 2027",
            course = "Two-Year Classroom Program",
            batch = "Nucleus XI-A",
            classYear = "Class XI",
            notifications = 3,
        ),
        todayClasses = listOf(
            TodayClass(
                subject = "Physics",
                topic = "Rotational Motion — Torque & Angular Momentum",
                time = "9:00 AM",
                location = "Room 204",
                mode = ClassMode.OFFLINE,
                isNext = true,
            ),
            TodayClass(
                subject = "Chemistry",
                topic = "Chemical Bonding — Hybridisation",
                time = "11:30 AM",
                location = "Live link",
                mode = ClassMode.ONLINE,
            ),
            TodayClass(
                subject = "Mathematics",
                topic = "Sequences & Series — AP/GP",
                time = "2:00 PM",
                location = "Room 204",
                mode = ClassMode.OFFLINE,
            ),
        ),
        pending = listOf(
            PendingItem("DPP 14 — Kinematics", "Physics", "Due today"),
            PendingItem("Mole Concept worksheet", "Chemistry", "Due tomorrow"),
        ),
        continueLearning = ContinueLearning(
            subject = "Physics",
            topic = "Rotational Motion — Moment of Inertia",
            progressPercent = 62,
            resumeAt = "Resume at 24:10",
        ),
        upcomingTests = listOf(
            UpcomingTest(
                name = "Weekly Test 12",
                dateLabel = "Sun, 12 Oct · 10:00 AM",
                durationLabel = "3 hrs",
                syllabus = "Rotational Motion, Chemical Bonding, Sequences & Series",
                totalMarks = 300,
            ),
            UpcomingTest(
                name = "Part Test 4 (Physics)",
                dateLabel = "Sat, 18 Oct · 9:00 AM",
                durationLabel = "1 hr",
                syllabus = "Mechanics — full",
                totalMarks = 100,
            ),
        ),
        latestResult = LatestResult(
            testName = "Weekly Test 11",
            score = 228,
            totalMarks = 300,
            percentage = 76,
            percentile = 94,
            batchRank = 4,
            batchSize = 60,
            overallRank = 112,
            subjectSplit = listOf(
                SubjectScore("Physics", 84, PerformanceBand.STRONG),
                SubjectScore("Chemistry", 71, PerformanceBand.GOOD),
                SubjectScore("Mathematics", 58, PerformanceBand.NEEDS_ATTENTION),
            ),
        ),
        overview = PerformanceOverview(
            testsAttempted = 11,
            averageScorePercent = 73,
            attendancePercent = 92,
            watchTimeHours = 38,
            last5Trend = listOf(64, 69, 67, 72, 76),
            trend = TrendDirection.IMPROVING,
        ),
        strengths = listOf(
            TopicAccuracy("Newton's Laws", "Physics", 93),
            TopicAccuracy("Stoichiometry", "Chemistry", 88),
            TopicAccuracy("Quadratic Equations", "Mathematics", 85),
        ),
        weaknesses = listOf(
            TopicAccuracy("Rotational Dynamics", "Physics", 41),
            TopicAccuracy("Trigonometric Identities", "Mathematics", 47),
            TopicAccuracy("Thermodynamics", "Chemistry", 52),
        ),
        recommendedPractice = listOf(
            RecommendedPractice(
                topic = "Rotational Dynamics",
                subject = "Physics",
                questionCount = 15,
                difficulty = Difficulty.MEDIUM,
                reason = "Lowest accuracy in last test (41%)",
            ),
            RecommendedPractice(
                topic = "Trigonometric Identities",
                subject = "Mathematics",
                questionCount = 20,
                difficulty = Difficulty.HARD,
                reason = "Repeated errors across 3 DPPs",
            ),
            RecommendedPractice(
                topic = "Thermodynamics",
                subject = "Chemistry",
                questionCount = 12,
                difficulty = Difficulty.EASY,
                reason = "Prerequisite for next week's chapter",
            ),
        ),
        attendance = AttendanceSummary(
            monthLabel = "October",
            monthPercent = 92,
            attended = 46,
            total = 50,
            subjectWise = listOf(
                SubjectAttendance("Physics", 95),
                SubjectAttendance("Chemistry", 90),
                SubjectAttendance("Mathematics", 88),
            ),
            recentNotes = listOf(
                "Absent — Chemistry, 7 Oct",
                "Late — Mathematics, 3 Oct",
            ),
        ),
        announcements = listOf(
            Announcement(
                title = "Weekly Test 12 timing changed",
                body = "Now starts at 10:00 AM (was 9:00 AM). Report 15 min early.",
                timeLabel = "2h ago",
                category = "Test",
            ),
            Announcement(
                title = "New material: Rotational Motion notes",
                body = "Revised notes with solved JEE Advanced PYQs are now in Physics.",
                timeLabel = "Yesterday",
                category = "Material",
            ),
            Announcement(
                title = "Holiday — 15 Oct",
                body = "Institute closed. Scheduled classes move to 16 Oct.",
                timeLabel = "2d ago",
                category = "Notice",
            ),
            Announcement(
                title = "Extra doubt-clearing session",
                body = "Physics doubt session with Dr. Rao on Fri 5 PM, Room 204.",
                timeLabel = "3d ago",
                category = "Class",
            ),
        ),
        courses = listOf(
            Course(
                name = "Two-Year Classroom Program",
                subjects = listOf(
                    CourseSubject("Physics", 48, chapters = 22, chaptersDone = 10),
                    CourseSubject("Chemistry", 41, chapters = 24, chaptersDone = 10),
                    CourseSubject("Mathematics", 36, chapters = 20, chaptersDone = 7),
                ),
            ),
        ),
        doubts = listOf(
            Doubt(
                question = "Why is angular momentum conserved when there's friction at the pivot?",
                subject = "Physics",
                status = DoubtStatus.AWAITING_REPLY,
                timeLabel = "1h ago",
            ),
            Doubt(
                question = "Difference between sp3d and sp3d2 geometry?",
                subject = "Chemistry",
                status = DoubtStatus.RESOLVED,
                timeLabel = "Yesterday",
            ),
            Doubt(
                question = "How to decide which trig substitution to use in integration?",
                subject = "Mathematics",
                status = DoubtStatus.OPEN,
                timeLabel = "2d ago",
            ),
        ),
        studyStreak = StudyStreak(
            currentDays = 12,
            weekProgress = listOf(true, true, true, false, true, true, false),
            dailyGoalMinutes = 120,
            doneMinutes = 78,
        ),
        leaderboard = listOf(
            LeaderboardEntry(1, "Ishaan Mehta", 91),
            LeaderboardEntry(2, "Diya Nair", 88),
            LeaderboardEntry(3, "Kabir Shah", 85),
            LeaderboardEntry(4, "Aarav Sharma", 76, isMe = true),
            LeaderboardEntry(5, "Sara Khan", 74),
        ),
        completedTests = listOf(
            CompletedTest("Weekly Test 11", "28 Sep", 76, batchRank = 4),
            CompletedTest("Weekly Test 10", "21 Sep", 72, batchRank = 6),
            CompletedTest("Part Test 3 (Chemistry)", "16 Sep", 67, batchRank = 9),
            CompletedTest("Weekly Test 9", "14 Sep", 69, batchRank = 7),
            CompletedTest("Weekly Test 8", "7 Sep", 64, batchRank = 11),
        ),
        bookmarks = listOf(
            Bookmark("Torque — solved JEE Adv PYQs", "Physics", "Notes"),
            Bookmark("Hybridisation shortcut table", "Chemistry", "Note"),
            Bookmark("AP/GP formula sheet", "Mathematics", "Formula"),
        ),
        downloads = listOf(
            DownloadItem("Rotational Motion — full lecture", "Physics", "148 MB", downloaded = true),
            DownloadItem("Chemical Bonding notes (PDF)", "Chemistry", "4.2 MB", downloaded = true),
            DownloadItem("Sequences & Series — DPP", "Mathematics", "1.1 MB", downloaded = false),
        ),
    )
}
