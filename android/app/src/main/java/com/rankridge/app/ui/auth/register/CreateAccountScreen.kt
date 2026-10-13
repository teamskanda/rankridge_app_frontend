package com.rankridge.app.ui.auth.register

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.rankridge.app.ui.auth.components.AuthSwitchRow
import com.rankridge.app.ui.auth.components.AuthTextField
import com.rankridge.app.ui.auth.components.PrimaryButton
import com.rankridge.app.ui.auth.components.RoleDropdown
import com.rankridge.app.ui.components.RankridgeTopBar

/**
 * Sign-up: name, role and phone, then the same simulated OTP request the login flow uses.
 *
 * As on [com.rankridge.app.ui.auth.otp.OtpScreen], the brand mark is not repeated inside the card
 * — the top app bar carries the wordmark and the back action.
 */
@Composable
fun CreateAccountScreen(
    onOtpRequested: (String, UserRole) -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateAccountViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.otpSentForPhone) {
        val phone = uiState.otpSentForPhone
        if (phone != null) {
            viewModel.onOtpNavigationHandled()
            onOtpRequested(phone, uiState.role)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            RankridgeTopBar(
                title = stringResource(R.string.app_name),
                icon = Icons.Filled.School,
                navigationIcon = {
                    IconButton(onClick = onNavigateToLogin) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        CreateAccountContent(
            uiState = uiState,
            onFullNameChange = viewModel::onFullNameChange,
            onRoleChange = viewModel::onRoleChange,
            onPhoneChange = viewModel::onPhoneChange,
            onSendOtp = viewModel::onSendOtp,
            onNavigateToLogin = onNavigateToLogin,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun CreateAccountContent(
    uiState: CreateAccountUiState,
    onFullNameChange: (String) -> Unit,
    onRoleChange: (UserRole) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSendOtp: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 28.dp)
                .widthIn(max = 480.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.create_title),
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        text = stringResource(R.string.create_subtitle),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 24.dp),
                    )

                    AuthTextField(
                        value = uiState.fullName,
                        onValueChange = onFullNameChange,
                        label = "Full name",
                        leadingIcon = Icons.Filled.Person,
                        errorText = uiState.fullNameError,
                        enabled = !uiState.isSubmitting,
                        imeAction = ImeAction.Next,
                    )

                    RoleDropdown(
                        selectedRole = uiState.role,
                        onRoleSelected = onRoleChange,
                        label = "I am a",
                        enabled = !uiState.isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
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
                        prompt = "Already have an account?",
                        actionText = "Sign in",
                        onActionClick = onNavigateToLogin,
                        enabled = !uiState.isSubmitting,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        }
    }
}
