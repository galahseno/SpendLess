package id.dev.spendless.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.dev.spendless.R
import id.dev.spendless.auth.domain.AuthRepository
import id.dev.spendless.core.domain.util.DataError
import id.dev.spendless.core.domain.util.Result
import id.dev.spendless.core.presentation.ui.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _event = Channel<LoginEvent>()
    val event = _event.receiveAsFlow()

    init {
        combine(
            _state.map { it.username }.distinctUntilChanged(),
            _state.map { it.pin }.distinctUntilChanged()
        ) { username, pin ->
            _state.update { state ->
                state.copy(
                    usernameSupportText =
                    if (username.contains(" ")) {
                        UiText.StringResource(R.string.invalid_username_space)
                    } else if (!isValidUsername(username)) {
                        UiText.StringResource(R.string.invalid_username)
                    } else null,
                    pinSupportText = if (!isValidPIN(pin)) {
                        UiText.StringResource(R.string.invalid_pin)
                    } else null,
                    canLogin = !username.contains(" ") &&
                            isValidUsername(username) && isValidPIN(pin)
                )
            }
        }.launchIn(viewModelScope)
    }


    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.OnUsernameChanged -> {
                _state.update { it.copy(username = action.username) }
            }

            is LoginAction.OnPINChanged -> {
                _state.update { it.copy(pin = action.pin) }
            }

            is LoginAction.OnLoginClick -> handleLoginAccount()
            else -> Unit
        }
    }

    private fun handleLoginAccount() {
        viewModelScope.launch {
            val result = authRepository.loginAccount(
                username = _state.value.username,
                pin = _state.value.pin,
            )

            when (result) {
                is Result.Error -> {
                    if (!_state.value.isErrorVisible) {
                        _state.update {
                            it.copy(
                                isErrorVisible = true,
                                errorMessage = UiText.StringResource(
                                    when (result.error) {
                                        DataError.Local.ERROR_PROSES -> R.string.error_proses
                                        DataError.Local.USER_NOT_EXIST -> R.string.username_not_exists
                                        DataError.Local.USER_AND_PIN_INCORRECT -> R.string.username_pin_incorrect
                                        else -> R.string.username_exists
                                    }
                                ),
                                canLogin = false
                            )
                        }
                        dismissError()
                    }
                }

                is Result.Success -> {
                    _event.send(LoginEvent.OnLoginSuccess)
                }
            }
        }
    }

    private fun dismissError() {
        viewModelScope.launch {
            delay(2000)
            _state.update { it.copy(isErrorVisible = false) }
        }
    }

    private fun isValidUsername(username: String): Boolean {
        return username.length in 3..14
    }

    private fun isValidPIN(pin: String): Boolean {
        return pin.length == 5
    }
}
