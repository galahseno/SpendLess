package id.dev.spendless.auth.presentation.register

sealed interface RegisterAction {
    data class OnUsernameChanged(val username: String): RegisterAction
    data object OnDismissErrorMessage: RegisterAction
    data object OnLoginClick: RegisterAction
}