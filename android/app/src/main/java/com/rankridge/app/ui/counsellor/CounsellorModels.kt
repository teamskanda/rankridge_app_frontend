package com.rankridge.app.ui.counsellor

/**
 * UI-only data models for the Counsellor dashboard (CLAUDE.md: "New leads, today's follow-ups,
 * applications, admissions, student/parent contact, interested course, branch, lead source,
 * status, next follow-up, call/WhatsApp/note/schedule-counselling actions"). A counsellor's job is
 * admissions pipeline triage — leads in, follow-ups done, applications pushed to admission — not
 * teaching or academic monitoring, so the shape here is a lead-and-application pipeline, not
 * courses or syllabus. No backend in this demo — [CounsellorMockData] stands in, surfaced via
 * [CounsellorDashboardViewModel].
 */

enum class LeadStatus { NEW, CONTACTED, FOLLOW_UP, INTERESTED, APPLICATION_SUBMITTED, ADMITTED, LOST }

enum class LeadPriority { HIGH, MEDIUM, LOW }

enum class LeadSource { WEBSITE, REFERRAL, WALK_IN, PHONE_INQUIRY, SOCIAL_MEDIA }

enum class ApplicationStatus { DOCUMENTS_PENDING, UNDER_REVIEW, APPROVED, ADMITTED }

data class CounsellorProfile(
    val name: String,
    val phone: String,
    val branch: String,
    val notifications: Int = 0,
) {
    val initials: String
        get() = name.trim().split(" ").filter { it.isNotEmpty() }.take(2)
            .joinToString("") { it.first().uppercase() }
}

data class Lead(
    val id: String,
    val name: String,
    val phone: String,
    val courseInterested: String,
    val branch: String,
    val source: LeadSource,
    val status: LeadStatus,
    val priority: LeadPriority,
    val createdLabel: String,
    val lastContactedLabel: String,
    val nextFollowUpLabel: String,
    val parentName: String? = null,
) {
    val initials: String
        get() = name.trim().split(" ").filter { it.isNotEmpty() }.take(2)
            .joinToString("") { it.first().uppercase() }
}

data class LeadNote(
    val id: String,
    val dateLabel: String,
    val text: String,
)

data class AdmissionApplication(
    val id: String,
    val leadId: String,
    val studentName: String,
    val course: String,
    val branch: String,
    val status: ApplicationStatus,
    val submittedLabel: String,
    val documentsStatus: String,
    val feeStatus: String,
)

data class CounsellorMetrics(
    val newLeadsToday: Int,
    val followUpsToday: Int,
    val applicationsPending: Int,
    val admissionsThisMonth: Int,
    val conversionRatePercent: Int,
)

data class CounsellorDashboard(
    val counsellor: CounsellorProfile,
    val metrics: CounsellorMetrics,
    val leads: List<Lead>,
    val notesByLead: Map<String, List<LeadNote>>,
    val applications: List<AdmissionApplication>,
)

