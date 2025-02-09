package id.dev.spendless.auth.presentation.register

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.spendless.R
import id.dev.spendless.auth.presentation.register.component.RegisterTextField
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.buttonBackground
import id.dev.spendless.core.presentation.design_system.component.SpendLessButton
import id.dev.spendless.core.presentation.design_system.errorBackground
import id.dev.spendless.core.presentation.design_system.errorHeightClosedKeyboard
import id.dev.spendless.core.presentation.design_system.errorHeightOpenKeyboard
import id.dev.spendless.core.presentation.design_system.screenBackground
import id.dev.spendless.core.presentation.design_system.topPaddingAuthScreen
import id.dev.spendless.core.presentation.ui.ObserveAsEvents
import id.dev.spendless.core.presentation.ui.UiText
import id.dev.spendless.core.presentation.ui.keyboardHeightAsState

@Composable
fun RegisterScreenRoot(
    onNavigateToLogin: () -> Unit,
    onSuccessCheckUsername: () -> Unit,
    onSuccessRegister: () -> Unit,
    viewModel: RegisterViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) { event ->
        when (event) {
            is RegisterEvent.OnProcessUsernameExists -> onSuccessCheckUsername()
            is RegisterEvent.OnRegisterSuccess -> onSuccessRegister()
            else -> {}
        }
    }

    RegisterScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is RegisterAction.OnLoginClick -> onNavigateToLogin()
                else -> {}
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
private fun RegisterScreen(
    state: RegisterState,
    onAction: (RegisterAction) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
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
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.welcome_to_spendless),
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = stringResource(R.string.how_can_we_address_you),
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = stringResource(R.string.create_unique_username),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(10.dp))
            RegisterTextField(
                text = state.username,
                onTextChanged = { onAction(RegisterAction.OnUsernameChanged(it)) },
                hint = stringResource(R.string.username_hint),
                modifier = Modifier.fillMaxWidth(),
                supportingText = if (state.usernameSupportText != null) state.usernameSupportText.asString()
                else null,
            )
            SpendLessButton(
                enable = state.canRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                text = stringResource(R.string.next_register),
                icon = {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = "",
                        modifier = Modifier.size(18.dp)
                    )
                },
                onClick = {
                    keyboardController?.hide()
                    onAction(RegisterAction.OnRegisterNextClick)
                }
            )
            Spacer(modifier = Modifier.height(14.dp))
            TextButton(
                onClick = {
                    onAction(RegisterAction.OnLoginClick)
                }
            ) {
                Text(
                    color = buttonBackground,
                    text = stringResource(R.string.already_have_acc),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W600),
                )
            }
        }

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
private fun RegisterScreenPreview() {
    SpendLessTheme {
        RegisterScreen(
            state = RegisterState(
                isErrorVisible = false,
                errorMessage = UiText.DynamicString("Something Wrong")
            ),
            onAction = {}
        )
    }
}