package id.dev.spendless.settings.presentation

import id.dev.spendless.core.presentation.ui.preferences.CurrencyEnum
import id.dev.spendless.core.presentation.ui.preferences.DecimalSeparatorEnum
import id.dev.spendless.core.presentation.ui.preferences.ExpensesFormatEnum
import id.dev.spendless.core.presentation.ui.preferences.ThousandsSeparatorEnum
import id.dev.spendless.settings.presentation.model.LockedOutEnum
import id.dev.spendless.settings.presentation.model.SessionExpiredEnum

sealed interface SettingsAction {
    data object OnBackClick : SettingsAction
    data object OnPreferencesClick : SettingsAction
    data object OnSecurityClick : SettingsAction
    data object OnLogoutClick : SettingsAction
    data class OnExpensesFormatSelected(val expense: ExpensesFormatEnum) : SettingsAction
    data class OnCurrencySelected(val currency: CurrencyEnum) : SettingsAction
    data class OnDecimalSeparatorSelected(val separator: DecimalSeparatorEnum) : SettingsAction
    data class OnThousandSeparatorSelected(val separator: ThousandsSeparatorEnum) : SettingsAction
    data object OnSavePreferences : SettingsAction

    data class OnBiometricStatusChanged(val status: Boolean) : SettingsAction
    data class OnSessionExpiryDurationChanged(val duration: SessionExpiredEnum) : SettingsAction
    data class OnLockedOutDurationChanged(val duration: LockedOutEnum) : SettingsAction
    data object OnSaveSecurity : SettingsAction
}