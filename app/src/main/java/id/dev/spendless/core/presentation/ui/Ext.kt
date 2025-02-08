package id.dev.spendless.core.presentation.ui


import id.dev.spendless.core.presentation.ui.preferences.CurrencyEnum
import id.dev.spendless.core.presentation.ui.preferences.DecimalSeparatorEnum
import id.dev.spendless.core.presentation.ui.preferences.ExpensesFormatEnum
import id.dev.spendless.core.presentation.ui.preferences.ThousandsSeparatorEnum
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

fun String.formatTotalSpend(
    expensesFormat: ExpensesFormatEnum,
    currency: CurrencyEnum,
    decimal: DecimalSeparatorEnum,
    thousands: ThousandsSeparatorEnum
): String {
    val number = this.toDoubleOrNull() ?: 0.0
    val decimalPart = this.substringAfter(".", "")
    val integerPart = number.toInt()

    val decimalFormatSymbols = DecimalFormatSymbols(Locale.ROOT)
    decimalFormatSymbols.groupingSeparator = when (thousands) {
        ThousandsSeparatorEnum.Dot -> '.'
        ThousandsSeparatorEnum.Comma -> ','
        ThousandsSeparatorEnum.Space -> ' '
    }
    decimalFormatSymbols.decimalSeparator = when (decimal) {
        DecimalSeparatorEnum.Dot -> '.'
        DecimalSeparatorEnum.Comma -> ','
    }

    val decimalFormat = DecimalFormat("#,##0", decimalFormatSymbols)
    decimalFormat.isGroupingUsed = true
    val formattedIntegerPart = decimalFormat.format(integerPart)

    return buildString {
        append(
            when (expensesFormat) {
                ExpensesFormatEnum.MinusPrefix -> "-"
                ExpensesFormatEnum.RoundBrackets -> "("
            }
        )
        append(currency.symbol)
        append(formattedIntegerPart)
        if (decimalPart.isNotEmpty()) {
            append(decimalFormatSymbols.decimalSeparator)
            append(decimalPart)
        }
        append(
            when (expensesFormat) {
                ExpensesFormatEnum.MinusPrefix -> ""
                ExpensesFormatEnum.RoundBrackets -> ")"
            }
        )
    }
}