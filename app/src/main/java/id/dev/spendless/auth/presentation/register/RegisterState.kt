package id.dev.spendless.auth.presentation.register

import id.dev.spendless.core.presentation.ui.UiText
import id.dev.spendless.core.presentation.ui.formatTotalSpend
import id.dev.spendless.core.presentation.ui.preferences.CurrencyEnum
import id.dev.spendless.core.presentation.ui.preferences.DecimalSeparatorEnum
import id.dev.spendless.core.presentation.ui.preferences.ExpensesFormatEnum
import id.dev.spendless.core.presentation.ui.preferences.ThousandsSeparatorEnum

data class RegisterState(
    val username: String = "",
    val canRegister: Boolean = false,
    val usernameSupportText: UiText? = null,
    val pin: String = "",
    val repeatPin: String = "",
    val isErrorVisible: Boolean = false,
    val errorMessage: UiText? = null,
    val totalSpend: String = "10382.45",
    val formattedTotalSpend: String = "10382.45".formatTotalSpend(ExpensesFormatEnum.MinusPrefix, CurrencyEnum.IDR, DecimalSeparatorEnum.Comma, ThousandsSeparatorEnum.Dot),
    val selectedExpenseFormat: ExpensesFormatEnum = ExpensesFormatEnum.MinusPrefix,
    val selectedCurrency: CurrencyEnum = CurrencyEnum.IDR,
    val selectedDecimalSeparator: DecimalSeparatorEnum = DecimalSeparatorEnum.Comma,
    val selectedThousandSeparator: ThousandsSeparatorEnum = ThousandsSeparatorEnum.Dot,
    val canProsesRegister: Boolean = false,
    //
)
