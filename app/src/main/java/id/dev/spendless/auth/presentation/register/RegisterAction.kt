package id.dev.spendless.auth.presentation.register

sealed interface RegisterAction {
    data class OnUsernameChanged(val username: String) : RegisterAction
    data object OnRegisterNextClick : RegisterAction
    data object OnLoginClick : RegisterAction

    data object OnBackClick : RegisterAction
    data class OnInputCreatePin(val pin: String) : RegisterAction
    data object OnDeleteCreatePin : RegisterAction

    data object OnResetPin : RegisterAction
    data object OnDeleteRepeatPin : RegisterAction
    data class OnInputRepeatPin(val repeatPin: String) : RegisterAction
}