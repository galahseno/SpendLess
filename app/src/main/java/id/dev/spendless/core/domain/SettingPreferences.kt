package id.dev.spendless.core.domain

import id.dev.spendless.core.domain.model.UserSecurity
import id.dev.spendless.core.domain.model.UserSession
import kotlinx.coroutines.flow.Flow

interface SettingPreferences {
    fun getUserId(): Flow<Int>
    fun getUserSession(): Flow<UserSession>
    fun getUserSecurity(): Flow<UserSecurity>

    suspend fun saveRegisterSession(
        userId: Int,
        username: String,
        expensesFormat: String,
        currencySymbol: String,
        decimalSeparator: String,
        thousandSeparator: String
    )

    suspend fun saveLoginSession(
        userId: Int,
        username: String,
    )

    suspend fun updateUserSession(
        expensesFormat: String,
        currencySymbol: String,
        decimalSeparator: String,
        thousandSeparator: String
    )

    suspend fun updateUserSecurity(
        biometricPromptEnable: Boolean,
        sessionExpiryDuration: Int,
        lockedOutDuration: Int
    )
}