package id.dev.spendless.core.presentation.add_transaction

import id.dev.spendless.core.presentation.ui.UiText
import id.dev.spendless.core.presentation.ui.preferences.CurrencyEnum
import id.dev.spendless.core.presentation.ui.preferences.DecimalSeparatorEnum
import id.dev.spendless.core.presentation.ui.preferences.ExpensesFormatEnum
import id.dev.spendless.core.presentation.ui.transaction.TransactionTypeEnum

data class AddTransactionState(
    val expenseName: String = "",
    val expenseNameSupportText: UiText? = null,
    val expenseAmount: String = "",
    val expenseAmountSupportText: UiText? = null,
    val expensesNote: String = "",
    val incomeName: String = "",
    val expenseNoteSupportText: UiText? = null,
    val incomeNameSupportText: UiText? = null,
    val incomeAmount: String = "",
    val incomeAmountSupportText: UiText? = null,
    val incomeNote: String = "",
    val incomeNoteSupportText: UiText? = null,
    val expenseFormat: ExpensesFormatEnum = ExpensesFormatEnum.RoundBrackets,
    val hintFormatSeparator: DecimalSeparatorEnum = DecimalSeparatorEnum.Dot,
    val currency: CurrencyEnum = CurrencyEnum.USD,
    val selectedTransactionType: TransactionTypeEnum = TransactionTypeEnum.Expenses,
    //
)
