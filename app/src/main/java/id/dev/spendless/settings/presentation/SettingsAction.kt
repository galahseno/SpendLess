package id.dev.spendless.settings.presentation

import id.dev.spendless.core.presentation.ui.preferences.CurrencyEnum

sealed interface SettingsAction {
    data class OnSelectionOfCurrency(val currency: CurrencyEnum) : SettingsAction
    data class OnSelectionOfExpensesFormat(val formatIndex:Int) :SettingsAction
}