package id.dev.spendless.core.presentation.pin_prompt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.dev.spendless.R
import id.dev.spendless.core.domain.CoreRepository
import id.dev.spendless.core.domain.SettingPreferences
import id.dev.spendless.core.domain.util.Result
import id.dev.spendless.core.presentation.ui.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
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
            .onEach {
                _state.value = _state.value.copy(username = it)
            }
            .launchIn(viewModelScope)

        settingPreferences
            .getUserSecurity()
            .onEach {
                _state.value = _state.value.copy(
                    biometricsEnabled = it.biometricPromptEnable
                )
            }
            .launchIn(viewModelScope)


        _state
            .map { it.failedAttempt }
            .distinctUntilChanged()
            .onEach { failedAttempt ->
                if (failedAttempt == 3) {
                    _state.update {
                        it.copy(
                            maxFailedAttempt = true,
                            failedAttempt = 0
                        )
                    }
                }
            }
            .launchIn(viewModelScope)

        _state
            .map { it.maxFailedAttempt }
            .distinctUntilChanged()
            .onEach { maxAttempt ->
                if (maxAttempt) {
                    while (_state.value.tryAgainDuration > 0) {
                        delay(1000)
                        _state.update {
                            it.copy(tryAgainDuration = it.tryAgainDuration - 1000)
                        }
                    }
                    _state.update {
                        it.copy(
                            maxFailedAttempt = false,
                            tryAgainDuration = 30000
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
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
                                    settingPreferences.updateLatestTimeStamp()
                                } else {
                                    _state.update {
                                        it.copy(
                                            isErrorVisible = true,
                                            errorMessage = UiText.StringResource(R.string.wrong_pin),
                                            pin = "",
                                            failedAttempt = it.failedAttempt.plus(1)
                                        )
                                    }
                                    dismissError()
                                }
                            }

                            is Result.Error -> {
                                _state.update {
                                    it.copy(
                                        isErrorVisible = true,
                                        errorMessage = UiText.StringResource(R.string.error_proses),
                                        pin = ""
                                    )
                                }
                                dismissError()
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
        // TODO save user preferences to db to persist latest preferences
        viewModelScope.launch {
            settingPreferences.logout()
            joinAll()
            _event.send(PinPromptEvent.OnSuccessLogout)
        }
    }

    private fun dismissError() {
        viewModelScope.launch {
            delay(2000)
            _state.update { it.copy(isErrorVisible = false) }
        }
    }
}