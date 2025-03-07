package id.dev.spendless.core.presentation.pin_prompt

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.spendless.R
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.component.SpendLessErrorContainer
import id.dev.spendless.core.presentation.design_system.component.pin.KeyPad
import id.dev.spendless.core.presentation.design_system.component.pin.PinDotView
import id.dev.spendless.core.presentation.design_system.errorBackground
import id.dev.spendless.core.presentation.design_system.errorHeightClosedKeyboard
import id.dev.spendless.core.presentation.design_system.errorHeightOpenKeyboard
import id.dev.spendless.core.presentation.design_system.screenBackground
import id.dev.spendless.core.presentation.design_system.topPaddingAuthScreen
import id.dev.spendless.core.presentation.ui.ObserveAsEvents
import id.dev.spendless.core.presentation.ui.formatTryAgainPinDuration
import id.dev.spendless.core.presentation.ui.keyboardOpenAsState
import id.dev.spendless.main.util.BiometricPromptManager
import org.koin.androidx.compose.koinViewModel

@Composable
fun PinPromptScreenRoot(
    onSuccessValidateSession: () -> Unit,
    promptManager: BiometricPromptManager,
    viewModel: PinPromptViewModel = koinViewModel()
) {
    val view = LocalView.current
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val enrollLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            //
        }
    )

    ObserveAsEvents(promptManager.promptResults) { promptResult ->
        when (promptResult) {
            is BiometricPromptManager.BiometricResult.AuthenticationSuccess -> {
                viewModel.onAction(PinPromptAction.OnSuccessValidateSession)
            }

            BiometricPromptManager.BiometricResult.AuthenticationFailed -> {
                promptManager.cancelBiometricPrompt()
                viewModel.onAction(PinPromptAction.OnFailedBiometricValidation)
            }

            BiometricPromptManager.BiometricResult.AuthenticationNotSet -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                        putExtra(
                            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                        )
                    }
                    enrollLauncher.launch(enrollIntent)
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    @Suppress("DEPRECATION")
                    enrollLauncher.launch(Intent(Settings.ACTION_FINGERPRINT_ENROLL))
                } else {
                    enrollLauncher.launch(Intent(Settings.ACTION_SECURITY_SETTINGS))
                }
            }

            else -> Unit
        }
    }

    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
    }

    ObserveAsEvents(viewModel.event) { event ->
        when (event) {
            is PinPromptEvent.OnSuccessValidateSession -> onSuccessValidateSession()
        }
    }

    PinPromptScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is PinPromptAction.OnBiometricClick -> {
                    promptManager.showBiometricPrompt(
                        title = context.getString(R.string.app_name),
                        description = context.getString(R.string.biometric_description)
                    )
                }

                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PinPromptScreen(
    state: PinPromptState,
    onAction: (PinPromptAction) -> Unit
) {
    val keyboardOpen by keyboardOpenAsState()
    val errorHeightDp by animateDpAsState(
        if (keyboardOpen) errorHeightOpenKeyboard else errorHeightClosedKeyboard,
        label = "anim_error_height"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = topPaddingAuthScreen)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.app_icon),
                contentDescription = "app_icon"
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (state.maxFailedAttempt) stringResource(R.string.too_many_failed_attempts)
                else stringResource(R.string.hello_pin_prompt, state.username),
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = if (state.maxFailedAttempt) stringResource(
                    R.string.try_your_pin_again_in,
                    state.lockedOutDuration.formatTryAgainPinDuration()
                )
                else stringResource(R.string.enter_pin_again),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(20.dp))
            PinDotView(
                pin = state.pin,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .alpha(if (state.maxFailedAttempt) 0.3f else 1f),
            )
            Spacer(modifier = Modifier.height(20.dp))
            KeyPad(
                modifier = Modifier
                    .fillMaxWidth(),
                isBiometricEnabled = !state.maxFailedAttempt && state.biometricsEnabled,
                isBiometricVisible = true,
                keyPadEnable = !state.maxFailedAttempt,
                onBiometricClick = {
                    onAction(PinPromptAction.OnBiometricClick)
                },
                onDeletePadClick = {
                    onAction(PinPromptAction.OnDeletePin)
                },
                onNumberPadClick = {
                    onAction(PinPromptAction.OnInputPin(it))
                }
            )
        }

        TopAppBar(
            modifier = Modifier
                .padding(end = 10.dp, top = 35.dp),
            title = {},
            actions = {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(errorBackground.copy(alpha = 0.08f))
                        .clickable {
                            onAction(PinPromptAction.OnLogoutClick)
                        }
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.logout_icon),
                        contentDescription = "Settings_icon",
                        tint = errorBackground,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        SpendLessErrorContainer(
            isErrorVisible = state.isErrorVisible,
            errorHeightDp = errorHeightDp,
            errorMessage = state.errorMessage?.asString() ?: "",
            keyboardOpen = keyboardOpen,
            modifier = Modifier
                .imePadding()
                .align(Alignment.BottomCenter)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PinPromptScreenPreview() {
    SpendLessTheme {
        PinPromptScreen(
            state = PinPromptState(
                username = "rockefeller74",
                biometricsEnabled = false,
                maxFailedAttempt = true
            ),
            onAction = {}
        )
    }
}