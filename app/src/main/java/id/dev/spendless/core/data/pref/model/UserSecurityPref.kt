package id.dev.spendless.core.data.pref.model

import id.dev.spendless.core.domain.model.UserSecurity
import kotlinx.serialization.Serializable

@Serializable
data class UserSecurityPref(
    val biometricPromptEnable: Boolean = false,
    val sessionExpiryDuration: Long = 300000,
    val lockedOutDuration: Long = 30000,
)

fun UserSecurityPref.toUserSecurity() : UserSecurity {
    return UserSecurity(
        biometricPromptEnable = biometricPromptEnable,
        sessionExpiryDuration = sessionExpiryDuration,
        lockedOutDuration = lockedOutDuration
    )
}