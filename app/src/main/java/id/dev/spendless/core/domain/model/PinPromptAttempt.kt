package id.dev.spendless.core.domain.model

data class PinPromptAttempt(
    val failedAttempt: Int,
    val maxFailedAttempt: Boolean,
    val lockedOutDuration: Long,
)
