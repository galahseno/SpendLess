package id.dev.spendless.core.domain

import kotlinx.coroutines.flow.Flow

interface SettingPreferences {
    fun getUserSession(): Flow<Int>

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
}