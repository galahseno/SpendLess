package id.dev.spendless.auth.presentation.login

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.spendless.R
import id.dev.spendless.auth.presentation.login.component.LoginTextField
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
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreenRoot(
    onNavigateToRegister: () -> Unit,
    onSuccessLogin: () -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) { event ->
        when(event) {
            is LoginEvent.OnLoginSuccess -> onSuccessLogin()
        }
    }

    LoginScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is LoginAction.OnRegisterClick -> onNavigateToRegister()
                else -> {}
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
private fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit
) {
    val usernameFocus = remember { FocusRequester() }
    val pinFocus = remember { FocusRequester() }
    var firstOpenScreen by rememberSaveable { mutableStateOf(true) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val keyboardHeight by keyboardHeightAsState()
    val keyboardOpen by remember(keyboardHeight) {
        mutableStateOf(keyboardHeight > 0.dp)
    }
    val errorHeightDp by animateDpAsState(
        if (keyboardOpen) errorHeightOpenKeyboard else errorHeightClosedKeyboard,
        label = "anim_error_height"
    )

    LaunchedEffect(firstOpenScreen) {
        if (firstOpenScreen) {
            usernameFocus.requestFocus()
            firstOpenScreen = false
        }
    }

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
                text = stringResource(R.string.welcome_back),
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = stringResource(R.string.enter_login_detail),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(14.dp))
            LoginTextField(
                text = state.username,
                onTextChanged = {
                    onAction(LoginAction.OnUsernameChanged(it))
                },
                hint = stringResource(R.string.username_hint),
                supportingText = if (state.usernameSupportText != null) state.usernameSupportText.asString()
                else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(usernameFocus),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        pinFocus.requestFocus()
                    }
                )
            )
            LoginTextField(
                text = state.pin,
                onTextChanged = {
                    if (it.isDigitsOnly()) {
                        onAction(LoginAction.OnPINChanged(it))
                    }
                },
                hint = stringResource(R.string.pin_hint),
                supportingText = if (state.pinSupportText != null) state.pinSupportText.asString()
                else null,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(pinFocus),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(
                    onGo = {
                        keyboardController?.hide()
                        onAction(LoginAction.OnLoginClick)
                    }
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            SpendLessButton(
                enable = state.canLogin,
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.log_in),
                onClick = {
                    onAction(LoginAction.OnLoginClick)
                }
            )
            Spacer(modifier = Modifier.height(14.dp))
            TextButton(
                onClick = {
                    onAction(LoginAction.OnRegisterClick)
                }
            ) {
                Text(
                    color = buttonBackground,
                    text = stringResource(R.string.new_to_spendless),
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
private fun LoginScreenPreview() {
    SpendLessTheme {
        LoginScreen(
            state = LoginState(
                errorMessage = UiText.DynamicString("Something Wrong")
            ),
            onAction = {})
    }
}