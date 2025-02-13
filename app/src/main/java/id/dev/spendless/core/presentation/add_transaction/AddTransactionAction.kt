package id.dev.spendless.core.presentation.add_transaction

import id.dev.spendless.core.presentation.ui.transaction.TransactionCategoryEnum
import id.dev.spendless.core.presentation.ui.transaction.TransactionTypeEnum
import id.dev.spendless.core.presentation.ui.transaction.repeat_interval.RepeatIntervalEnum

sealed interface AddTransactionAction {
    data object OnCloseModalSheet : AddTransactionAction
    data class OnTransactionTypeSelected(val type: TransactionTypeEnum) : AddTransactionAction
    data class OnExpenseOrIncomeNameChanged(
        val transactionType: TransactionTypeEnum,
        val value: String
    ) : AddTransactionAction

    data class OnExpenseOrIncomeAmountChanged(
        val transactionType: TransactionTypeEnum,
        val value: String
    ) : AddTransactionAction

    data class OnExpenseOrIncomeNoteChanged(
        val transactionType: TransactionTypeEnum,
        val value: String
    ) : AddTransactionAction

    data class OnExpenseCategorySelected(val expenseCategory: TransactionCategoryEnum) :
        AddTransactionAction

    data class OnRepeatIntervalSelected(
        val transactionType: TransactionTypeEnum,
        val repeatInterval: RepeatIntervalEnum
    ) : AddTransactionAction

    data object OnAddTransaction : AddTransactionAction
}