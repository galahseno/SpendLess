package id.dev.spendless.core.presentation.pin_prompt

sealed interface PinPromptAction {
    data object OnDeletePin : PinPromptAction
    data object OnBiometricClick : PinPromptAction
    data object OnFailedBiometricValidation : PinPromptAction
    data object OnSuccessValidateSession : PinPromptAction
    data class OnInputPin(val pin: String) : PinPromptAction
    data object OnLogoutClick : PinPromptAction
}