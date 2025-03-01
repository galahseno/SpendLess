package id.dev.spendless.settings.presentation

sealed interface SettingsEvent {
    data object OnSuccessLogout : SettingsEvent
    data object OnSuccessSavePreferences : SettingsEvent
}