package com.rankridge.app.ui.assistant

import com.rankridge.app.ui.auth.UserRole

/** Who wrote a line in the conversation. */
enum class ChatAuthor { USER, ASSISTANT }

/**
 * One message in the assistant conversation. [imageUri] is a content-URI string when the student
 * attached a photo; [text] may be blank in that case. UI-only — nothing here is persisted.
 */
data class ChatMessage(
    val id: Long,
    val author: ChatAuthor,
    val text: String,
    val imageUri: String? = null,
)

/**
 * A tappable suggestion shown above the input. [id] keys the scripted answer in
 * [MockAssistantEngine], so a chip always produces a sensible reply with no typing.
 */
data class QuickReply(val id: String, val label: String)

/**
 * Everything the assistant needs to present itself for one role. The assistant is deliberately
 * role-aware: a teacher's first question ("what should I act on next?") is not a student's
 * ("what should I learn next?"), so the greeting and the starter chips differ per role, mirroring
 * the per-role dashboard design in CLAUDE.md.
 */
data class AssistantProfile(
    val assistantName: String,
    val tagline: String,
    val greeting: String,
    val inputHint: String,
    val quickReplies: List<QuickReply>,
)

/** The assistant's display name, shown in the sheet header and on every assistant bubble. */
const val ASSISTANT_NAME = "Ridge AI"

/** Resolve the role-specific presentation for the floating assistant. */
fun assistantProfileFor(role: UserRole): AssistantProfile = when (role) {
    UserRole.STUDENT -> AssistantProfile(
        assistantName = ASSISTANT_NAME,
        tagline = "Your study helper",
        greeting = "Hi! I'm $ASSISTANT_NAME, your study buddy. Ask me to explain a topic, work " +
            "through a doubt, or point you at what to practise. Snap a photo of a question and " +
            "I'll take a look.",
        inputHint = "Ask a doubt or topic...",
        quickReplies = listOf(
            QuickReply("explain_topic", "Explain a topic"),
            QuickReply("solve_doubt", "Solve a doubt"),
            QuickReply("weak_areas", "My weak areas"),
            QuickReply("today_plan", "What's next today?"),
        ),
    )
    UserRole.TEACHER -> AssistantProfile(
        assistantName = ASSISTANT_NAME,
        tagline = "Your teaching assistant",
        greeting = "Hi! I'm $ASSISTANT_NAME. I can summarise your day, flag the batch or student " +
            "that needs you, or help you draft a reply to a doubt. What would help right now?",
        inputHint = "Ask about a batch or student...",
        quickReplies = listOf(
            QuickReply("teacher_day", "Summarise my day"),
            QuickReply("batch_attention", "Which batch needs me?"),
            QuickReply("draft_doubt_reply", "Draft a doubt reply"),
            QuickReply("students_risk", "Who's at risk?"),
        ),
    )
    UserRole.PARENT -> AssistantProfile(
        assistantName = ASSISTANT_NAME,
        tagline = "Your child's progress helper",
        greeting = "Hi! I'm $ASSISTANT_NAME. Ask me how your child is doing, whether anything " +
            "needs attention, or help getting ready for a parent-teacher meeting.",
        inputHint = "Ask about your child...",
        quickReplies = listOf(
            QuickReply("child_summary", "How is my child doing?"),
            QuickReply("alerts", "Anything urgent?"),
            QuickReply("attendance_summary", "Attendance summary"),
            QuickReply("ptm_prep", "Prep for PTM"),
        ),
    )
    UserRole.ACADEMIC_HEAD -> AssistantProfile(
        assistantName = ASSISTANT_NAME,
        tagline = "Your delivery-quality helper",
        greeting = "Hi! I'm $ASSISTANT_NAME. I can give you a read on delivery health, flag " +
            "faculty who need support, or surface at-risk batches and compliance gaps.",
        inputHint = "Ask about delivery or faculty...",
        quickReplies = listOf(
            QuickReply("delivery_health", "Delivery health today"),
            QuickReply("faculty_support", "Who needs support?"),
            QuickReply("at_risk_batches", "At-risk batches"),
            QuickReply("compliance_gaps", "Compliance gaps"),
        ),
    )
    UserRole.COUNSELLOR -> AssistantProfile(
        assistantName = ASSISTANT_NAME,
        tagline = "Your admissions helper",
        greeting = "Hi! I'm $ASSISTANT_NAME. I can line up today's follow-ups, point you at the " +
            "hottest leads, or draft a message to a parent. Where do you want to start?",
        inputHint = "Ask about leads or follow-ups...",
        quickReplies = listOf(
            QuickReply("todays_followups", "Today's follow-ups"),
            QuickReply("hot_leads", "Hot leads"),
            QuickReply("draft_whatsapp", "Draft a WhatsApp reply"),
            QuickReply("admission_status", "Admission status"),
        ),
    )
    UserRole.MENTOR -> AssistantProfile(
        assistantName = ASSISTANT_NAME,
        tagline = "Your mentoring helper",
        greeting = "Hi! I'm $ASSISTANT_NAME. I can tell you which mentees need you, surface " +
            "attendance alerts, or help you draft a check-in message. What's on your mind?",
        inputHint = "Ask about a mentee...",
        quickReplies = listOf(
            QuickReply("mentees_attention", "Who needs me?"),
            QuickReply("attendance_alerts", "Attendance alerts"),
            QuickReply("draft_checkin", "Draft a check-in"),
            QuickReply("review_schedule", "Review schedule"),
        ),
    )
    // The assistant is not attached to the Admin console (web dashboard placeholder), so this is
    // never reached in navigation; a neutral profile keeps the mapping exhaustive and total.
    UserRole.ADMIN -> AssistantProfile(
        assistantName = ASSISTANT_NAME,
        tagline = "Operations helper",
        greeting = "Hi! I'm $ASSISTANT_NAME.",
        inputHint = "Ask a question...",
        quickReplies = emptyList(),
    )
}
