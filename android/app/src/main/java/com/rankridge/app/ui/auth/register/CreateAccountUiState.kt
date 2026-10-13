package com.rankridge.app.ui.auth.register

import com.rankridge.app.ui.auth.UserRole

data class CreateAccountUiState(
    val fullName: String = "",
    val role: UserRole = UserRole.STUDENT,
    val phone: String = "",
    val fullNameError: String? = null,
    val phoneError: String? = null,
    val isSubmitting: Boolean = false,
    /** Set to the entered phone once the (simulated) OTP request succeeds, to trigger navigation. */
    val otpSentForPhone: String? = null,
)
