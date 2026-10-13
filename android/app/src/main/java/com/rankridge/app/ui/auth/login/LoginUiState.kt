package com.rankridge.app.ui.auth.login

import com.rankridge.app.ui.auth.UserRole

data class LoginUiState(
    val phone: String = "",
    val phoneError: String? = null,
    val role: UserRole = UserRole.STUDENT,
    val isSubmitting: Boolean = false,
    /** Set to the entered phone once the (simulated) OTP request succeeds, to trigger navigation. */
    val otpSentForPhone: String? = null,
)
