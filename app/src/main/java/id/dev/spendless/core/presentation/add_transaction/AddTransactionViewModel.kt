package id.dev.spendless.core.presentation.add_transaction

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.dev.spendless.R
import id.dev.spendless.core.presentation.ui.UiText
import id.dev.spendless.core.presentation.ui.preferences.DecimalSeparatorEnum
import id.dev.spendless.core.presentation.ui.transaction.TransactionTypeEnum
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

class AddTransactionViewModel(

) : ViewModel() {
    private val _state = MutableStateFlow(AddTransactionState())
    val state = _state.asStateFlow()

    private val _event = Channel<AddTransactionEvent>()
    val event = _event.receiveAsFlow()

    init {
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

            else -> {}
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

    // TODO Trim traling when save transaction
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
        return amount.length <= 13
    }

    private fun isValidTransactionNote(note: String): Boolean {
        return note.length <= 100
    }
}