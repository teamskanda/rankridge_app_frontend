package com.rankridge.app.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rankridge.app.core.validation.Validators
import com.rankridge.app.ui.auth.UserRole
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** UI-only: validates the phone number and simulates an OTP request (no backend in this demo). */
class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onPhoneChange(value: String) {
        val digits = value.filter(Char::isDigit).take(Validators.PHONE_LENGTH)
        _uiState.update { it.copy(phone = digits, phoneError = null) }
    }

    fun onRoleChange(role: UserRole) {
        _uiState.update { it.copy(role = role) }
    }

    fun onSendOtp() {
        val state = _uiState.value
        if (state.isSubmitting) return

        val phoneError = Validators.validatePhone(state.phone)
        if (phoneError != null) {
            _uiState.update { it.copy(phoneError = phoneError) }
            return
        }

        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            delay(SIMULATED_DELAY_MS) // mimic a network round-trip for the spinner
            _uiState.update { it.copy(isSubmitting = false, otpSentForPhone = it.phone) }
        }
    }

    fun onOtpNavigationHandled() {
        _uiState.update { it.copy(otpSentForPhone = null) }
    }

    private companion object {
        const val SIMULATED_DELAY_MS = 600L
    }
}
