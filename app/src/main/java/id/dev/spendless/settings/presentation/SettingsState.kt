package id.dev.spendless.settings.presentation

import id.dev.spendless.core.presentation.ui.UiText
import id.dev.spendless.core.presentation.ui.preferences.CurrencyEnum
import id.dev.spendless.core.presentation.ui.preferences.DecimalSeparatorEnum
import id.dev.spendless.core.presentation.ui.preferences.ExpensesFormatEnum
import id.dev.spendless.core.presentation.ui.preferences.ThousandsSeparatorEnum
import id.dev.spendless.settings.presentation.model.LockedOutEnum
import id.dev.spendless.settings.presentation.model.SessionExpiredEnum

data class SettingsState(
    val totalSpend: String = "-10382.45",
    val formattedTotalSpend: String = "",
    val selectedExpenseFormat: ExpensesFormatEnum = ExpensesFormatEnum.MinusPrefix,
    val selectedCurrency: CurrencyEnum = CurrencyEnum.IDR,
    val selectedDecimalSeparator: DecimalSeparatorEnum = DecimalSeparatorEnum.Comma,
    val selectedThousandSeparator: ThousandsSeparatorEnum = ThousandsSeparatorEnum.Dot,
    val isErrorVisible: Boolean = false,
    val errorMessage: UiText? = null,
    val canSave: Boolean = false,
    val biometricsEnabled: Boolean = false,
    val sessionExpiryDuration: SessionExpiredEnum = SessionExpiredEnum.FIVE_MINUTES,
    val lockedOutDuration: LockedOutEnum = LockedOutEnum.FIFTEEN_SECONDS,
)