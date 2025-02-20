package id.dev.spendless.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.dev.spendless.core.domain.SettingPreferences
import id.dev.spendless.core.domain.model.Transaction
import id.dev.spendless.core.domain.model.LargestTransaction
import id.dev.spendless.core.domain.model.TransactionGroup
import id.dev.spendless.core.presentation.ui.formatDateForHeader
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
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale

class DashboardViewModel(
    dashboardRepository: DashboardRepository,
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
            dashboardRepository.getAllTransactions().distinctUntilChanged()
        ) { session, balance, largestTransaction, totalSpendPreviousWeek, allTransactions ->
            _state.update {
                it.copy(
                    username = session.username,
                    balance = String.format(Locale.getDefault(), "%.2f", balance).formatTotalSpend(
                        expensesFormat = ExpensesFormatEnum.valueOf(session.expensesFormat),
                        currency = CurrencyEnum.valueOf(session.currencySymbol),
                        decimal = DecimalSeparatorEnum.valueOf(session.decimalSeparator),
                        thousands = ThousandsSeparatorEnum.valueOf(session.thousandSeparator)
                    ),
                    negativeBalance = balance?.let { it < 0.0 } ?: true,
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
                    allTransactions = allTransactions.groupBy { transaction ->
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
                                    amount = allTransactions.amount.formatTotalSpend(
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
                        allTransactions = it.allTransactions.map { group ->
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

            else -> Unit
        }
    }
}