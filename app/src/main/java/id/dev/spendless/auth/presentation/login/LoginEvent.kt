package id.dev.spendless.auth.presentation.login

sealed interface LoginEvent {
    data object OnLoginSuccess: LoginEvent
}