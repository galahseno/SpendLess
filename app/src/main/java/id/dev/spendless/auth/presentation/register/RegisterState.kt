package id.dev.spendless.auth.presentation.register

import id.dev.spendless.core.presentation.ui.UiText

data class RegisterState(
    val username: String = "",
    val canRegister: Boolean = false,
    val usernameSupportText: UiText? = null,
    val isErrorVisible: Boolean = false,
    val errorMessage: UiText? = null
    //
)
