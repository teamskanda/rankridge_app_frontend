package com.rankridge.app.ui.auth.register

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

/** UI-only: validates the form and simulates an OTP request (no backend in this demo). */
class CreateAccountViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CreateAccountUiState())
    val uiState: StateFlow<CreateAccountUiState> = _uiState.asStateFlow()

    fun onFullNameChange(value: String) {
        _uiState.update { it.copy(fullName = value, fullNameError = null) }
    }

    fun onRoleChange(role: UserRole) {
        _uiState.update { it.copy(role = role) }
    }

    fun onPhoneChange(value: String) {
        val digits = value.filter(Char::isDigit).take(Validators.PHONE_LENGTH)
        _uiState.update { it.copy(phone = digits, phoneError = null) }
    }

    fun onSendOtp() {
        val state = _uiState.value
        if (state.isSubmitting) return

        val fullNameError = Validators.validateFullName(state.fullName)
        val phoneError = Validators.validatePhone(state.phone)
        if (fullNameError != null || phoneError != null) {
            _uiState.update { it.copy(fullNameError = fullNameError, phoneError = phoneError) }
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
