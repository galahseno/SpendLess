package id.dev.spendless.core.domain.model

data class UserSecurity(
    val biometricPromptEnable: Boolean,
    val sessionExpiryDuration: Int,
    val lockedOutDuration: Int,
)
