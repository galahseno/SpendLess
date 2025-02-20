package id.dev.spendless.settings.presentation

sealed interface SettingsEvent {
    data object OnSuccessSavePreferences : SettingsEvent
}