package id.dev.spendless.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.dev.spendless.R
import id.dev.spendless.core.presentation.ui.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

class LoginViewModel(

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
            Pair(username, pin)
        }.onEach { (username, pin) ->
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
            is LoginAction.OnLoginClick -> {
                // TODO Handle Login
            }
            else -> {}
        }
    }

    private fun isValidUsername(username: String): Boolean {
        return username.length in 3..14
    }

    private fun isValidPIN(pin: String): Boolean {
        return pin.length == 5
    }
}
