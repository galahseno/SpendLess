package id.dev.spendless.auth.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.spendless.R
import id.dev.spendless.auth.presentation.login.component.LoginTextField
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.buttonBackground
import id.dev.spendless.core.presentation.design_system.component.SpendLessButton
import id.dev.spendless.core.presentation.design_system.screenBackground
import id.dev.spendless.core.presentation.ui.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreenRoot(
    onNavigateToRegister: () -> Unit, viewModel: LoginViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) {

    }

    LoginScreen(state = state, onAction = { action ->
        when (action) {
            is LoginAction.OnRegisterClick -> {
                onNavigateToRegister()
            }

            else -> {}
        }
        viewModel.onAction(action)
    })
}

@Composable
private fun LoginScreen(
    state: LoginState, onAction: (LoginAction) -> Unit
) {
    val usernameFocus = remember { FocusRequester() }
    val pinFocus = remember { FocusRequester() }
    var firstOpenScreen by rememberSaveable { mutableStateOf(true) }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(firstOpenScreen) {
        if (firstOpenScreen) {
            usernameFocus.requestFocus()
            firstOpenScreen = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBackground)
            .verticalScroll(rememberScrollState())
            .padding(top = 88.dp)
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
        LoginTextField(text = state.username,
            onTextChanged = {
                onAction(LoginAction.OnUsernameChanged(it))
            },
            hint = stringResource(R.string.username_hint),
            supportingText = if (state.usernameSupportText != null) state.usernameSupportText.asString()
            else null,
            visualTransformation = VisualTransformation.None,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(usernameFocus),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                pinFocus.requestFocus()
            })
        )
        LoginTextField(text = state.pin,
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
            keyboardActions = KeyboardActions(onGo = {
                keyboardController?.hide()
                //TODO trigger LOGIN
            })
        )
        Spacer(modifier = Modifier.height(4.dp))
        SpendLessButton(enable = state.canLogin,
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.log_in),
            onClick = {
                //TODO trigger LOGIN
            })
        Spacer(modifier = Modifier.height(14.dp))
        TextButton(onClick = {
            onAction(LoginAction.OnRegisterClick)
        }) {
            Text(
                color = buttonBackground,
                text = stringResource(R.string.new_to_spendless),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.W600
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    SpendLessTheme {
        LoginScreen(state = LoginState(), onAction = {})
    }
}