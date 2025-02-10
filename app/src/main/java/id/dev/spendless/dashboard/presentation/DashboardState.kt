package id.dev.spendless.dashboard.presentation

import id.dev.spendless.core.presentation.ui.formatTotalSpend
import id.dev.spendless.core.presentation.ui.preferences.CurrencyEnum
import id.dev.spendless.core.presentation.ui.preferences.DecimalSeparatorEnum
import id.dev.spendless.core.presentation.ui.preferences.ExpensesFormatEnum
import id.dev.spendless.core.presentation.ui.preferences.ThousandsSeparatorEnum

data class DashboardState(
    val username: String = "",
    val balance: String = "-10382.45".formatTotalSpend(
        expensesFormat = ExpensesFormatEnum.MinusPrefix,
        currency = CurrencyEnum.IDR,
        decimal = DecimalSeparatorEnum.Dot,
        thousands = ThousandsSeparatorEnum.Comma
    ),
    val previousWeekSpend: String = "0".formatTotalSpend(
        expensesFormat = ExpensesFormatEnum.MinusPrefix,
        currency = CurrencyEnum.IDR,
        decimal = DecimalSeparatorEnum.Dot,
        thousands = ThousandsSeparatorEnum.Comma
    ),
    val transaction: Boolean = true,
    //
)
