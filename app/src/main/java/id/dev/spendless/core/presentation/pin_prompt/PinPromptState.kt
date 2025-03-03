package id.dev.spendless.core.presentation.pin_prompt

import id.dev.spendless.core.presentation.ui.UiText
import id.dev.spendless.settings.presentation.model.LockedOutEnum

data class PinPromptState(
    val username: String = "",
    val pin: String = "",
    val biometricsEnabled: Boolean = false,
    val isErrorVisible: Boolean = false,
    val errorMessage: UiText? = null,
    val failedAttempt: Int = 0,
    val maxFailedAttempt: Boolean = false,
    val lockedOutDuration: Long = LockedOutEnum.THIRTY_SECONDS.millis
    //
)
