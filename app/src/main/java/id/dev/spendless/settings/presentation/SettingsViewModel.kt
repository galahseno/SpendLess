package id.dev.spendless.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.dev.spendless.R
import id.dev.spendless.core.domain.SettingPreferences
import id.dev.spendless.core.presentation.ui.UiText
import id.dev.spendless.core.presentation.ui.formatTotalSpend
import id.dev.spendless.core.presentation.ui.preferences.CurrencyEnum
import id.dev.spendless.core.presentation.ui.preferences.DecimalSeparatorEnum
import id.dev.spendless.core.presentation.ui.preferences.ExpensesFormatEnum
import id.dev.spendless.core.presentation.ui.preferences.ThousandsSeparatorEnum
import id.dev.spendless.settings.presentation.model.LockedOutEnum
import id.dev.spendless.settings.presentation.model.SessionExpiredEnum
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingPreferences: SettingPreferences
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    private val _event = Channel<SettingsEvent>()
    val event = _event.receiveAsFlow()

    init {
        settingPreferences
            .getUserSession()
            .distinctUntilChanged()
            .onEach { session ->
                _state.update {
                    it.copy(
                        formattedTotalSpend = it.totalSpend.formatTotalSpend(
                            expensesFormat = ExpensesFormatEnum.valueOf(session.expensesFormat),
                            currency = CurrencyEnum.valueOf(session.currencySymbol),
                            decimal = DecimalSeparatorEnum.valueOf(session.decimalSeparator),
                            thousands = ThousandsSeparatorEnum.valueOf(session.thousandSeparator)
                        ),
                        selectedCurrency = CurrencyEnum.valueOf(session.currencySymbol),
                        selectedExpenseFormat = ExpensesFormatEnum.valueOf(session.expensesFormat),
                        selectedDecimalSeparator = DecimalSeparatorEnum.valueOf(session.decimalSeparator),
                        selectedThousandSeparator = ThousandsSeparatorEnum.valueOf(session.thousandSeparator)
                    )
                }
            }
            .launchIn(viewModelScope)

        settingPreferences
            .getUserSecurity()
            .distinctUntilChanged()
            .onEach { userSecurity ->
                _state.update {
                    it.copy(
                        biometricsEnabled = userSecurity.biometricPromptEnable,
                        sessionExpiryDuration = SessionExpiredEnum.fromMinutes(userSecurity.sessionExpiryDuration),
                        lockedOutDuration = LockedOutEnum.fromSeconds(userSecurity.lockedOutDuration)
                    )
                }
            }.launchIn(viewModelScope)

        _state
            .map { it.selectedDecimalSeparator to it.selectedThousandSeparator }
            .distinctUntilChanged()
            .onEach { (decimal, thousand) ->
                _state.update { it.copy(canSave = decimal.name != thousand.name) }

            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.OnExpensesFormatSelected -> handleSelectedExpensesFormat(action.expense)
            is SettingsAction.OnCurrencySelected -> handleSelectedCurrency(action.currency)
            is SettingsAction.OnDecimalSeparatorSelected -> handleSelectedDecimalSeparator(action.separator)
            is SettingsAction.OnThousandSeparatorSelected -> handleSelectedThousandSeparator(action.separator)
            is SettingsAction.OnLogoutClick -> {

            }

            is SettingsAction.OnSavePreferences -> handleSavePreferences()

            is SettingsAction.OnBiometricStatusChanged -> handleBiometricStatusChanged(action.status)
            is SettingsAction.OnSessionExpiryDurationChanged -> handleSessionExpiryDurationChanged(
                action.duration
            )

            is SettingsAction.OnLockedOutDurationChanged -> handleLockedOutDurationChanged(action.duration)
            is SettingsAction.OnSaveSecurity -> handleSaveSecurity()
            else -> Unit
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

        if (isSameSeparator && !_state.value.isErrorVisible) {
            _state.update {
                it.copy(
                    isErrorVisible = true,
                    errorMessage = UiText.StringResource(R.string.invalid_separator)
                )
            }
            dismissError()
        }

        _state.update {
            it.copy(
                selectedDecimalSeparator = separator,
                formattedTotalSpend = it.totalSpend.takeIf { !isSameSeparator }?.formatTotalSpend(
                    it.selectedExpenseFormat,
                    it.selectedCurrency,
                    separator,
                    it.selectedThousandSeparator
                ) ?: it.formattedTotalSpend
            )
        }
    }

    private fun handleSelectedThousandSeparator(separator: ThousandsSeparatorEnum) {
        val isSameSeparator = separator.name == _state.value.selectedDecimalSeparator.name

        if (isSameSeparator && !_state.value.isErrorVisible) {
            _state.update {
                it.copy(
                    isErrorVisible = true,
                    errorMessage = UiText.StringResource(R.string.invalid_separator)
                )
            }
            dismissError()
        }

        _state.update {
            it.copy(
                selectedThousandSeparator = separator,
                formattedTotalSpend = it.totalSpend.takeIf { !isSameSeparator }?.formatTotalSpend(
                    it.selectedExpenseFormat,
                    it.selectedCurrency,
                    it.selectedDecimalSeparator,
                    separator,
                ) ?: it.formattedTotalSpend
            )
        }
    }

    private fun handleSavePreferences() {
        viewModelScope.launch {
            try {
                settingPreferences.updateUserSession(
                    expensesFormat = _state.value.selectedExpenseFormat.name,
                    currencySymbol = _state.value.selectedCurrency.name,
                    decimalSeparator = _state.value.selectedDecimalSeparator.name,
                    thousandSeparator = _state.value.selectedThousandSeparator.name
                )

                joinAll()

                _event.send(SettingsEvent.OnSuccessSavePreferences)
            } catch (_: Exception) {
                coroutineContext.ensureActive()
                _state.update {
                    it.copy(
                        isErrorVisible = true,
                        errorMessage = UiText.StringResource(R.string.error_proses),
                    )
                }
                dismissError()
            }
        }
    }

    private fun handleBiometricStatusChanged(status: Boolean) {
        _state.update { it.copy(biometricsEnabled = status) }
    }

    private fun handleSessionExpiryDurationChanged(duration: SessionExpiredEnum) {
        _state.update { it.copy(sessionExpiryDuration = duration) }
    }

    private fun handleLockedOutDurationChanged(duration: LockedOutEnum) {
        _state.update { it.copy(lockedOutDuration = duration) }
    }

    private fun handleSaveSecurity() {
        viewModelScope.launch {
            try {
                settingPreferences.updateUserSecurity(
                    biometricPromptEnable = _state.value.biometricsEnabled,
                    sessionExpiryDuration = _state.value.sessionExpiryDuration.minutes,
                    lockedOutDuration = _state.value.lockedOutDuration.seconds
                )

                joinAll()

                _event.send(SettingsEvent.OnSuccessSavePreferences)
            } catch (_: Exception) {
                coroutineContext.ensureActive()
                _state.update {
                    it.copy(
                        isErrorVisible = true,
                        errorMessage = UiText.StringResource(R.string.error_proses),
                    )
                }
                dismissError()
            }
        }
    }

    private fun dismissError() {
        viewModelScope.launch {
            delay(2000)
            _state.update { it.copy(isErrorVisible = false) }
        }
    }
}