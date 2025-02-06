package id.dev.spendless.auth.presentation.login

import id.dev.spendless.core.presentation.ui.UiText

data class LoginState(
    val username: String = "",
    val pin: String = "",
    val canLogin: Boolean = false,
    val usernameSupportText: UiText? = null,
    val pinSupportText: UiText? = null,
    //
)
