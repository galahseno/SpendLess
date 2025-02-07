package id.dev.spendless.auth.presentation.register.create_pin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.spendless.R
import id.dev.spendless.auth.presentation.register.RegisterAction
import id.dev.spendless.auth.presentation.register.RegisterEvent
import id.dev.spendless.auth.presentation.register.RegisterState
import id.dev.spendless.auth.presentation.register.RegisterViewModel
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.component.KeyPad
import id.dev.spendless.core.presentation.design_system.component.PinDotView
import id.dev.spendless.core.presentation.design_system.errorBackground
import id.dev.spendless.core.presentation.design_system.errorHeightClosedKeyboard
import id.dev.spendless.core.presentation.design_system.errorHeightOpenKeyboard
import id.dev.spendless.core.presentation.design_system.screenBackground
import id.dev.spendless.core.presentation.design_system.topPaddingAuthScreen
import id.dev.spendless.core.presentation.ui.ObserveAsEvents
import id.dev.spendless.core.presentation.ui.keyboardHeightAsState

@Composable
fun CreatePinScreenRoot(
    onBackClick: () -> Unit,
    onProcessToRepeatPin: () -> Unit,
    viewModel: RegisterViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) { event ->
        when (event) {
            is RegisterEvent.OnProcessToRepeatPin -> onProcessToRepeatPin()
            else -> {}
        }
    }

    CreatePinScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is RegisterAction.OnBackClick -> onBackClick()
                else -> {}
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreatePinScreen(
    state: RegisterState,
    onAction: (RegisterAction) -> Unit
) {
    val keyboardHeight by keyboardHeightAsState()
    val keyboardOpen by remember(keyboardHeight) {
        mutableStateOf(keyboardHeight > 0.dp)
    }
    val errorHeightDp by animateDpAsState(
        if (keyboardOpen) errorHeightOpenKeyboard else errorHeightClosedKeyboard,
        label = "anim_error_height"
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(screenBackground)
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
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.create_pin),
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = stringResource(R.string.use_pin_to_login),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(20.dp))
            PinDotView(
                pin = state.pin,
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
                    onAction(RegisterAction.OnDeleteCreatePin)
                },
                onNumberPadClick = {
                    onAction(RegisterAction.OnInputCreatePin(it))
                }
            )
        }

        TopAppBar(
            modifier = Modifier
                .padding(10.dp),
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

        AnimatedVisibility(
            visible = state.isErrorVisible,
            label = "anime_error_container",
            modifier = Modifier
                .padding(bottom = keyboardHeight)
                .align(Alignment.BottomCenter),
            enter = slideIn(initialOffset = { IntOffset(0, it.height / 2) }) + fadeIn(),
            exit = slideOut(targetOffset = { IntOffset(0, it.height / 2) }) + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(errorHeightDp)
                    .background(errorBackground),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.errorMessage?.asString() ?: "",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W500),
                    modifier = Modifier
                        .then(
                            if (keyboardOpen) Modifier
                            else Modifier.navigationBarsPadding()
                        )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreatePinScreenPreview() {
    SpendLessTheme {
        CreatePinScreen(
            state = RegisterState(
                pin = "12"
            ),
            onAction = {}
        )
    }
}