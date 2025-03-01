package id.dev.spendless.settings.presentation.security

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.spendless.R
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.component.SpendLessButton
import id.dev.spendless.core.presentation.design_system.screenBackground
import id.dev.spendless.core.presentation.ui.ObserveAsEvents
import id.dev.spendless.settings.presentation.SettingsAction
import id.dev.spendless.settings.presentation.SettingsEvent
import id.dev.spendless.settings.presentation.SettingsState
import id.dev.spendless.settings.presentation.SettingsViewModel
import id.dev.spendless.settings.presentation.component.SettingsTopAppBar
import id.dev.spendless.settings.presentation.security.component.BiometricPromptStatus
import id.dev.spendless.settings.presentation.security.component.LockedOutDuration
import id.dev.spendless.settings.presentation.security.component.SessionExpiryDuration
import org.koin.androidx.compose.koinViewModel

@Composable
fun SecurityScreenRoot(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) { event ->
        when (event) {
            is SettingsEvent.OnSuccessSavePreferences -> onBackClick()
            else -> Unit
        }
    }

    SecurityScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is SettingsAction.OnBackClick -> onBackClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
private fun SecurityScreen(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit
) {

    Scaffold(
        topBar = {
            SettingsTopAppBar(
                titleAppBar = stringResource(R.string.security),
                modifier = Modifier,
                onBackClick = {
                    onAction(SettingsAction.OnBackClick)
                }
            )
        },
        containerColor = screenBackground,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                Text(
                    text = stringResource(R.string.biometric_for_pin_prompt),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.W500,
                        fontSize = 14.sp
                    )
                )
                BiometricPromptStatus(
                    status = state.biometricsEnabled,
                    onStatusSelected = {
                        onAction(SettingsAction.OnBiometricStatusChanged(it))
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
            Text(
                text = stringResource(R.string.session_expiry_duration),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.W500,
                    fontSize = 14.sp
                )
            )
            SessionExpiryDuration(
                selectedDuration = state.sessionExpiryDuration,
                onSelectedDuration = {
                    onAction(SettingsAction.OnSessionExpiryDurationChanged(it))
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.locked_out_duration),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.W500,
                    fontSize = 14.sp
                )
            )
            LockedOutDuration(
                selectedDuration = state.lockedOutDuration,
                onSelectedDuration = {
                    onAction(SettingsAction.OnLockedOutDurationChanged(it))
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            SpendLessButton(
                modifier = Modifier.fillMaxWidth(),
                enable = true,
                text = stringResource(R.string.save),
                onClick = {
                    onAction(SettingsAction.OnSaveSecurity)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SecurityScreenPreview() {
    SpendLessTheme {
        SecurityScreen(
            state = SettingsState(),
            onAction = {}
        )
    }
}