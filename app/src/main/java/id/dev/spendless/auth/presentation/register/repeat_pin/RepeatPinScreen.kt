package id.dev.spendless.auth.presentation.register.repeat_pin

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.spendless.R
import id.dev.spendless.auth.presentation.register.RegisterAction
import id.dev.spendless.auth.presentation.register.RegisterEvent
import id.dev.spendless.auth.presentation.register.RegisterState
import id.dev.spendless.auth.presentation.register.RegisterViewModel
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.component.SpendLessErrorContainer
import id.dev.spendless.core.presentation.design_system.component.pin.KeyPad
import id.dev.spendless.core.presentation.design_system.component.pin.PinDotView
import id.dev.spendless.core.presentation.design_system.errorHeightClosedKeyboard
import id.dev.spendless.core.presentation.design_system.errorHeightOpenKeyboard
import id.dev.spendless.core.presentation.design_system.screenBackground
import id.dev.spendless.core.presentation.design_system.topPaddingAuthScreen
import id.dev.spendless.core.presentation.ui.ObserveAsEvents
import id.dev.spendless.core.presentation.ui.keyboardOpenAsState

@Composable
fun RepeatPinScreenRoot(
    onBackClick: () -> Unit,
    onProcessToOnboardingPreferences: () -> Unit,
    onSuccessRegister: () -> Unit,
    viewModel: RegisterViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) { event ->
        when (event) {
            is RegisterEvent.OnProcessToOnboardingPreferences -> onProcessToOnboardingPreferences()
            is RegisterEvent.OnRegisterSuccess -> onSuccessRegister()
            else -> Unit
        }
    }

    BackHandler {
        viewModel.onAction(RegisterAction.OnResetPin)
        onBackClick()
    }

    RepeatPinScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is RegisterAction.OnBackClick -> {
                    viewModel.onAction(RegisterAction.OnResetPin)
                    onBackClick()
                }

                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RepeatPinScreen(
    state: RegisterState,
    onAction: (RegisterAction) -> Unit
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
                text = stringResource(R.string.repeat_pin),
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = stringResource(R.string.enter_pin_again),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(20.dp))
            PinDotView(
                pin = state.repeatPin,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
            )
            Spacer(modifier = Modifier.height(20.dp))
            KeyPad(
                modifier = Modifier
                    .fillMaxWidth(),
                isBiometricEnabled = false,
                onBiometricClick = {},
                onDeletePadClick = {
                    onAction(RegisterAction.OnDeleteRepeatPin)
                },
                onNumberPadClick = {
                    onAction(RegisterAction.OnInputRepeatPin(it))
                }
            )
        }

        TopAppBar(
            modifier = Modifier
                .padding(start = 10.dp, top = 35.dp),
            title = {},
            navigationIcon = {
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "back_icon",
                    modifier = Modifier.clickable {
                        onAction(RegisterAction.OnBackClick)
                    }
                )
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

@Preview
@Composable
private fun RepeatPinScreenPreview() {
    SpendLessTheme {
        RepeatPinScreen(
            state = RegisterState(),
            onAction = {}
        )
    }
}