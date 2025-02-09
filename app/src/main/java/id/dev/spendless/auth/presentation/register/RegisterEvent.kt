package id.dev.spendless.auth.presentation.register

sealed interface RegisterEvent {
    data object OnProcessUsernameExists : RegisterEvent
    data object OnProcessToRepeatPin : RegisterEvent
    data object OnProcessToOnboardingPreferences : RegisterEvent
    data object OnRegisterSuccess : RegisterEvent
}