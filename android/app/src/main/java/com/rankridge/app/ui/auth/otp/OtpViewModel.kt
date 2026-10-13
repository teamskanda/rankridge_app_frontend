package com.rankridge.app.ui.auth.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rankridge.app.core.validation.Validators
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI-only OTP verification. No backend: any correctly-formatted 6-digit code is accepted.
 * Manages the resend countdown so the "Resend OTP" action re-enables after [RESEND_SECONDS].
 */
class OtpViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OtpUiState())
    val uiState: StateFlow<OtpUiState> = _uiState.asStateFlow()

    init {
        startResendCountdown()
    }

    fun onOtpChange(value: String) {
        val digits = value.filter(Char::isDigit).take(Validators.OTP_LENGTH)
        _uiState.update { it.copy(otp = digits, otpError = null) }
    }

    fun onVerify() {
        val state = _uiState.value
        if (state.isSubmitting) return

        val otpError = Validators.validateOtp(state.otp)
        if (otpError != null) {
            _uiState.update { it.copy(otpError = otpError) }
            return
        }

        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            delay(SIMULATED_DELAY_MS) // mimic server verification for the spinner
            _uiState.update { it.copy(isSubmitting = false, verified = true) }
        }
    }

    fun onResend() {
        if (_uiState.value.secondsUntilResend > 0) return
        _uiState.update { it.copy(otp = "", otpError = null) }
        startResendCountdown()
    }

    fun onVerifiedHandled() {
        _uiState.update { it.copy(verified = false) }
    }

    private fun startResendCountdown() {
        viewModelScope.launch {
            var remaining = RESEND_SECONDS
            _uiState.update { it.copy(secondsUntilResend = remaining) }
            while (remaining > 0) {
                delay(1_000L)
                remaining--
                _uiState.update { it.copy(secondsUntilResend = remaining) }
            }
        }
    }

    private companion object {
        const val SIMULATED_DELAY_MS = 600L
        const val RESEND_SECONDS = 30
    }
}
