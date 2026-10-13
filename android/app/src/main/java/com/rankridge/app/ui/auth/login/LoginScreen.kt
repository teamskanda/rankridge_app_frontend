package com.rankridge.app.ui.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rankridge.app.R
import com.rankridge.app.ui.auth.UserRole
import com.rankridge.app.ui.common.SystemBarAppearance
import com.rankridge.app.ui.auth.components.AuthSwitchRow
import com.rankridge.app.ui.auth.components.AuthTextField
import com.rankridge.app.ui.auth.components.BrandMark
import com.rankridge.app.ui.auth.components.PrimaryButton
import com.rankridge.app.ui.auth.components.RoleDropdown
import com.rankridge.app.ui.theme.BrandWordmark

/**
 * Sign-in: role picker + phone number, then a simulated OTP request.
 *
 * There is deliberately no top app bar. Sign-in is the start destination, so there is nothing to
 * navigate back to, and a bar carrying the wordmark would repeat the brand lockup that already
 * sits above the card. One lockup, on the page rather than inside the form.
 */
@Composable
fun LoginScreen(
    onOtpRequested: (String, UserRole) -> Unit,
    onNavigateToCreateAccount: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(),
) {
    // No branded bar on this screen, so the status bar sits on the page background.
    SystemBarAppearance(lightIcons = isSystemInDarkTheme())

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.otpSentForPhone) {
        val phone = uiState.otpSentForPhone
        if (phone != null) {
            viewModel.onOtpNavigationHandled()
            onOtpRequested(phone, uiState.role)
        }
    }

    Scaffold(modifier = modifier) { innerPadding ->
        LoginContent(
            uiState = uiState,
            onPhoneChange = viewModel::onPhoneChange,
            onRoleChange = viewModel::onRoleChange,
            onSendOtp = viewModel::onSendOtp,
            onNavigateToCreateAccount = onNavigateToCreateAccount,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun LoginContent(
    uiState: LoginUiState,
    onPhoneChange: (String) -> Unit,
    onRoleChange: (UserRole) -> Unit,
    onSendOtp: () -> Unit,
    onNavigateToCreateAccount: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .widthIn(max = 480.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            BrandMark(size = 72.dp)
            Text(
                text = stringResource(R.string.app_name),
                style = BrandWordmark,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 10.dp, bottom = 22.dp),
            )

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.login_title),
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        text = stringResource(R.string.login_subtitle),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 24.dp),
                    )

                    RoleDropdown(
                        selectedRole = uiState.role,
                        onRoleSelected = onRoleChange,
                        label = "I am a",
                        enabled = !uiState.isSubmitting,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    AuthTextField(
                        value = uiState.phone,
                        onValueChange = onPhoneChange,
                        label = "Phone number",
                        leadingIcon = Icons.Filled.Phone,
                        errorText = uiState.phoneError,
                        enabled = !uiState.isSubmitting,
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done,
                        modifier = Modifier.padding(top = 12.dp),
                    )

                    PrimaryButton(
                        text = "Continue",
                        onClick = onSendOtp,
                        loading = uiState.isSubmitting,
                        modifier = Modifier.padding(top = 16.dp),
                    )

                    AuthSwitchRow(
                        prompt = "Don't have an account?",
                        actionText = stringResource(R.string.action_create_account),
                        onActionClick = onNavigateToCreateAccount,
                        enabled = !uiState.isSubmitting,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }

            Text(
                text = stringResource(R.string.login_footer),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 24.dp),
            )
        }
    }
}
