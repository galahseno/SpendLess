package id.dev.spendless.core.data.pref.model

import id.dev.spendless.core.domain.model.PinPromptAttempt
import kotlinx.serialization.Serializable

@Serializable
data class PinPromptAttemptPref(
    val failedAttempt: Int = 0,
    val maxFailedAttempt: Boolean = false,
    val lockedOutDuration: Long = 30000,
)

fun PinPromptAttemptPref.toPinPromptAttempt() : PinPromptAttempt {
    return PinPromptAttempt(
        failedAttempt = failedAttempt,
        maxFailedAttempt = maxFailedAttempt,
        lockedOutDuration = lockedOutDuration
    )
}