package id.dev.spendless.core.presentation.pin_prompt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.dev.spendless.R
import id.dev.spendless.core.domain.CoreRepository
import id.dev.spendless.core.domain.SettingPreferences
import id.dev.spendless.core.domain.model.PinPromptAttempt
import id.dev.spendless.core.domain.util.Result
import id.dev.spendless.core.presentation.ui.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PinPromptViewModel(
    private val settingPreferences: SettingPreferences,
    private val coreRepository: CoreRepository
) : ViewModel() {
    private val _state = MutableStateFlow(PinPromptState())
    val state = _state.asStateFlow()

    private val _event = Channel<PinPromptEvent>()
    val event = _event.receiveAsFlow()

    init {
        settingPreferences
            .getUserSession()
            .map { it.username }
            .distinctUntilChanged()
            .onEach { username ->
                if (username.isNotEmpty()) {
                    _state.update { it.copy(username = username) }
                }
            }
            .launchIn(viewModelScope)

        combine(
            settingPreferences.getPinPromptAttempt().distinctUntilChanged(),
            settingPreferences.getUserSecurity().distinctUntilChanged()
        ) { pinPromptAttempt, userSecurity ->
            if (pinPromptAttempt.failedAttempt == 3)
                settingPreferences.updateMaxAttemptPinPrompt(true)

            _state.update {
                it.copy(
                    maxFailedAttempt = pinPromptAttempt.maxFailedAttempt,
                    biometricsEnabled = userSecurity.biometricPromptEnable,
                    lockedOutDuration = if (pinPromptAttempt.maxFailedAttempt) pinPromptAttempt.lockedOutDuration
                    else userSecurity.lockedOutDuration,
                )
            }

            if (pinPromptAttempt.maxFailedAttempt) {
                if (_state.value.lockedOutDuration > 0) {
                    delay(1000)
                    settingPreferences.updateLatestDuration( _state.value.lockedOutDuration - 1000)
                } else {
                    _state.update {
                        it.copy(
                            lockedOutDuration = userSecurity.lockedOutDuration
                        )
                    }
                    settingPreferences.resetPinPromptAttempt(
                        PinPromptAttempt(
                            failedAttempt = 0,
                            maxFailedAttempt = false,
                            lockedOutDuration =  userSecurity.lockedOutDuration
                        )
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun onAction(action: PinPromptAction) {
        when (action) {
            is PinPromptAction.OnInputPin -> handleInputPin(action.pin)
            is PinPromptAction.OnDeletePin -> handleDeletePin()
            is PinPromptAction.OnLogoutClick -> handleLogout()
            is PinPromptAction.OnSuccessValidateSession -> handleSuccessValidateSession()
            else -> Unit
        }
    }

    private fun handleSuccessValidateSession() {
        viewModelScope.launch {
            settingPreferences.updateLatestTimeStamp()
            settingPreferences.changeSession(false)
            _event.send(PinPromptEvent.OnSuccessValidateSession)
        }
    }

    private fun handleInputPin(pin: String) {
        if (_state.value.pin.length < 5) {
            if (!_state.value.isErrorVisible) {
                _state.update { it.copy(pin = it.pin.plus(pin)) }
                if (_state.value.pin.length == 5) {
                    viewModelScope.launch {
                        val userId = settingPreferences.getUserId().first()

                        when (val result =
                            coreRepository.checkSessionPin(userId, _state.value.pin)) {
                            is Result.Success -> {
                                if (result.data) {
                                    _event.send(PinPromptEvent.OnSuccessValidateSession)
                                } else {
                                    _state.update {
                                        it.copy(
                                            isErrorVisible = true,
                                            errorMessage = UiText.StringResource(R.string.wrong_pin),
                                            pin = "",
                                        )
                                    }
                                    settingPreferences.updateFailedAttempt(
                                        settingPreferences.getPinPromptAttempt().first().failedAttempt.plus(1)
                                    )
                                    dismissError()
                                }
                            }

                            is Result.Error -> {
                                showError(UiText.StringResource(R.string.error_proses))
                                _state.update { it.copy(pin = "") }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleDeletePin() {
        if (_state.value.pin.isNotEmpty()) {
            _state.update { it.copy(pin = it.pin.dropLast(1)) }
        }
    }

    private fun handleLogout() {
        viewModelScope.launch {
            when (coreRepository.logout()) {
                /**
                 * Do nothing since getUserId already observe in main activity
                 **/
                is Result.Success -> {}

                is Result.Error -> {
                    showError(UiText.StringResource(R.string.error_proses))
                }
            }
        }
    }

    private fun showError(message: UiText) {
        if (_state.value.isErrorVisible) return
        _state.update {
            it.copy(
                isErrorVisible = true,
                errorMessage = message,
            )
        }
        dismissError()
    }

    private fun dismissError() {
        viewModelScope.launch {
            delay(2000)
            _state.update { it.copy(isErrorVisible = false) }
        }
    }
}