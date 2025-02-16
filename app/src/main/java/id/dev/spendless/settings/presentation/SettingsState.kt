package id.dev.spendless.settings.presentation

import id.dev.spendless.core.presentation.ui.preferences.CurrencyEnum

data class SettingsState(
    val spendAmount: Int = 0,
    val selectedCurrencyEnum: CurrencyEnum = CurrencyEnum.USD
    //
)
