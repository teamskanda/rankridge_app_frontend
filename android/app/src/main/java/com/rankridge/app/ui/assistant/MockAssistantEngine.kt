package com.rankridge.app.ui.assistant

import com.rankridge.app.ui.auth.UserRole
import kotlin.random.Random

/**
 * The single place that turns a student/teacher/parent message into an assistant reply.
 *
 * This is a UI-only mock: there is no model and no backend. It matches the tapped quick-reply id
 * (or keywords in free text) to a small table of scripted, role-flavoured answers, and picks one
 * phrasing at random so repeat testing doesn't read as robotic. When a real assistant backend
 * lands, replace [reply] with a streaming API call — nothing else in the assistant UI needs to
 * change, since the whole surface is written against [ChatMessage].
 */
object MockAssistantEngine {

    /**
     * Produce the assistant's answer. [key] is a quick-reply id when the user tapped a chip, or
     * the raw typed text otherwise. [hasImage] is true when the user attached a photo.
     */
    fun reply(role: UserRole, key: String, hasImage: Boolean): String {
        if (hasImage) return oneOf(imageResponses(role))
        val resolved = resolveKey(role, key)
        val table = responseTable(role)
        return oneOf(table[resolved] ?: fallback(role))
    }

    private fun oneOf(options: List<String>): String =
        options[Random.nextInt(options.size)]

    /** Map either a quick-reply id or free text onto a known response key. */
    private fun resolveKey(role: UserRole, key: String): String {
        if (responseTable(role).containsKey(key)) return key
        val t = key.lowercase()
        fun has(vararg words: String) = words.any { t.contains(it) }
        return when {
            has("weak", "struggl", "bad at", "improve") -> "weak_areas"
            has("explain", "what is", "concept", "understand", "teach me") -> "explain_topic"
            has("doubt", "solve", "answer", "how do i", "stuck") -> "solve_doubt"
            has("today", "next", "schedule", "class now", "plan") -> "today_plan"
            has("attend") -> "attendance_summary"
            has("ptm", "meeting", "parent teacher") -> "ptm_prep"
            has("risk", "at risk", "attention") -> "students_risk"
            has("lead") -> "hot_leads"
            has("follow") -> "todays_followups"
            has("draft", "message", "whatsapp", "reply", "write") -> draftKeyFor(role)
            else -> "__fallback__"
        }
    }

    private fun draftKeyFor(role: UserRole): String = when (role) {
        UserRole.TEACHER -> "draft_doubt_reply"
        UserRole.COUNSELLOR -> "draft_whatsapp"
        UserRole.MENTOR -> "draft_checkin"
        else -> "__fallback__"
    }

    private fun imageResponses(role: UserRole): List<String> = when (role) {
        UserRole.STUDENT -> listOf(
            "Got it — this looks like a Rotational Motion problem. Start by drawing the free-body " +
                "diagram, then apply torque = I x alpha about the pivot. Want me to walk through " +
                "it step by step?",
            "Nice, I can read the question. This is a quadratic — try factoring first, and if the " +
                "roots aren't clean, fall back to the quadratic formula. Shall I solve it with you?",
            "Thanks for the photo. This is an organic chemistry mechanism. Identify the " +
                "nucleophile and the leaving group first — that usually tells you the whole path. " +
                "Want the stepwise mechanism?",
        )
        UserRole.TEACHER -> listOf(
            "I can see the submission. The working is right up to the second step, then a sign " +
                "error creeps in. Worth flagging that to the batch — want me to draft a short note?",
        )
        UserRole.PARENT -> listOf(
            "Thanks for the photo. This looks like your child's test paper. The marks lost are " +
                "mostly on one topic — I'd raise it gently at the next PTM. Want talking points?",
        )
        else -> listOf(
            "Thanks, I can see the image. Tell me what you'd like me to do with it and I'll help.",
        )
    }

    private fun fallback(role: UserRole): List<String> = when (role) {
        UserRole.STUDENT -> listOf(
            "I can help with that. Could you tell me the subject or topic so I can be specific? " +
                "You can also tap one of the suggestions above.",
            "Happy to help! Give me a topic or paste the question, and I'll break it down. " +
                "Remember your teacher can confirm anything tricky in the doubt center.",
        )
        else -> listOf(
            "I can help with that. Could you add a little more detail, or tap one of the " +
                "suggestions above to get started?",
        )
    }

