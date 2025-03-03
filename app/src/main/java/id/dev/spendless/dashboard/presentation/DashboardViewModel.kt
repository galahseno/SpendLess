package id.dev.spendless.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.dev.spendless.core.domain.SettingPreferences
import id.dev.spendless.core.domain.model.transaction.LargestTransaction
import id.dev.spendless.core.domain.model.transaction.Transaction
import id.dev.spendless.core.domain.model.transaction.TransactionGroup
import id.dev.spendless.core.presentation.ui.formatDateForHeader
import id.dev.spendless.core.presentation.ui.formatDoubleDigit
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
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class DashboardViewModel(
    dashboardRepository: DashboardRepository,
    private val settingPreferences: SettingPreferences
) : ViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    private val _event = Channel<DashboardEvent>()
    val event = _event.receiveAsFlow()

    init {
        combine(
            settingPreferences.getUserSession().distinctUntilChanged(),
            dashboardRepository.getTotalBalance().distinctUntilChanged()
        ) { session, balance ->
            _state.update { state ->
                state.copy(
                    username = session.username,
                    balance = balance.formatDoubleDigit().formatTotalSpend(
                        expensesFormat = ExpensesFormatEnum.valueOf(session.expensesFormat),
                        currency = CurrencyEnum.valueOf(session.currencySymbol),
                        decimal = DecimalSeparatorEnum.valueOf(session.decimalSeparator),
                        thousands = ThousandsSeparatorEnum.valueOf(session.thousandSeparator)
                    ),
                    negativeBalance = balance?.let { it < 0.0 } ?: true,
                )
            }
        }.launchIn(viewModelScope)

        combine(
            settingPreferences.getUserSession().distinctUntilChanged(),
            dashboardRepository.getLargestTransaction().distinctUntilChanged()
        ) { session, largestTransaction ->
            _state.update {
                it.copy(
                    largestTransaction = largestTransaction?.let {
                        LargestTransaction(
                            transactionName = largestTransaction.transactionName,
                            amount = largestTransaction.amount
                                ?.toDoubleOrNull()
                                .formatDoubleDigit()
                                .formatTotalSpend(
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

        combine(
            settingPreferences.getUserSession().distinctUntilChanged(),
            dashboardRepository.getTotalSpentPreviousWeek().distinctUntilChanged()
        ) { session, totalSpendPreviousWeek ->
            _state.update {
                it.copy(
                    previousWeekSpend = totalSpendPreviousWeek.formatDoubleDigit().formatTotalSpend(
                        expensesFormat = ExpensesFormatEnum.valueOf(session.expensesFormat),
                        currency = CurrencyEnum.valueOf(session.currencySymbol),
                        decimal = DecimalSeparatorEnum.valueOf(session.decimalSeparator),
                        thousands = ThousandsSeparatorEnum.valueOf(session.thousandSeparator)
                    ),
                )
            }
        }.launchIn(viewModelScope)

        combine(
            settingPreferences.getUserSession().distinctUntilChanged(),
            dashboardRepository.getLatestTransactions().distinctUntilChanged()
        ) { session, latestTransactions ->
            _state.update {
                it.copy(
                    latestTransactions = latestTransactions.groupBy { transaction ->
                        LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(transaction.createdAt),
                            ZoneId.systemDefault()
                        ).toLocalDate()
                    }.map { (date, transactions) ->
                        TransactionGroup(
                            formattedDate = date.formatDateForHeader(),
                            transactions = transactions.map { allTransactions ->
                                Transaction(
                                    id = allTransactions.id,
                                    transactionName = allTransactions.transactionName,
                                    categoryEmoji = allTransactions.categoryEmoji,
                                    categoryName = allTransactions.categoryName,
                                    amount = allTransactions.amount
                                        .toDouble()
                                        .formatDoubleDigit()
                                        .formatTotalSpend(
                                            expensesFormat = ExpensesFormatEnum.valueOf(session.expensesFormat),
                                            currency = CurrencyEnum.valueOf(session.currencySymbol),
                                            decimal = DecimalSeparatorEnum.valueOf(session.decimalSeparator),
                                            thousands = ThousandsSeparatorEnum.valueOf(session.thousandSeparator)
                                        ),
                                    note = allTransactions.note,
                                    createdAt = allTransactions.createdAt
                                )
                            }
                        )
                    }
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
        when (action) {
            is DashboardAction.OnItemTransactionClick -> {
                _state.update {
                    it.copy(
                        latestTransactions = it.latestTransactions.map { group ->
                            group.copy(
                                transactions = group.transactions.map { transaction ->
                                    if (transaction.id == action.id) {
                                        transaction.copy(isNoteOpen = !transaction.isNoteOpen)
                                    } else {
                                        transaction
                                    }
                                }
                            )
                        }
                    )
                }
            }

            is DashboardAction.OnFABClick -> {
                viewModelScope.launch {
                    val isSessionValid = settingPreferences.checkSessionExpired()
                    if (isSessionValid) return@launch

                    _state.update { it.copy(showBottomSheet = true) }
                }
            }

            is DashboardAction.OnShowAllClick, DashboardAction.OnSettingClick -> {
                viewModelScope.launch {
                    settingPreferences.checkSessionExpired()
                }
            }

            is DashboardAction.OnCloseBottomSheet -> {
                _state.update { it.copy(showBottomSheet = false) }
            }
        }
    }
}