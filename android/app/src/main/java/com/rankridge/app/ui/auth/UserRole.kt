package com.rankridge.app.ui.auth

/** Roles offered in the sign-up selector (UI-only; no backend in this demo). */
enum class UserRole(val displayName: String) {
    STUDENT("Student"),
    TEACHER("Teacher"),
    PARENT("Parent"),
    ACADEMIC_HEAD("Academic Head"),
    ADMIN("Admin"),
    COUNSELLOR("Counsellor"),
    MENTOR("Mentor"),
}
