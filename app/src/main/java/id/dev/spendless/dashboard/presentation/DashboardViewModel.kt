package id.dev.spendless.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.dev.spendless.core.domain.SettingPreferences
import id.dev.spendless.core.presentation.ui.formatTotalSpend
import id.dev.spendless.core.presentation.ui.preferences.CurrencyEnum
import id.dev.spendless.core.presentation.ui.preferences.DecimalSeparatorEnum
import id.dev.spendless.core.presentation.ui.preferences.ExpensesFormatEnum
import id.dev.spendless.core.presentation.ui.preferences.ThousandsSeparatorEnum
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

class DashboardViewModel(
    settingPreferences: SettingPreferences
) : ViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    private val _event = Channel<DashboardEvent>()
    val event = _event.receiveAsFlow()

    init {
        settingPreferences
            .getUserSession()
            .onEach { session ->
                _state.update {
                    it.copy(
                        username = session.username,
                        balance = it.balance.formatTotalSpend(
                            expensesFormat = ExpensesFormatEnum.valueOf(session.expensesFormat),
                            currency = CurrencyEnum.valueOf(session.currencySymbol),
                            decimal = DecimalSeparatorEnum.valueOf(session.decimalSeparator),
                            thousands = ThousandsSeparatorEnum.valueOf(session.thousandSeparator)
                        ),
                        previousWeekSpend = it.previousWeekSpend.formatTotalSpend(
                            expensesFormat = ExpensesFormatEnum.valueOf(session.expensesFormat),
                            currency = CurrencyEnum.valueOf(session.currencySymbol),
                            decimal = DecimalSeparatorEnum.valueOf(session.decimalSeparator),
                            thousands = ThousandsSeparatorEnum.valueOf(session.thousandSeparator)
                        )
                    )
                }
            }.launchIn(viewModelScope)
    }

    fun onAction(action: DashboardAction) {

    }
}