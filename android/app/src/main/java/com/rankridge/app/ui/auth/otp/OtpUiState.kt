package com.rankridge.app.ui.auth.otp

data class OtpUiState(
    val otp: String = "",
    val otpError: String? = null,
    val isSubmitting: Boolean = false,
    val secondsUntilResend: Int = 0,
    /** Set true once the (simulated) verification succeeds, to trigger navigation. */
    val verified: Boolean = false,
)
