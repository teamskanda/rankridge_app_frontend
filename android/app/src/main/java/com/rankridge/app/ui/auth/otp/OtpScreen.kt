package com.rankridge.app.ui.auth.otp

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
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rankridge.app.R
import com.rankridge.app.ui.auth.components.OtpInput
import com.rankridge.app.ui.auth.components.PrimaryButton
import com.rankridge.app.ui.components.RankridgeTopBar

/**
 * OTP verification. UI-only: any 6 digits are accepted.
 *
 * The brand mark is not repeated inside the card — the top app bar already carries the wordmark,
 * and this screen needs that bar anyway for the back action.
 */
@Composable
fun OtpScreen(
    phone: String,
    onVerified: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OtpViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.verified) {
        if (uiState.verified) {
            viewModel.onVerifiedHandled()
            onVerified()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            RankridgeTopBar(
                title = stringResource(R.string.app_name),
                icon = Icons.Filled.School,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        OtpContent(
            uiState = uiState,
            phone = phone,
            onOtpChange = viewModel::onOtpChange,
            onVerify = viewModel::onVerify,
            onResend = viewModel::onResend,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun OtpContent(
    uiState: OtpUiState,
    phone: String,
    onOtpChange: (String) -> Unit,
    onVerify: () -> Unit,
    onResend: () -> Unit,
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
                        text = stringResource(R.string.otp_title),
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        text = stringResource(R.string.otp_subtitle, "+91 $phone"),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 24.dp),
                    )

                    OtpInput(
                        value = uiState.otp,
                        onValueChange = onOtpChange,
                        isError = uiState.otpError != null,
                        enabled = !uiState.isSubmitting,
                        onDone = onVerify,
                    )

                    Text(
                        text = uiState.otpError ?: stringResource(R.string.otp_demo_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (uiState.otpError != null) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                    )

                    PrimaryButton(
                        text = "Verify",
                        onClick = onVerify,
                        loading = uiState.isSubmitting,
                        modifier = Modifier.padding(top = 16.dp),
                    )

                    if (uiState.secondsUntilResend > 0) {
                        Text(
                            text = "Resend code in ${uiState.secondsUntilResend}s",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 14.dp),
                        )
                    } else {
                        TextButton(
                            onClick = onResend,
                            enabled = !uiState.isSubmitting,
                            modifier = Modifier.padding(top = 6.dp),
                        ) {
                            Text("Resend code")
                        }
                    }
                }
            }
        }
    }
}