    // The honesty line: the assistant is a first-pass helper, not a replacement for the teacher,
    // matching the doubt-center flow in CLAUDE.md where a human still resolves the doubt.
    private fun responseTable(role: UserRole): Map<String, List<String>> = when (role) {
        UserRole.STUDENT -> mapOf(
            "explain_topic" to listOf(
                "Sure — which topic? Tell me the chapter (say, Thermodynamics or Integration) " +
                    "and I'll give you the core idea, one worked example, and a quick check.",
                "Let's do it. Name the topic and I'll explain it simply, then show where students " +
                    "usually slip up.",
            ),
            "solve_doubt" to listOf(
                "Go ahead and type the question, or snap a photo of it with the camera icon. I'll " +
                    "work through it step by step rather than just giving the final answer.",
                "Paste the doubt or attach a picture. I'll solve it with you — and if it's a " +
                    "tricky one, your teacher can confirm it in the doubt center.",
            ),
            "weak_areas" to listOf(
                "Based on your recent tests, Rotational Motion and Thermochemistry are your " +
                    "lowest-accuracy topics. I'd start with 10 practice questions on Rotational " +
                    "Motion today. Want me to line those up?",
                "Your last 5 tests point to two soft spots: Current Electricity and Mole Concept. " +
                    "Short daily practice beats cramming here — shall I suggest a 15-minute set?",
            ),
            "today_plan" to listOf(
                "Today you have Physics at 4:00 PM (Rotational Motion) and a Chemistry DPP due " +
                    "tonight. Next test is the Weekly Test on Saturday. Want me to set a reminder?",
                "Up next: your 4:00 PM Physics class, then the pending Chemistry assignment. " +
                    "After that, revise one weak topic for 15 minutes. Want the revision pick?",
            ),
        )
        UserRole.TEACHER -> mapOf(
            "teacher_day" to listOf(
                "You have 3 lectures today, attendance pending for 1 of them, and 2 doubts in the " +
                    "queue. Nucleus XI-A's syllabus is slightly behind. Want me to prioritise?",
            ),
            "batch_attention" to listOf(
                "Nucleus XI-A needs a look: average dipped on the last test and 4 students missed " +
                    "the DPP. Want the student list so you can follow up?",
            ),
            "draft_doubt_reply" to listOf(
                "Here's a starting draft: \"Good question. Remember torque depends on the " +
                    "perpendicular distance, not the full length. Try redrawing r at 90 degrees " +
                    "to F and re-check.\" Want me to adjust the tone?",
            ),
            "students_risk" to listOf(
                "3 students are trending high-risk this week — mostly from a score drop plus " +
                    "attendance under 75%. I'd prioritise a quick call with each. Want their names?",
            ),
        )
        UserRole.PARENT -> mapOf(
            "child_summary" to listOf(
                "This week your child attended 5 of 6 classes, scored above the batch average on " +
                    "the last test, but has one pending assignment. Overall: steady, with one " +
                    "thing to nudge. Want the detail?",
            ),
            "alerts" to listOf(
                "One thing worth noting: attendance slipped to one absence this week, and there's " +
                    "a pending DPP. Nothing urgent, but a gentle reminder at home would help.",
            ),
            "attendance_summary" to listOf(
                "Attendance this month is 92% — 23 of 25 classes. The two misses were both " +
                    "Chemistry. I'd keep an eye on that subject. Want the subject-wise split?",
            ),
            "ptm_prep" to listOf(
                "For the upcoming PTM, three things to raise: the Chemistry dip, the pending " +
                    "assignment, and strong progress in Maths. Want me to turn these into " +
                    "questions you can ask the teacher?",
            ),
        )
        UserRole.ACADEMIC_HEAD -> mapOf(
            "delivery_health" to listOf(
                "Today: 18 of 21 classes held, 2 pending, 1 cancelled. Attendance average 88%, " +
                    "syllabus on-track at 84%. The pending classes are both in the XI batches. " +
                    "Want me to flag the faculty?",
            ),
            "faculty_support" to listOf(
                "Two faculty are showing delayed/unmarked lectures this week. It's usually a " +
                    "marking-habit gap rather than teaching. Want the names and their pending counts?",
            ),
            "at_risk_batches" to listOf(
                "Nucleus XI-A and Pinnacle XII-B are the two at-risk batches: falling test " +
                    "averages plus attendance under 80%. Want the risk reasons broken down?",
            ),
            "compliance_gaps" to listOf(
                "Open compliance items: 3 unmarked lectures, 1 delayed batch, and 2 overdue PTMs. " +
                    "Clearing the unmarked lectures first will lift the on-track number most.",
            ),
        )
        UserRole.COUNSELLOR -> mapOf(
            "todays_followups" to listOf(
                "You have 6 follow-ups due today — 2 are hot leads who asked for a call back. " +
                    "I'd start with those two. Want me to open the first one?",
            ),
            "hot_leads" to listOf(
                "Your hottest leads right now: two JEE enquiries from this week's walk-ins, both " +
                    "marked 'very interested'. Striking while it's warm matters. Want their details?",
            ),
            "draft_whatsapp" to listOf(
                "Here's a draft: \"Hi, thanks for visiting RankRidge today! Sharing the JEE batch " +
                    "details and fee structure we discussed. Happy to answer anything — when works " +
                    "for a quick call?\" Want it shorter?",
            ),
            "admission_status" to listOf(
                "Admissions this week: 4 applications submitted, 2 awaiting documents, 1 fee " +
                    "pending. The documents one has been waiting 3 days — worth a nudge. Want it?",
            ),
        )
        UserRole.MENTOR -> mapOf(
            "mentees_attention" to listOf(
                "2 of your mentees need you this week: one with a sharp test-score drop and one " +
                    "whose attendance fell below 75%. Want to start with the score drop?",
            ),
            "attendance_alerts" to listOf(
                "One mentee has missed 3 classes this week, mostly mornings. Often it's something " +
                    "simple at home. Want me to draft a gentle check-in?",
            ),
            "draft_checkin" to listOf(
                "Here's a draft: \"Hi! Noticed you missed a couple of classes this week — " +
                    "everything okay? I'm here if you want to talk through anything, school or " +
                    "otherwise.\" Want to adjust it?",
            ),
            "review_schedule" to listOf(
                "You have 3 mentor reviews coming up this week. The earliest is tomorrow with your " +
                    "highest-risk mentee. Want me to pull their recent trend before it?",
            ),
        )
        UserRole.ADMIN -> emptyMap()
    }
}
