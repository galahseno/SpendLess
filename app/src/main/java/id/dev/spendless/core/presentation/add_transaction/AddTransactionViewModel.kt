package id.dev.spendless.core.presentation.add_transaction

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.dev.spendless.R
import id.dev.spendless.core.domain.CoreRepository
import id.dev.spendless.core.domain.SettingPreferences
import id.dev.spendless.core.domain.model.AddTransactionModel
import id.dev.spendless.core.domain.util.Result
import id.dev.spendless.core.presentation.ui.UiText
import id.dev.spendless.core.presentation.ui.preferences.CurrencyEnum
import id.dev.spendless.core.presentation.ui.preferences.DecimalSeparatorEnum
import id.dev.spendless.core.presentation.ui.preferences.ExpensesFormatEnum
import id.dev.spendless.core.presentation.ui.transaction.TransactionCategoryEnum
import id.dev.spendless.core.presentation.ui.transaction.TransactionTypeEnum
import id.dev.spendless.core.presentation.ui.transaction.repeat_interval.RepeatIntervalEnum
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

class AddTransactionViewModel(
    private val coreRepository: CoreRepository,
    settingPreferences: SettingPreferences
) : ViewModel() {
    private val _state = MutableStateFlow(AddTransactionState())
    val state = _state.asStateFlow()

    private val _event = Channel<AddTransactionEvent>()
    val event = _event.receiveAsFlow()

    init {
        settingPreferences
            .getUserSession()
            .onEach { session ->
                _state.update {
                    it.copy(
                        expenseFormat = ExpensesFormatEnum.valueOf(session.expensesFormat),
                        currency = CurrencyEnum.valueOf(session.currencySymbol),
                        hintFormatSeparator = DecimalSeparatorEnum.valueOf(session.decimalSeparator),
                    )
                }
            }
            .launchIn(viewModelScope)

        _state
            .map { it.expenseName to it.incomeName }
            .distinctUntilChanged()
            .onEach { (expensesName, incomeName) ->
                _state.update {
                    it.copy(
                        expenseNameSupportText = if (!isValidTransactionName(expensesName)) {
                            UiText.StringResource(R.string.invalid_transaction)
                        } else null,
                        incomeNameSupportText = if (!isValidTransactionName(incomeName)) {
                            UiText.StringResource(R.string.invalid_transaction)
                        } else null,
                    )
                }
            }
            .launchIn(viewModelScope)

        _state
            .map { it.expenseAmount to it.incomeAmount }
            .distinctUntilChanged()
            .onEach { (expenseAmount, incomeAmount) ->
                _state.update {
                    it.copy(
                        expenseAmountSupportText = if (!isValidTransactionAmount(expenseAmount)) {
                            UiText.StringResource(R.string.invalid_transaction_amount)
                        } else null,
                        incomeAmountSupportText = if (!isValidTransactionAmount(incomeAmount)) {
                            UiText.StringResource(R.string.invalid_transaction_amount)
                        } else null,
                    )
                }
            }
            .launchIn(viewModelScope)

        _state
            .map { it.expensesNote to it.incomeNote }
            .distinctUntilChanged()
            .onEach { (expenseNote, incomeNote) ->
                _state.update {
                    it.copy(
                        expenseNoteSupportText = if (!isValidTransactionNote(expenseNote)) {
                            UiText.StringResource(R.string.invalid_transaction_note)
                        } else null,
                        incomeNoteSupportText = if (!isValidTransactionNote(incomeNote)) {
                            UiText.StringResource(R.string.invalid_transaction_note)
                        } else null,
                    )
                }
            }
            .launchIn(viewModelScope)

        _state
            .onEach {
                when (it.selectedTransactionType) {
                    TransactionTypeEnum.Expenses -> {
                        _state.update { state ->
                            state.copy(
                                canAddTransaction = isValidTransactionName(it.expenseName) &&
                                        isValidTransactionAmount(it.expenseAmount) &&
                                        isValidTransactionNote(it.expensesNote) &&
                                        !it.expenseAmount.matches(Regex("[.,]"))
                            )
                        }
                    }

                    TransactionTypeEnum.Income -> {
                        _state.update { state ->
                            state.copy(
                                canAddTransaction = isValidTransactionName(it.incomeName) &&
                                        isValidTransactionAmount(it.incomeAmount) &&
                                        isValidTransactionNote(it.incomeNote) &&
                                        !it.incomeAmount.matches(Regex("[.,]"))
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun onAction(action: AddTransactionAction) {
        when (action) {
            is AddTransactionAction.OnTransactionTypeSelected ->
                handleTransactionTypeSelected(action.type)

            is AddTransactionAction.OnExpenseOrIncomeNameChanged ->
                handleExpenseOrIncomeNameChanged(action.transactionType, action.value)

            is AddTransactionAction.OnExpenseOrIncomeAmountChanged ->
                handleExpenseOrIncomeAmountChanged(action.transactionType, action.value)

            is AddTransactionAction.OnExpenseOrIncomeNoteChanged ->
                handleExpenseOrIncomeNoteChanged(action.transactionType, action.value)

            is AddTransactionAction.OnExpenseCategorySelected ->
                handleExpenseCategorySelected(action.expenseCategory)

            is AddTransactionAction.OnRepeatIntervalSelected ->
                handleRepeatIntervalSelected(action.transactionType, action.repeatInterval)

            is AddTransactionAction.OnAddTransaction -> handleAddTransaction()

            else -> Unit
        }
    }

    private fun handleTransactionTypeSelected(transactionType: TransactionTypeEnum) {
        _state.update { it.copy(selectedTransactionType = transactionType) }
    }

    private fun handleExpenseOrIncomeNameChanged(
        transactionType: TransactionTypeEnum,
        value: String
    ) {
        val trimmedValue = trimTrailingExtraSpaces(value)
        when (transactionType) {
            TransactionTypeEnum.Expenses -> _state.update { it.copy(expenseName = trimmedValue) }
            TransactionTypeEnum.Income -> _state.update { it.copy(incomeName = trimmedValue) }
        }
    }

    // TODO Handle thousand separator
    private fun handleExpenseOrIncomeAmountChanged(
        transactionType: TransactionTypeEnum,
        value: String
    ) {
        val allowedSeparator = when (_state.value.hintFormatSeparator) {
            DecimalSeparatorEnum.Dot -> "."
            DecimalSeparatorEnum.Comma -> ","
        }
        val separatorCount = value.count { it.toString() == allowedSeparator }
        val isSeparatorValid = separatorCount <= 1

        val parts = value.split(allowedSeparator)
        val isDecimalPartValid = parts.size <= 2 && (parts.size == 1 || parts[1].length <= 2)

        if (isSeparatorValid && isDecimalPartValid &&
            (value.isDigitsOnly() || value.contains(allowedSeparator))
        ) {
            val filteredText = if (separatorCount > 0 && !value.isDigitsOnly()) {
                value.filter { it.isDigit() || it.toString() == allowedSeparator }
            } else {
                value
            }
            val limitedDecimalText = if (parts.size == 2 && parts[1].length > 2) {
                parts[0] + allowedSeparator + parts[1].substring(0, 2)
            } else {
                filteredText
            }
            when (transactionType) {
                TransactionTypeEnum.Expenses -> _state.update { it.copy(expenseAmount = limitedDecimalText) }
                TransactionTypeEnum.Income -> _state.update { it.copy(incomeAmount = limitedDecimalText) }
            }
        }
    }

    private fun handleExpenseOrIncomeNoteChanged(
        transactionType: TransactionTypeEnum,
        value: String
    ) {
        when (transactionType) {
            TransactionTypeEnum.Expenses -> _state.update { it.copy(expensesNote = value) }
            TransactionTypeEnum.Income -> _state.update { it.copy(incomeNote = value) }
        }
    }

    private fun handleExpenseCategorySelected(
        expenseCategory: TransactionCategoryEnum
    ) {
        _state.update { it.copy(selectedExpenseCategory = expenseCategory) }
    }

    private fun handleRepeatIntervalSelected(
        transactionType: TransactionTypeEnum,
        repeatInterval: RepeatIntervalEnum
    ) {
        when (transactionType) {
            TransactionTypeEnum.Expenses -> _state.update { it.copy(selectedExpenseRepeatInterval = repeatInterval) }
            TransactionTypeEnum.Income -> _state.update { it.copy(selectedIncomeRepeatInterval = repeatInterval) }
        }
    }

    // TODO Trim traling when save transaction
    private fun handleAddTransaction() {
        viewModelScope.launch {
            val transactionData = when (_state.value.selectedTransactionType) {
                TransactionTypeEnum.Expenses -> AddTransactionModel(
                    transactionName = _state.value.expenseName,
                    categoryEmoji = _state.value.selectedExpenseCategory.categoryEmoji,
                    categoryName = _state.value.selectedExpenseCategory.categoryName,
                    amount = _state.value.expenseAmount
                        .replace(",", ".").toDouble().unaryMinus(),
                    note = _state.value.expensesNote,
                    createdAt = System.currentTimeMillis(),
                    repeat = _state.value.selectedExpenseRepeatInterval.repeatName
                )

                TransactionTypeEnum.Income -> AddTransactionModel(
                    transactionName = _state.value.incomeName,
                    categoryEmoji = TransactionCategoryEnum.Income.categoryEmoji,
                    categoryName = TransactionCategoryEnum.Income.categoryName,
                    amount = _state.value.incomeAmount.replace(",", ".").toDouble(),
                    note = _state.value.incomeNote,
                    createdAt = System.currentTimeMillis(),
                    repeat = _state.value.selectedIncomeRepeatInterval.repeatName
                )
            }
            val result = coreRepository.createTransaction(transactionData)

            when (result) {
                is Result.Error -> {
                    if (!_state.value.isErrorVisible) {
                        _state.update {
                            it.copy(
                                isErrorVisible = true,
                                errorMessage = UiText.StringResource(R.string.error_proses),
                            )
                        }
                        dismissError()
                    }
                }

                is Result.Success -> {
                    _event.send(AddTransactionEvent.OnAddTransactionSuccess)
                    _state.update {
                        it.copy(
                            expenseName = "",
                            expenseAmount = "",
                            expensesNote = "",
                            incomeName = "",
                            incomeAmount = "",
                            incomeNote = "",
                            selectedExpenseCategory = TransactionCategoryEnum.Other,
                            selectedExpenseRepeatInterval = RepeatIntervalEnum.DoesNotRepeat,
                            selectedIncomeRepeatInterval = RepeatIntervalEnum.DoesNotRepeat
                        )
                    }
                }
            }
        }
    }

    private fun trimTrailingExtraSpaces(input: String): String {
        if (input.isBlank()) {
            return ""
        }

        val trimmedStart = input.trimStart()
        val trimmedEnd = trimmedStart.trimEnd()

        val hasTrailingSpace = input.endsWith(" ") && trimmedEnd.length < trimmedStart.length

        return buildString {
            append(trimmedEnd)
            if (hasTrailingSpace) {
                append(" ")
            }
        }
    }

    private fun isValidTransactionName(username: String): Boolean {
        return username.length in 3..14
    }

    private fun isValidTransactionAmount(amount: String): Boolean {
        return amount.length in 1..13
    }

    private fun isValidTransactionNote(note: String): Boolean {
        return note.length <= 100
    }

    private fun dismissError() {
        viewModelScope.launch {
            delay(2000)
            _state.update { it.copy(isErrorVisible = false) }
        }
    }
}