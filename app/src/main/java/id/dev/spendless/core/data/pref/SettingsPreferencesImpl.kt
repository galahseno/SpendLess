package id.dev.spendless.core.data.pref

import android.os.SystemClock
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import id.dev.spendless.core.domain.SettingPreferences
import id.dev.spendless.core.domain.model.UserSecurity
import id.dev.spendless.core.domain.model.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SettingPreferencesImpl(
    private val dataStore: DataStore<Preferences>
) : SettingPreferences {

    override fun getUserId(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[USER_ID_KEY] ?: -1
        }
    }

    override fun getUserSession(): Flow<UserSession> {
        return dataStore.data.map {
            UserSession(
                username = it[USER_NAME_KEY] ?: "",
                expensesFormat = it[EXPENSES_FORMAT_KEY] ?: "MinusPrefix",
                currencySymbol = it[CURRENCY_KEY] ?: "USD",
                decimalSeparator = it[DECIMAL_SEPARATOR_KEY] ?: "Dot",
                thousandSeparator = it[THOUSAND_SEPARATOR_KEY] ?: "Comma",
            )
        }
    }

    override fun getUserSecurity(): Flow<UserSecurity> {
        return dataStore.data.map {
            UserSecurity(
                biometricPromptEnable = it[BIOMETRICS_KEY] ?: false,
                sessionExpiryDuration = it[SESSION_EXPIRED_DURATION_KEY] ?: 300000,
                lockedOutDuration = it[LOCKED_DURATION_KEY] ?: 15000
            )
        }
    }

    override suspend fun saveRegisterSession(
        userId: Int,
        username: String,
        expensesFormat: String,
        currencySymbol: String,
        decimalSeparator: String,
        thousandSeparator: String
    ) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USER_NAME_KEY] = username
            preferences[EXPENSES_FORMAT_KEY] = expensesFormat
            preferences[CURRENCY_KEY] = currencySymbol
            preferences[DECIMAL_SEPARATOR_KEY] = decimalSeparator
            preferences[THOUSAND_SEPARATOR_KEY] = thousandSeparator
        }
    }

    override suspend fun saveLoginSession(
        userId: Int,
        username: String,
    ) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USER_NAME_KEY] = username
        }
    }

    override suspend fun updateUserSession(
        expensesFormat: String,
        currencySymbol: String,
        decimalSeparator: String,
        thousandSeparator: String
    ) {
        dataStore.edit { preferences ->
            preferences[EXPENSES_FORMAT_KEY] = expensesFormat
            preferences[CURRENCY_KEY] = currencySymbol
            preferences[DECIMAL_SEPARATOR_KEY] = decimalSeparator
            preferences[THOUSAND_SEPARATOR_KEY] = thousandSeparator
        }
    }

    override suspend fun updateUserSecurity(
        biometricPromptEnable: Boolean,
        sessionExpiryDuration: Long,
        lockedOutDuration: Long
    ) {
        dataStore.edit { preferences ->
            preferences[BIOMETRICS_KEY] = biometricPromptEnable
            preferences[SESSION_EXPIRED_DURATION_KEY] = sessionExpiryDuration
            preferences[LOCKED_DURATION_KEY] = lockedOutDuration
        }
    }

    override suspend fun logout() {
        dataStore.edit {
            it.clear()
        }
    }

    override fun getSessionExpired(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[USER_ID_KEY]?.let { userId ->
                if (userId != -1) {
                    preferences[SESSION_EXPIRED_KEY] ?: false
                } else {
                    false
                }
            } ?: false
        }
    }

    override suspend fun updateLatestTimeStamp() {
        dataStore.edit {
            it[LATEST_TIMESTAMP_KEY] = SystemClock.elapsedRealtime()
        }
    }

    override suspend fun checkSessionExpired(): Boolean {
        val latestTimestamp = dataStore.data.map {
            it[LATEST_TIMESTAMP_KEY] ?: 0
        }.first()

        val timeStamp = SystemClock.elapsedRealtime()

        val sessionExpiryDuration = dataStore.data.map {
            it[SESSION_EXPIRED_DURATION_KEY] ?: 300000
        }.first()

        val isSessionExpired = timeStamp < latestTimestamp ||
                timeStamp > latestTimestamp.plus(sessionExpiryDuration)

        dataStore.edit {
            it[SESSION_EXPIRED_KEY] = isSessionExpired
        }
        return isSessionExpired
    }

    override suspend fun changeSession(value: Boolean) {
        dataStore.edit {
            it[SESSION_EXPIRED_KEY] = value
        }
    }

    private companion object {
        val USER_ID_KEY = intPreferencesKey("user_id")
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val EXPENSES_FORMAT_KEY = stringPreferencesKey("expenses_format")
        val CURRENCY_KEY = stringPreferencesKey("Currency")
        val DECIMAL_SEPARATOR_KEY = stringPreferencesKey("decimal_separator")
        val THOUSAND_SEPARATOR_KEY = stringPreferencesKey("thousand_separator")

        val BIOMETRICS_KEY = booleanPreferencesKey("biometrics")
        val SESSION_EXPIRED_DURATION_KEY = longPreferencesKey("session_expired_duration")
        val LOCKED_DURATION_KEY = longPreferencesKey("locked_duration")

        val LATEST_TIMESTAMP_KEY = longPreferencesKey("latest_timestamp")
        val SESSION_EXPIRED_KEY = booleanPreferencesKey("session_expired")
    }
}