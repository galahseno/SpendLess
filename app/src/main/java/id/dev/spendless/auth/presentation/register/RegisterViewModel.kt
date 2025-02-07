package id.dev.spendless.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.dev.spendless.R
import id.dev.spendless.core.presentation.ui.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(

) : ViewModel() {
    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    private val _event = Channel<RegisterEvent>()
    val event = _event.receiveAsFlow()

    init {
        _state
            .map { it.username }
            .distinctUntilChanged()
            .onEach { username ->
                _state.update {
                    it.copy(
                        usernameSupportText = if (username.contains(" ")) {
                            UiText.StringResource(R.string.invalid_username_space)
                        } else if (!isValidUsername(username)) {
                            UiText.StringResource(R.string.invalid_username)
                        } else null,
                        canRegister = !username.contains(" ") &&
                                isValidUsername(username)
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.OnUsernameChanged -> {
                _state.update { it.copy(username = action.username) }
            }
            is RegisterAction.OnInputCreatePin -> handleInputCreatePin(action.pin)

            is RegisterAction.OnDeleteCreatePin -> handleDeleteCreatePin()

            is RegisterAction.OnResetPin -> handleResetPin()

            is RegisterAction.OnInputRepeatPin -> handleInputRepeatPin(action.repeatPin)

            is RegisterAction.OnDeleteRepeatPin -> handleDeleteRepeatPin()
            else -> {}
        }
    }

    private fun handleInputCreatePin(pin: String) {
        if (_state.value.pin.length < 5) {
            _state.update { it.copy(pin = it.pin.plus(pin)) }
            if (_state.value.pin.length == 5) {
                viewModelScope.launch {
                    _event.send(RegisterEvent.OnProcessToRepeatPin)
                }
            }
        }
    }

    private fun handleDeleteCreatePin() {
        if (_state.value.pin.isNotEmpty()) {
            _state.update { it.copy(pin = it.pin.dropLast(1)) }
        }
    }

    private fun handleResetPin() {
        _state.update { it.copy(pin = "", repeatPin = "") }
    }

    private fun handleInputRepeatPin(repeatPin: String) {
        if (_state.value.repeatPin.length < 5) {
            _state.update { it.copy(repeatPin = it.repeatPin.plus(repeatPin)) }
            if (_state.value.repeatPin.length == 5) {
                validateRepeatPin()
            }
        }
    }

    private fun handleDeleteRepeatPin() {
        if (_state.value.repeatPin.isNotEmpty()) {
            _state.update { it.copy(repeatPin = it.repeatPin.dropLast(1)) }
        }
    }

    private fun validateRepeatPin() {
        if (_state.value.repeatPin == _state.value.pin) {
            viewModelScope.launch {
                _event.send(RegisterEvent.OnProcessToOnboardingPreferences)
            }
        } else {
            _state.update {
                it.copy(
                    isErrorVisible = true,
                    errorMessage = UiText.StringResource(R.string.pin_not_match),
                    repeatPin = ""
                )
            }
            viewModelScope.launch {
                delay(2000)
                _state.update {
                    it.copy(isErrorVisible = false)
                }
            }
        }
    }

    private fun isValidUsername(username: String): Boolean {
        return username.length in 3..14
    }
}