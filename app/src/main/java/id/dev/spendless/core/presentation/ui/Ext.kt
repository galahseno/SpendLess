package id.dev.spendless.core.presentation.ui


import id.dev.spendless.core.presentation.ui.preferences.CurrencyEnum
import id.dev.spendless.core.presentation.ui.preferences.DecimalSeparatorEnum
import id.dev.spendless.core.presentation.ui.preferences.ExpensesFormatEnum
import id.dev.spendless.core.presentation.ui.preferences.ThousandsSeparatorEnum
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit

fun String.formatTotalSpend(
    expensesFormat: ExpensesFormatEnum,
    currency: CurrencyEnum,
    decimal: DecimalSeparatorEnum,
    thousands: ThousandsSeparatorEnum
): String {
    val number = this.toDoubleOrNull() ?: 0.0
    val isNegative = number < 0
    val absoluteNumber = kotlin.math.abs(number)
    val decimalPart = this.substringAfter(".", "")
    val integerPart = absoluteNumber.toInt()

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
            when {
                expensesFormat == ExpensesFormatEnum.MinusPrefix && isNegative -> "-"
                expensesFormat == ExpensesFormatEnum.RoundBrackets -> "("
                else -> ""
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

fun LocalDate.formatDateForHeader(): String {
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)

    return when (this) {
        today -> "Today"
        yesterday -> "Yesterday"
        else -> {
            val formatter = DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())
            this.format(formatter)
        }
    }
}

fun Long.formatTryAgainPinDuration(): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) % 60

    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}