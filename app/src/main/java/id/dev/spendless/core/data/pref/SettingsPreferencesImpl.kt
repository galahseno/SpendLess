package id.dev.spendless.core.data.pref

import android.os.SystemClock
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import id.dev.spendless.core.data.pref.model.PinPromptAttemptPref
import id.dev.spendless.core.data.pref.model.UserSecurityPref
import id.dev.spendless.core.data.pref.model.UserSessionPref
import id.dev.spendless.core.data.pref.model.toPinPromptAttempt
import id.dev.spendless.core.data.pref.model.toUserSecurity
import id.dev.spendless.core.data.pref.model.toUserSession
import id.dev.spendless.core.domain.SettingPreferences
import id.dev.spendless.core.domain.model.PinPromptAttempt
import id.dev.spendless.core.domain.model.UserSecurity
import id.dev.spendless.core.domain.model.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SettingPreferencesImpl(
    private val dataStore: DataStore<Preferences>,
    private val userSessionDataStore: DataStore<UserSessionPref>,
    private val userSecurityDataStore: DataStore<UserSecurityPref>,
    private val pinPromptDataStore: DataStore<PinPromptAttemptPref>
) : SettingPreferences {

    override fun getUserId(): Flow<Int> {
        return userSessionDataStore.data.map { it.userId }
    }

    override fun getUserSession(): Flow<UserSession> {
        return userSessionDataStore.data.map {
            it.toUserSession()
        }
    }

    override fun getUserSecurity(): Flow<UserSecurity> {
        return userSecurityDataStore.data.map {
            it.toUserSecurity()
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
        userSessionDataStore.updateData {
            it.copy(
                userId = userId,
                username = username,
                expensesFormat = expensesFormat,
                currencySymbol = currencySymbol,
                decimalSeparator = decimalSeparator,
                thousandSeparator = thousandSeparator
            )
        }
        updateLatestTimeStamp()
    }

    override suspend fun saveLoginSession(
        userId: Int,
        username: String,
    ) {
        userSessionDataStore.updateData {
            it.copy(
                userId = userId,
                username = username
            )
        }
        updateLatestTimeStamp()
    }

    override suspend fun updateUserSession(
        expensesFormat: String,
        currencySymbol: String,
        decimalSeparator: String,
        thousandSeparator: String
    ) {
        userSessionDataStore.updateData {
            it.copy(
                expensesFormat = expensesFormat,
                currencySymbol = currencySymbol,
                decimalSeparator = decimalSeparator,
                thousandSeparator = thousandSeparator
            )
        }
    }

    override suspend fun updateUserSecurity(
        biometricPromptEnable: Boolean,
        sessionExpiryDuration: Long,
        lockedOutDuration: Long
    ) {
        userSecurityDataStore.updateData {
            it.copy(
                biometricPromptEnable = biometricPromptEnable,
                sessionExpiryDuration = sessionExpiryDuration,
                lockedOutDuration = lockedOutDuration
            )
        }
        pinPromptDataStore.updateData {
            it.copy(
                lockedOutDuration = lockedOutDuration
            )
        }
    }

    override suspend fun logout() {
        dataStore.edit { it.clear() }
        userSessionDataStore.updateData { UserSessionPref() }
        userSecurityDataStore.updateData { UserSecurityPref() }
        pinPromptDataStore.updateData { PinPromptAttemptPref() }
    }

    override fun getSessionExpired(): Flow<Boolean> {
        val userId = userSessionDataStore.data.map { it.userId }
        return dataStore.data.map { preferences ->
            if (userId.first() != -1) {
                preferences[SESSION_EXPIRED_KEY] ?: false
            } else {
                false
            }
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
        val sessionExpiryDuration = userSecurityDataStore.data.map {
            it.sessionExpiryDuration
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

    override fun getPinPromptAttempt(): Flow<PinPromptAttempt> {
        return pinPromptDataStore.data.map {
            it.toPinPromptAttempt()
        }
    }

    override suspend fun resetPinPromptAttempt(lockedOutDuration: Long) {
        pinPromptDataStore.updateData {
            PinPromptAttemptPref(lockedOutDuration = lockedOutDuration)
        }
    }

    override suspend fun updateFailedAttempt(value: Int) {
        pinPromptDataStore.updateData { it.copy(failedAttempt = value) }
    }

    override suspend fun updateMaxAttemptPinPrompt(value: Boolean) {
        pinPromptDataStore.updateData { it.copy(maxFailedAttempt = value) }
    }

    override suspend fun updateLatestDuration(value: Long) {
        pinPromptDataStore.updateData { it.copy(lockedOutDuration = value) }
    }

    override suspend fun changeAddBottomSheetValue(value: Boolean) {
        userSessionDataStore.updateData { it.copy(addBottomSheetState = value) }
    }

    override fun getBottomSheetValue(): Flow<Boolean> {
        return userSessionDataStore.data.map { it.addBottomSheetState }
    }

    private companion object {
        val LATEST_TIMESTAMP_KEY = longPreferencesKey("latest_timestamp")
        val SESSION_EXPIRED_KEY = booleanPreferencesKey("session_expired")
    }
}