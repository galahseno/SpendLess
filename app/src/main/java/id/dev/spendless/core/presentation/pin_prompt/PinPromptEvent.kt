package id.dev.spendless.core.presentation.pin_prompt

sealed interface PinPromptEvent {
    data object OnSuccessValidateSession: PinPromptEvent
    data object OnSuccessLogout: PinPromptEvent
}