/** Static sample content standing in for the backend. Realistic coaching-institute admissions data. */
object CounsellorMockData {
    private val kabirLead = Lead(
        id = "lead_kabir",
        name = "Kabir Mehta",
        phone = "99887 76655",
        courseInterested = "One-Year Target Program (NEET)",
        branch = "Rankridge — HSR Layout",
        source = LeadSource.REFERRAL,
        status = LeadStatus.ADMITTED,
        priority = LeadPriority.LOW,
        createdLabel = "2 Aug",
        lastContactedLabel = "18 Aug",
        nextFollowUpLabel = "—",
        parentName = "Sunita Mehta",
    )
    private val tanviLead = Lead(
        id = "lead_tanvi",
        name = "Tanvi Rao",
        phone = "98450 11223",
        courseInterested = "Two-Year Classroom Program (JEE)",
        branch = "Rankridge — HSR Layout",
        source = LeadSource.WEBSITE,
        status = LeadStatus.NEW,
        priority = LeadPriority.HIGH,
        createdLabel = "Today",
        lastContactedLabel = "Not contacted yet",
        nextFollowUpLabel = "Today",
    )
    private val arjunLead = Lead(
        id = "lead_arjun",
        name = "Arjun Nambiar",
        phone = "97123 44556",
        courseInterested = "Foundation Program",
        branch = "Rankridge — Koramangala",
        source = LeadSource.WALK_IN,
        status = LeadStatus.FOLLOW_UP,
        priority = LeadPriority.MEDIUM,
        createdLabel = "6 Oct",
        lastContactedLabel = "8 Oct",
        nextFollowUpLabel = "Today",
        parentName = "Vinod Nambiar",
    )
    private val meeraLead = Lead(
        id = "lead_meera",
        name = "Meera Iyer",
        phone = "96654 33221",
        courseInterested = "One-Year Target Program (NEET)",
        branch = "Rankridge — Koramangala",
        source = LeadSource.PHONE_INQUIRY,
        status = LeadStatus.INTERESTED,
        priority = LeadPriority.HIGH,
        createdLabel = "2 Oct",
        lastContactedLabel = "9 Oct",
        nextFollowUpLabel = "Tomorrow",
    )
    private val rohanLead = Lead(
        id = "lead_rohan",
        name = "Rohan Gupta",
        phone = "95780 22114",
        courseInterested = "Two-Year Classroom Program (JEE)",
        branch = "Rankridge — HSR Layout",
        source = LeadSource.SOCIAL_MEDIA,
        status = LeadStatus.APPLICATION_SUBMITTED,
        priority = LeadPriority.MEDIUM,
        createdLabel = "28 Sep",
        lastContactedLabel = "10 Oct",
        nextFollowUpLabel = "16 Oct",
    )
    private val divyaLead = Lead(
        id = "lead_divya",
        name = "Divya Krishnan",
        phone = "94567 88990",
        courseInterested = "Foundation Program",
        branch = "Rankridge — Koramangala",
        source = LeadSource.WEBSITE,
        status = LeadStatus.CONTACTED,
        priority = LeadPriority.LOW,
        createdLabel = "4 Oct",
        lastContactedLabel = "5 Oct",
        nextFollowUpLabel = "17 Oct",
    )
    private val sameerLead = Lead(
        id = "lead_sameer",
        name = "Sameer Khan",
        phone = "93456 77889",
        courseInterested = "One-Year Target Program (NEET)",
        branch = "Rankridge — HSR Layout",
        source = LeadSource.REFERRAL,
        status = LeadStatus.LOST,
        priority = LeadPriority.LOW,
        createdLabel = "15 Sep",
        lastContactedLabel = "22 Sep",
        nextFollowUpLabel = "—",
    )

    val dashboard = CounsellorDashboard(
        counsellor = CounsellorProfile(
            name = "Pooja Reddy",
            phone = "90876 54321",
            branch = "Rankridge — HSR Layout",
            notifications = 4,
        ),
        metrics = CounsellorMetrics(
            newLeadsToday = 1,
            followUpsToday = 2,
            applicationsPending = 2,
            admissionsThisMonth = 6,
            conversionRatePercent = 34,
        ),
        leads = listOf(tanviLead, arjunLead, meeraLead, rohanLead, divyaLead, kabirLead, sameerLead),
        notesByLead = mapOf(
            "lead_arjun" to listOf(
                LeadNote("n1", "8 Oct", "Spoke with Mr. Nambiar — wants fee structure and scholarship test details before deciding."),
            ),
            "lead_meera" to listOf(
                LeadNote("n2", "9 Oct", "Very interested, comparing with one other institute. Sent brochure and faculty profile on WhatsApp."),
            ),
            "lead_rohan" to listOf(
                LeadNote("n3", "28 Sep", "Application submitted online. Awaiting document verification."),
                LeadNote("n4", "10 Oct", "Followed up — documents received, scheduling admission test."),
            ),
            "lead_kabir" to listOf(
                LeadNote("n5", "18 Aug", "Admitted to Zenith XII-B. Fee payment completed in full."),
            ),
        ),
        applications = listOf(
            AdmissionApplication(
                id = "app_rohan",
                leadId = "lead_rohan",
                studentName = "Rohan Gupta",
                course = "Two-Year Classroom Program (JEE)",
                branch = "Rankridge — HSR Layout",
                status = ApplicationStatus.UNDER_REVIEW,
                submittedLabel = "28 Sep",
                documentsStatus = "All documents received",
                feeStatus = "Registration fee paid",
            ),
            AdmissionApplication(
                id = "app_meera",
                leadId = "lead_meera",
                studentName = "Meera Iyer",
                course = "One-Year Target Program (NEET)",
                branch = "Rankridge — Koramangala",
                status = ApplicationStatus.DOCUMENTS_PENDING,
                submittedLabel = "9 Oct",
                documentsStatus = "Awaiting marksheet upload",
                feeStatus = "Not paid yet",
            ),
            AdmissionApplication(
                id = "app_kabir",
                leadId = "lead_kabir",
                studentName = "Kabir Mehta",
                course = "One-Year Target Program (NEET)",
                branch = "Rankridge — HSR Layout",
                status = ApplicationStatus.ADMITTED,
                submittedLabel = "10 Aug",
                documentsStatus = "Verified",
                feeStatus = "Paid in full",
            ),
        ),
    )
}
