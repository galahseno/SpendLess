package id.dev.spendless.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.dev.spendless.core.domain.SettingPreferences
import id.dev.spendless.core.domain.model.LargestTransaction
import id.dev.spendless.core.presentation.ui.formatTotalSpend
import id.dev.spendless.core.presentation.ui.preferences.CurrencyEnum
import id.dev.spendless.core.presentation.ui.preferences.DecimalSeparatorEnum
import id.dev.spendless.core.presentation.ui.preferences.ExpensesFormatEnum
import id.dev.spendless.core.presentation.ui.preferences.ThousandsSeparatorEnum
import id.dev.spendless.dashboard.domain.DashboardRepository
import id.dev.spendless.dashboard.presentation.util.formatTimestamp
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

class DashboardViewModel(
    private val dashboardRepository: DashboardRepository,
    settingPreferences: SettingPreferences
) : ViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    private val _event = Channel<DashboardEvent>()
    val event = _event.receiveAsFlow()

    init {
        combine(
            settingPreferences.getUserSession().distinctUntilChanged(),
            dashboardRepository.getTotalBalance().distinctUntilChanged(),
            dashboardRepository.getLargestTransaction().distinctUntilChanged(),
            dashboardRepository.getTotalSpentPreviousWeek().distinctUntilChanged(),
        ) { session, balance, largestTransaction, totalSpendPreviousWeek ->
            _state.update {
                it.copy(
                    username = session.username,
                    balance = balance.toString().formatTotalSpend(
                        expensesFormat = ExpensesFormatEnum.valueOf(session.expensesFormat),
                        currency = CurrencyEnum.valueOf(session.currencySymbol),
                        decimal = DecimalSeparatorEnum.valueOf(session.decimalSeparator),
                        thousands = ThousandsSeparatorEnum.valueOf(session.thousandSeparator)
                    ),
                    previousWeekSpend = totalSpendPreviousWeek.toString().formatTotalSpend(
                        expensesFormat = ExpensesFormatEnum.valueOf(session.expensesFormat),
                        currency = CurrencyEnum.valueOf(session.currencySymbol),
                        decimal = DecimalSeparatorEnum.valueOf(session.decimalSeparator),
                        thousands = ThousandsSeparatorEnum.valueOf(session.thousandSeparator)
                    ),
                    largestTransaction = largestTransaction?.let {
                        LargestTransaction(
                            transactionName = largestTransaction.transactionName,
                            amount = largestTransaction.amount
                                ?.formatTotalSpend(
                                    expensesFormat = ExpensesFormatEnum.valueOf(session.expensesFormat),
                                    currency = CurrencyEnum.valueOf(session.currencySymbol),
                                    decimal = DecimalSeparatorEnum.valueOf(session.decimalSeparator),
                                    thousands = ThousandsSeparatorEnum.valueOf(session.thousandSeparator)
                                ),
                            createdAt = largestTransaction.createdAt?.toLong()?.formatTimestamp()
                        )
                    } ?: LargestTransaction(),
                )
            }
        }.launchIn(viewModelScope)


        dashboardRepository
            .getLargestTransactionCategoryAllTime()
            .distinctUntilChanged()
            .onEach { transactionCategory ->
                if (transactionCategory != null) {
                    _state.update {
                        it.copy(
                            largestTransactionCategoryAllTime = transactionCategory
                        )
                    }
                }
            }.launchIn(viewModelScope)

    }

    fun onAction(action: DashboardAction) {

    }
}