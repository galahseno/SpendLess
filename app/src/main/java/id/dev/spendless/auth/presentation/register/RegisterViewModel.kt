package id.dev.spendless.auth.presentation.register

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.dev.spendless.R
import id.dev.spendless.auth.domain.AuthRepository
import id.dev.spendless.core.domain.util.DataError
import id.dev.spendless.core.domain.util.Result
import id.dev.spendless.core.presentation.ui.UiText
import id.dev.spendless.core.presentation.ui.formatTotalSpend
import id.dev.spendless.core.presentation.ui.preferences.CurrencyEnum
import id.dev.spendless.core.presentation.ui.preferences.DecimalSeparatorEnum
import id.dev.spendless.core.presentation.ui.preferences.ExpensesFormatEnum
import id.dev.spendless.core.presentation.ui.preferences.ThousandsSeparatorEnum
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
    private val authRepository: AuthRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(
        RegisterState(
            pin = savedStateHandle.get<String>(PIN_KEY) ?: "",
            repeatPin = savedStateHandle.get<String>(REPEAT_PIN_KEY) ?: ""
        )
    )
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
                        canRegister = !username.contains(" ") && username.isNotEmpty()
                    )
                }
            }
            .launchIn(viewModelScope)

        _state
            .map { it.selectedDecimalSeparator to it.selectedThousandSeparator }
            .distinctUntilChanged()
            .onEach { (decimal, thousand) ->
                _state.update { it.copy(canProsesRegister = decimal.name != thousand.name) }

            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.OnUsernameChanged -> handleUsernameChanged(action.username)
            is RegisterAction.OnRegisterNextClick -> handleCheckUsername()
            is RegisterAction.OnInputCreatePin -> handleInputCreatePin(action.pin)
            is RegisterAction.OnDeleteCreatePin -> handleDeleteCreatePin()
            is RegisterAction.OnResetPin -> handleResetPin()
            is RegisterAction.OnInputRepeatPin -> handleInputRepeatPin(action.repeatPin)
            is RegisterAction.OnDeleteRepeatPin -> handleDeleteRepeatPin()
            is RegisterAction.OnExpensesFormatSelected -> handleSelectedExpensesFormat(action.expense)
            is RegisterAction.OnCurrencySelected -> handleSelectedCurrency(action.currency)
            is RegisterAction.OnDecimalSeparatorSelected -> handleSelectedDecimalSeparator(action.separator)
            is RegisterAction.OnThousandSeparatorSelected -> handleSelectedThousandSeparator(action.separator)
            is RegisterAction.OnRegisterAccount -> handleRegisterAccount()
            else -> Unit
        }
    }

    private fun handleUsernameChanged(username: String) {
        _state.update { it.copy(username = username) }
    }

    private fun handleCheckUsername() {
        if (!isValidUsername(_state.value.username)) {
            showError(UiText.StringResource(R.string.invalid_username))
            return
        }

        viewModelScope.launch {
            when (val result = authRepository.checkUsernameExists(_state.value.username)) {
                is Result.Success -> {
                    _event.send(RegisterEvent.OnProcessUsernameExists)
                }

                is Result.Error -> {
                    val errorMessage = when (result.error) {
                        DataError.Local.ERROR_PROSES -> R.string.error_proses
                        DataError.Local.USER_EXIST -> R.string.username_exists
                        else -> R.string.username_exists
                    }
                    showError(UiText.StringResource(errorMessage))
                    _state.update { it.copy(canRegister = false) }
                }
            }
        }
    }

    private fun handleInputCreatePin(pin: String) {
        if (_state.value.pin.length < 5) {
            _state.update { it.copy(pin = it.pin.plus(pin)) }
            if (_state.value.pin.length == 5) {
                savedStateHandle[PIN_KEY] = _state.value.pin
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
        savedStateHandle.apply {
            set(PIN_KEY, "")
            set(REPEAT_PIN_KEY, "")
        }
        _state.update { it.copy(pin = "", repeatPin = "") }
    }

    private fun handleInputRepeatPin(repeatPin: String) {
        if (_state.value.repeatPin.length < 5) {
            if (!_state.value.isErrorVisible) {
                _state.update { it.copy(repeatPin = it.repeatPin.plus(repeatPin)) }
                if (_state.value.repeatPin.length == 5) {
                    validateRepeatPin()
                }
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
            savedStateHandle[REPEAT_PIN_KEY] = _state.value.repeatPin
            viewModelScope.launch {
                _event.send(RegisterEvent.OnProcessToOnboardingPreferences)
            }
        } else {
            showError(UiText.StringResource(R.string.pin_not_match))
            _state.update { it.copy(repeatPin = "") }
        }
    }

    private fun handleSelectedExpensesFormat(expense: ExpensesFormatEnum) {
        _state.update {
            it.copy(
                selectedExpenseFormat = expense,
                formattedTotalSpend = it.totalSpend.formatTotalSpend(
                    expense,
                    it.selectedCurrency,
                    it.selectedDecimalSeparator,
                    it.selectedThousandSeparator
                )
            )
        }
    }

    private fun handleSelectedCurrency(currency: CurrencyEnum) {
        _state.update {
            it.copy(
                selectedCurrency = currency,
                formattedTotalSpend = it.totalSpend.formatTotalSpend(
                    it.selectedExpenseFormat,
                    currency,
                    it.selectedDecimalSeparator,
                    it.selectedThousandSeparator
                )
            )
        }
    }

    private fun handleSelectedDecimalSeparator(separator: DecimalSeparatorEnum) {
        val isSameSeparator = separator.name == _state.value.selectedThousandSeparator.name

        if (isSameSeparator) {
            showError(UiText.StringResource(R.string.invalid_separator))
        }

        _state.update {
            it.copy(
                selectedDecimalSeparator = separator,
                formattedTotalSpend = it.totalSpend.formatTotalSpend(
                    it.selectedExpenseFormat,
                    it.selectedCurrency,
                    separator,
                    it.selectedThousandSeparator
                )
            )
        }
    }

    private fun handleSelectedThousandSeparator(separator: ThousandsSeparatorEnum) {
        val isSameSeparator = separator.name == _state.value.selectedDecimalSeparator.name

        if (isSameSeparator) {
            showError(UiText.StringResource(R.string.invalid_separator))
        }

        _state.update {
            it.copy(
                selectedThousandSeparator = separator,
                formattedTotalSpend = it.totalSpend.formatTotalSpend(
                    it.selectedExpenseFormat,
                    it.selectedCurrency,
                    it.selectedDecimalSeparator,
                    separator,
                )
            )
        }
    }

    private fun handleRegisterAccount() {
        viewModelScope.launch {
            val result = authRepository.registerAccount(
                username = _state.value.username,
                pin = _state.value.pin,
                expensesFormat = _state.value.selectedExpenseFormat.name,
                currencySymbol = _state.value.selectedCurrency.name,
                decimalSeparator = _state.value.selectedDecimalSeparator.name,
                thousandSeparator = _state.value.selectedThousandSeparator.name
            )

            when (result) {
                is Result.Error -> {
                    showError(UiText.StringResource(R.string.error_proses))
                }

                is Result.Success -> {
                    _event.send(RegisterEvent.OnRegisterSuccess)
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

    private fun isValidUsername(username: String): Boolean {
        return username.length in 3..14
    }

    companion object {
        private const val PIN_KEY = "PIN_KEY"
        private const val REPEAT_PIN_KEY = "REPEAT_PIN_KEY"
    }
}