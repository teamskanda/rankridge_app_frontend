package com.rankridge.app.core.validation

/**
 * Pure-Kotlin input validation (no Android dependencies, so it is unit-testable on the JVM).
 * Each function returns an error message, or null when the value is valid.
 */
object Validators {

    const val PHONE_LENGTH = 10
    const val OTP_LENGTH = 6

    // Indian mobile numbers: 10 digits starting 6-9.
    private val PHONE_REGEX = Regex("^[6-9]\\d{9}$")

    fun validateFullName(value: String): String? = when {
        value.isBlank() -> "Please enter your name."
        value.trim().length < 2 -> "Name must be at least 2 characters."
        else -> null
    }

    fun validatePhone(value: String): String? = when {
        value.isBlank() -> "Please enter your phone number."
        !PHONE_REGEX.matches(value.trim()) -> "Enter a valid 10-digit mobile number."
        else -> null
    }

    fun validateOtp(value: String): String? = when {
        value.isBlank() -> "Please enter the OTP."
        value.length < OTP_LENGTH -> "Enter the $OTP_LENGTH-digit code."
        value.any { !it.isDigit() } -> "OTP must contain digits only."
        else -> null
    }
}
