package id.dev.spendless.auth.presentation.login

sealed interface LoginAction {
    data class OnUsernameChanged(val username: String): LoginAction
    data class OnPINChanged(val pin: String): LoginAction
    data object OnRegisterClick: LoginAction
}