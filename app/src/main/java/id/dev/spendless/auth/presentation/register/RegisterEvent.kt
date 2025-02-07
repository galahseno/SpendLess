package id.dev.spendless.auth.presentation.register

sealed interface RegisterEvent {
    data object OnProcessToRepeatPin : RegisterEvent
    data object OnProcessToOnboardingPreferences : RegisterEvent
}