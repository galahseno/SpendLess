package id.dev.spendless.transaction.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.dev.spendless.core.domain.SettingPreferences
import id.dev.spendless.core.domain.model.transaction.Transaction
import id.dev.spendless.core.domain.model.transaction.TransactionGroup
import id.dev.spendless.core.presentation.ui.formatDateForHeader
import id.dev.spendless.core.presentation.ui.formatDoubleDigit
import id.dev.spendless.core.presentation.ui.formatTotalSpend
import id.dev.spendless.core.presentation.ui.preferences.CurrencyEnum
import id.dev.spendless.core.presentation.ui.preferences.DecimalSeparatorEnum
import id.dev.spendless.core.presentation.ui.preferences.ExpensesFormatEnum
import id.dev.spendless.core.presentation.ui.preferences.ThousandsSeparatorEnum
import id.dev.spendless.transaction.domain.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class AllTransactionViewModel(
    transactionRepository: TransactionRepository,
    private val settingPreferences: SettingPreferences
) : ViewModel() {
    private val _state = MutableStateFlow(AllTransactionState())
    val state = _state.asStateFlow()

    init {
        combine(
            settingPreferences.getUserSession().distinctUntilChanged(),
            transactionRepository.getAllTransactions().distinctUntilChanged()
        ) { session, allTransactions ->
            _state.update {
                it.copy(
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

        settingPreferences
            .getAddBottomSheetValue()
            .distinctUntilChanged()
            .onEach { bottomSheetValue ->
                _state.update { it.copy(showBottomSheet = bottomSheetValue) }
            }.launchIn(viewModelScope)

        settingPreferences
            .getExportBottomSheetValue()
            .distinctUntilChanged()
            .onEach { bottomSheetValue ->
                _state.update { it.copy(showExportBottomSheet = bottomSheetValue) }
            }.launchIn(viewModelScope)
    }

    fun onAction(action: AllTransactionAction) {
        when (action) {
            is AllTransactionAction.OnItemTransactionClick -> handleTransactionClick(action.id)

            is AllTransactionAction.OnFABClick -> {
                viewModelScope.launch {
                    val isSessionValid = settingPreferences.checkSessionExpired()
                    if (isSessionValid) return@launch

                    settingPreferences.changeAddBottomSheetValue(true)
                }
            }

            is AllTransactionAction.OnCloseBottomSheet -> {
                viewModelScope.launch {
                    settingPreferences.changeAddBottomSheetValue(false)
                    settingPreferences.changeExportBottomSheetValue(false)
                }
            }
            is AllTransactionAction.OnExportClick -> {
                viewModelScope.launch {
                    val isSessionValid = settingPreferences.checkSessionExpired()
                    if (isSessionValid) return@launch

                    settingPreferences.changeExportBottomSheetValue(true)
                }
            }

            else -> Unit
        }
    }

    private fun handleTransactionClick(id: Int) {
        _state.update {
            it.copy(
                allTransactions = it.allTransactions.map { group ->
                    group.copy(
                        transactions = group.transactions.map { transaction ->
                            if (transaction.id == id) {
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
}